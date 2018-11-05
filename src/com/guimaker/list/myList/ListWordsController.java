package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.*;
import com.guimaker.list.loadAdditionalWordsHandling.FoundWordInsideVisibleRangePlusMaximumWordsStrategy;
import com.guimaker.list.loadAdditionalWordsHandling.FoundWordInsideVisibleRangeStrategy;
import com.guimaker.list.loadAdditionalWordsHandling.FoundWordOutsideRangeStrategy;
import com.guimaker.list.loadAdditionalWordsHandling.LoadWordsForFoundWord;
import com.guimaker.list.myList.panel.ListViewManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.FilteredWordMatch;
import com.guimaker.model.ListRow;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.MainPanel;
import com.guimaker.strings.Prompts;
import com.guimaker.swingUtilities.ProgressUpdater;
import com.guimaker.utilities.Pair;
import com.guimaker.utilities.Range;
import com.guimaker.utilities.ThreadUtilities;
import com.guimaker.utilities.WordSearching;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class ListWordsController<Word extends ListElement> {
	private List<ListRow<Word>> allWordsToRowNumberMap = new ArrayList<>();
	private ListViewManager<Word> listViewManager;
	private ApplicationChangesManager applicationChangesManager;
	private final int MAXIMUM_WORDS_TO_SHOW = 201;
	private int lastRowVisible = -1;
	private final List<LoadWordsForFoundWord> strategiesForFoundWord = new ArrayList<>();
	private ListRow<Word> currentlyHighlightedWord;
	private ListElementInitializer<Word> wordInitializer;
	private List<SwitchBetweenInputsFailListener> switchBetweenInputsFailListeners = new ArrayList<>();
	private ProgressUpdater progressUpdater;
	private Set<ListObserver<Word>> observers = new HashSet<>();
	private Pair<MyList, ListElement> parentListAndWord;
	private boolean finishEditActionRequested;
	private boolean isInEditMode;
	private final int numberOfWordsToDisplayByFilter = 10;
	private String wordSpecificPrompt;
	private ListFilterHandler listFilterHandler;
	private MyList<Word> myList;
	//TODO switchBetweenInputsFailListeners should be deleted from here

	public ListWordsController(ListConfiguration listConfiguration,
			ListRowCreator<Word> listRowCreator, String title,
			ApplicationChangesManager applicationChangesManager,
			ListElementInitializer<Word> wordInitializer, MyList<Word> myList) {
		//TODO do not pass around list row creator, title etc, they are
		// already contained in list configuration
		this.myList = myList;
		this.wordInitializer = wordInitializer;
		wordSpecificPrompt = listConfiguration.getWordSpecificDeletePrompt();
		parentListAndWord = listConfiguration.getParentListAndWordContainingThisList();
		progressUpdater = new ProgressUpdater();
		this.applicationChangesManager = applicationChangesManager;
		listViewManager = new ListViewManager<>(listConfiguration,
				applicationChangesManager, listRowCreator, this, title);
		listFilterHandler = new ListFilterHandler(this);
		initializeFoundWordStrategies();
	}

	public ListElementInitializer<Word> getWordInitializer() {
		return wordInitializer;
	}

	public void inheritScrollPane() {
		listViewManager.inheritScrollPane();
	}

	private void initializeFoundWordStrategies() {
		strategiesForFoundWord.add(new FoundWordInsideVisibleRangeStrategy());
		strategiesForFoundWord.add(
				new FoundWordInsideVisibleRangePlusMaximumWordsStrategy(
						MAXIMUM_WORDS_TO_SHOW, this,
						listViewManager.getLoadPreviousWordsHandler(),
						listViewManager.getLoadNextWordsHandler()));
		strategiesForFoundWord.add(
				new FoundWordOutsideRangeStrategy(MAXIMUM_WORDS_TO_SHOW, this));
	}

	public int getMaximumWordsToShow() {
		return MAXIMUM_WORDS_TO_SHOW;
	}

	public boolean add(Word r, InputGoal inputGoal, boolean tryToShowWord) {
		return add(r, inputGoal, tryToShowWord, true);
	}

	public boolean add(Word r, InputGoal inputGoal, boolean tryToShowWord,
			boolean validate) {

		if (r == null) {
			return false;
		}
		boolean valid = !validate || !isWordDefined(r).exists();
		if (valid) {
			boolean canNewWordBeDisplayed = canNewWordBeDisplayed();
			if (tryToShowWord) {
				if (!lastWordIsVisible()) {
					loadLastWord();
				}
				else if (!canNewWordBeDisplayed) {
					removeFirstRow();
					canNewWordBeDisplayed = true;
				}
			}

			ListRow<Word> newWord = listViewManager.addRow(r,
					allWordsToRowNumberMap.size() + 1,
					canNewWordBeDisplayed && tryToShowWord,
					listViewManager.getLoadNextWordsHandler(), inputGoal);
			allWordsToRowNumberMap.add(newWord);
			if (canNewWordBeDisplayed && tryToShowWord) {
				lastRowVisible = allWordsToRowNumberMap.size() - 1;
			}

			return true;
		}
		return false;
	}

	private void removeFirstRow() {
		ListRow<Word> rowToRemove = allWordsToRowNumberMap.get(
				getFirstVisibleRowNumber());
		listViewManager.removeRow(rowToRemove.getJPanel());
		rowToRemove.setPanel(null);
	}

	private void loadLastWord() {
		showWordsStartingFromRow(
				allWordsToRowNumberMap.size() - 1 - getMaximumWordsToShow());
	}

	private boolean lastWordIsVisible() {
		return lastRowVisible == allWordsToRowNumberMap.size() - 1;
	}

	private boolean canNewWordBeDisplayed() {
		return listViewManager.getNumberOfListRows() < MAXIMUM_WORDS_TO_SHOW;
	}

	public void remove(Word word) {
		if (!observers.isEmpty() && parentListAndWord != null) {
			throw new IllegalStateException(
					"A child list cannot have observers, "
							+ "only parent list is allowed to have them");
		}
		if (parentListAndWord != null) {
			parentListAndWord.getLeft()
							 .updateObservers(parentListAndWord.getRight(),
									 ListElementModificationType.EDIT);
		}
		else {
			observers.forEach(list -> list.update(word,
					ListElementModificationType.DELETE));
		}

		ListRow<Word> listRow = findListRowContainingWord(word);
		if (listRow == null) {
			return;
		}
		listViewManager.removeRow(listRow.getJPanel());
		int indexOfRemovedWord = allWordsToRowNumberMap.indexOf(listRow);
		allWordsToRowNumberMap.remove(listRow);
		updateRowNumbers(indexOfRemovedWord);
		if (currentlyHighlightedWord == listRow) {
			currentlyHighlightedWord = null;
		}
		if (allWordsToRowNumberMap.isEmpty()) {
			listViewManager.addElementsForEmptyList();
		}

	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex; i < allWordsToRowNumberMap.size(); i++) {
			ListRow<Word> listRow = allWordsToRowNumberMap.get(i);
			listRow.decrementRowNumber();
			JLabel label = listRow.getIndexLabel();
			label.setText(listViewManager.createTextForRowNumber(i + 1));
		}

	}

	private ListRow<Word> findListRowContainingWord(Word r) {
		for (int i = 0; i < allWordsToRowNumberMap.size(); i++) {
			Word word = allWordsToRowNumberMap.get(i)
											  .getWord();
			if (word.equals(r)) {
				return allWordsToRowNumberMap.get(i);
			}
		}
		return null;
	}

	public List<Word> getWords() {
		List<Word> words = new ArrayList<>();
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			words.add(listRow.getWord());
		}
		return words;
	}

	private List<ListRow<Word>> getWordsWithDetails() {
		List<ListRow<Word>> listRows = new ArrayList<>();
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			listRows.add(listRow);
		}
		return listRows;

	}

	public int getNumberOfWords() {
		return allWordsToRowNumberMap.size();
	}

	public Word getWordInRow(int rowNumber1Based) {
		return allWordsToRowNumberMap.get(rowNumber1Based)
									 .getWord();
	}

	public void highlightRowAndScroll(int rowNumber,
			boolean clearLastHighlightedWord) {
		if (rowNumber == -1) {
			return;
		}
		loadWordsIfNecessary(rowNumber);
		ListRow<Word> foundWord = allWordsToRowNumberMap.get(rowNumber);
		if (!foundWord.isShowing()) {
			return;
		}
		foundWord.setHighlighted(true);
		if (clearLastHighlightedWord && currentlyHighlightedWord != null) {
			listViewManager.clearHighlightedRow(
					currentlyHighlightedWord.getJPanel());
		}
		listViewManager.highlightRowAndScroll(foundWord.getJPanel());
		currentlyHighlightedWord = foundWord;
	}

	public void clearHighlightedWords() {
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			if (listRow.isHighlighted()) {
				listViewManager.clearHighlightedRow(listRow.getJPanel());
			}
		}
	}

	private void loadWordsIfNecessary(int foundWordRowNumber) {
		for (LoadWordsForFoundWord strategyForFoundWord : strategiesForFoundWord) {
			if (strategyForFoundWord.isApplicable(foundWordRowNumber,
					new Range(getFirstVisibleRowNumber(), lastRowVisible))) {
				strategyForFoundWord.execute();
				break;
			}
		}
	}

	public Integer getHighlightedRowNumber() {
		return currentlyHighlightedWord != null ?
				allWordsToRowNumberMap.stream()
									  .filter(e -> e.equals(
											  currentlyHighlightedWord))
									  .map(e -> allWordsToRowNumberMap.indexOf(
											  e))
									  .findFirst()
									  .orElseThrow(
											  IllegalArgumentException::new) :
				-1;
	}

	public void scrollToBottom() {
		loadWordsIfNecessary(allWordsToRowNumberMap.size() - 1);
		listViewManager.scrollToBottom();
	}

	public JPanel getPanel() {
		return listViewManager.getPanel();
	}

	public void clear() {
		allWordsToRowNumberMap.clear();
		listViewManager.clear();
		lastRowVisible = 0;
	}

	public WordInMyListExistence<Word> isWordDefined(Word word) {
		if (word.isEmpty()) {
			return new WordInMyListExistence<>(false, null, 0);
		}
		for (int i = 0; i < allWordsToRowNumberMap.size(); i++) {
			ListRow<Word> listRow = allWordsToRowNumberMap.get(i);
			if (listRow.getWord()
					   .equals(word)) {
				return new WordInMyListExistence<>(true, listRow.getWord(),
						i + 1);
			}
		}
		return new WordInMyListExistence<>(false, null, -1);
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			private static final long serialVersionUID = 5946111397005824819L;

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!applicationChangesManager.getApplicationWindow()
											  .showConfirmDialog(String.format(
													  Prompts.DELETE_ELEMENT,
													  wordSpecificPrompt))) {
					return;
				}
				remove(word);
				applicationChangesManager.save();
			}
		};
	}

	public List<Word> getWordsByHighlight(boolean highlighted) {
		List<Word> highlightedWords = new ArrayList<>();
		for (ListRow<Word> word : allWordsToRowNumberMap) {
			if (word.isHighlighted() == highlighted) {
				highlightedWords.add(word.getWord());
			}
		}
		return highlightedWords;
	}

	public void scrollToTop() {
		listViewManager.scrollToTop();
	}

	public int addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler) {
		double numberOfElementsToAdd =
				(double) getMaximumWordsToShow() / (double) 2;
		return addSuccessiveWords(loadWordsHandler, numberOfElementsToAdd);
	}

	public int addSuccessiveWords(LoadWordsHandler loadWordsHandler,
			double numberOfElementsToAdd) {
		int i = 0;
		while (i < numberOfElementsToAdd && loadWordsHandler.shouldContinue(
				lastRowVisible, allWordsToRowNumberMap.size())) {
			loadWordsHandler.addWord();
			i++;
		}
		return i;
	}

	public int getFirstVisibleRowNumber() {
		return lastRowVisible - MAXIMUM_WORDS_TO_SHOW + 1;
	}

	public void showPreviousWord(LoadPreviousWordsHandler loadPreviousWords) {
		//TODO lots of magic numbers
		lastRowVisible--;
		int rowNumber = getFirstVisibleRowNumber();
		ListRow<Word> addedWord = listViewManager.addRow(
				allWordsToRowNumberMap.get(rowNumber)
									  .getWord(), rowNumber + 1, true,
				loadPreviousWords, InputGoal.EDIT);
		allWordsToRowNumberMap.set(rowNumber, addedWord);

	}

	public void showNextWord(LoadNextWordsHandler loadNextWords) {
		lastRowVisible++;
		ListRow<Word> wordListRow = allWordsToRowNumberMap.get(lastRowVisible);
		ListRow<Word> visibleRow = listViewManager.addRow(
				wordListRow.getWord(), lastRowVisible + 1, true, loadNextWords,
				listViewManager.getInputGoal());
		if (wordListRow.isHighlighted()) {
			listViewManager.highlightRow(visibleRow.getJPanel());
		}
		allWordsToRowNumberMap.set(lastRowVisible, visibleRow);
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listViewManager.clear();
		if (firstRowToLoad > 0) {
			listViewManager.enableButtonShowPreviousWords();
		}
		lastRowVisible = Math.max(firstRowToLoad - 1, -1);
		LoadNextWordsHandler loadNextWordsHandler = listViewManager.getLoadNextWordsHandler();
		for (int i = 0;
			 i < getMaximumWordsToShow() && loadNextWordsHandler.shouldContinue(
					 lastRowVisible, allWordsToRowNumberMap.size() - 1); i++) {
			showNextWord(loadNextWordsHandler);
			//TODO do not pass around load words handler, use some enum: next/previous word
			progressUpdater.updateProgress();
		}

	}

	public void clearVisibleRows() {
		listViewManager.removeWordsFromRangeInclusive(
				new Range(1, listViewManager.getNumberOfListRows()));
	}

	public void removeRowsFromRangeInclusive(Range range) {
		listViewManager.removeWordsFromRangeInclusive(range);
	}

	public AbstractAction addNewWord(InputGoal inputGoal) {
		return new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				add(wordInitializer.initializeElement(), inputGoal, true);
				if (parentListAndWord != null) {
					parentListAndWord.getLeft()
									 .updateObservers(
											 parentListAndWord.getRight(),
											 ListElementModificationType.EDIT);
				}
			}
		};

	}

	public MainPanel getPanelWithSelectedInput() {
		ListRow<Word> rowWithSelectedInput = getRowWithSelectedInput();
		if (rowWithSelectedInput != null) {
			return rowWithSelectedInput.getWrappingPanel();
		}
		else {
			return findFirstVisiblePanelInScrollPane();
		}
	}

	public void addSwitchBetweenInputsFailListener(
			SwitchBetweenInputsFailListener listener) {
		switchBetweenInputsFailListeners.add(listener);
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			listRow.getWrappingPanel()
				   .addSwitchBetweenInputsFailedListener(listener);
		}
	}

	public ListRow<Word> getRowWithSelectedInput() {
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			if (listRow.getWrappingPanel()
					   .hasSelectedInput()) {
				return listRow;
			}
		}
		return null;
	}

	public void selectPanelBelowOrAboveSelected(MoveDirection moveDirection) {
		//TODO this should also be handled in main panel in his selection manager
		// -> in order for this to be possible, all list rows should be contained in one
		// main panel, currently for each row theres new main panel created
		ListRow<Word> selectedRow = getRowWithSelectedInput();
		if (selectedRow == null) {
			MainPanel firstVisiblePanel = findFirstVisiblePanelInScrollPane();
			firstVisiblePanel.selectNextInputInSameRow();
			return;
		}
		int rowNumberOfSelectedPanel = selectedRow.getRowNumber();
		int columnNumber = selectedRow.getWrappingPanel()
									  .getSelectedInputIndex();
		MainPanel panelBelowOrAbove = null;
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			if (listRow.getRowNumber() == rowNumberOfSelectedPanel
					+ moveDirection.getIncrementValue()) {
				panelBelowOrAbove = listRow.getWrappingPanel();
			}
		}
		if (panelBelowOrAbove != null) {
			panelBelowOrAbove.selectInputInColumn(columnNumber);
			listViewManager.scrollTo(panelBelowOrAbove.getPanel());
		}
		else {
			switchBetweenInputsFailListeners.forEach(
					listener -> listener.switchBetweenInputsFailed(
							selectedRow.getWrappingPanel()
									   .getSelectedInput(), moveDirection));
		}
	}

	public void toggleEnabledState() {
		listViewManager.toggleEnabledState();
	}

	public MainPanel findFirstVisiblePanelInScrollPane() {
		for (ListRow<Word> row : allWordsToRowNumberMap) {
			MainPanel wrappingPanel = row.getWrappingPanel();
			if (!wrappingPanel.getPanel()
							  .getVisibleRect()
							  .isEmpty()) {
				return wrappingPanel;
			}
		}
		return null;
	}

	public void addWords(List<Word> words, InputGoal inputGoal,
			boolean tryToShowWords, boolean validate) {
		for (Word word : words) {
			add(word, inputGoal, tryToShowWords, validate);
		}
	}

	public ProgressUpdater getProgressUpdater() {
		return progressUpdater;
	}

	public AbstractAction createEditWordAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isInEditMode = true;
				repaint(word, InputGoal.EDIT_TEMPORARILY);
			}
		};
	}

	public AbstractAction createFinishEditAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isInEditMode = false;
				Component focusOwner = FocusManager.getCurrentManager()
												   .getFocusOwner();
				if (focusOwner instanceof JTextComponent) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
										.clearGlobalFocusOwner();
					finishEditActionRequested = true;
				}
				else {
					repaintWordAndHighlightIfNeeded(word);
				}

			}
		};
	}

	private void repaintWordAndHighlightIfNeeded(Word word) {
		repaint(word);
		if (getWordsByHighlight(true).contains(word)) {
			highlightRowAndScroll(get0BasedRowNumberOfWord(word), false);
		}
	}

	public int get0BasedRowNumberOfWord(Word word) {
		return getWords().indexOf(word);
	}

	public void addObserver(ListObserver<Word> listObserver) {
		observers.add(listObserver);
	}

	public void updateObservers(Word word,
			ListElementModificationType modificationType) {
		observers.forEach(observer -> observer.update(word, modificationType));
	}

	public void repaint(Word word) {
		repaint(word, isInEditMode ? InputGoal.EDIT_TEMPORARILY : null);
	}

	public void repaint(Word word, InputGoal inputGoal) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		if (listRow == null || !listRow.isShowing()) {
			return;
		}
		MainPanel panel = listViewManager.repaintWord(word,
				listRow.getRowNumber(), listRow.getJPanel(), inputGoal,
				listRow.isHighlighted());

		listRow.setPanel(panel);
	}

	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		if (finishEditActionRequested && postValidationData.isValid()) {
			repaintWordAndHighlightIfNeeded(
					postValidationData.getValidatedWord());
		}
		finishEditActionRequested = false;
	}

	public boolean isInEditMode() {
		return isInEditMode;
	}

	public boolean isFilterInputFocused() {
		return listViewManager.isFilterInputFocused();
	}

	public AbstractAction createActionClearFilter() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ThreadUtilities.callOnOtherThread(
						() -> showWordsStartingFromRow(
								getFirstVisibleRowNumber()));
			}
		};
	}

	public DocumentListener createActionFilterImmediately() {
		return new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				listFilterHandler.scheduleFiltering();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				listFilterHandler.scheduleFiltering();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		};
	}

	public void filterWords() {
		String text = listViewManager.getFilterComponent()
									 .getText();
		ListElementPropertyManager filterInputPropertyManager = listViewManager.getFilterInputPropertyManager();
		SortedMap<FilteredWordMatch, ListRow<Word>> words = WordSearching.filterWords(
				getWordsWithDetails(), text, filterInputPropertyManager);
		listViewManager.clear();

		int newRowNumber = 1;
		for (ListRow<Word> listRow : words.values()) {
			if (newRowNumber > numberOfWordsToDisplayByFilter) {
				break;
			}
			int rowNumber = listRow.getRowNumber() - 1;
			listRow.setPanel(listViewManager.addRow(
					allWordsToRowNumberMap.get(rowNumber)
										  .getWord(), newRowNumber++, true,
					listViewManager.getLoadNextWordsHandler(),
					listViewManager.getInputGoal())
											.getWrappingPanel());
			if (listRow.isHighlighted()) {
				listViewManager.highlightRow(listRow.getJPanel());
			}
		}
		scrollToTop();
	}

	public AbstractAction createActionShowInsertWordDialog() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationWindow applicationWindow = applicationChangesManager.getApplicationWindow();
				applicationWindow.showInsertWordDialog(myList,
						applicationWindow.getApplicationConfiguration()
										 .getInsertWordPanelPositioner());
			}
		};
	}
}