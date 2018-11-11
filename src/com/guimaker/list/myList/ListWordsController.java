package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementInitializer;
import com.guimaker.list.ListObserver;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.list.loadAdditionalWordsHandling.*;
import com.guimaker.list.myList.panel.ListViewManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.ListRow;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.MainPanel;
import com.guimaker.strings.Prompts;
import com.guimaker.swingUtilities.ProgressUpdater;
import com.guimaker.utilities.Pair;
import com.guimaker.utilities.Range;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.FocusManager;
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
	private String wordSpecificPrompt;
	private MyList<Word> myList;
	//TODO switchBetweenInputsFailListeners should be deleted from here

	public ListWordsController(ListConfiguration<Word> listConfiguration,
			MyList<Word> myList) {
		this.myList = myList;
		this.wordInitializer = listConfiguration.getListElementInitializer();
		wordSpecificPrompt = listConfiguration.getWordSpecificDeletePrompt();
		parentListAndWord = listConfiguration.getParentListAndWordContainingThisList();
		progressUpdater = new ProgressUpdater();
		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		listViewManager = new ListViewManager<>(listConfiguration, this);
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
					listViewManager.enableButtonShowPreviousWords();
				}
			}

			ListRow<Word> newWord = listViewManager.addRow(r,
					allWordsToRowNumberMap.size() + 1,
					canNewWordBeDisplayed && tryToShowWord,
					ListWordsLoadingDirection.NEXT, inputGoal);
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

	public List<ListRow<Word>> getWordsWithDetails() {
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

	public AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numberOfListRows = listViewManager.getNumberOfListRows();
				listViewManager.clearRowsPanel();
				addNextHalfOfMaximumWords(loadWordsHandler, numberOfListRows);
				boolean shouldDisable = loadWordsHandler.shouldDisableLoadWordsButton(
						ListWordsController.this);
				listViewManager.enableOrDisableLoadWordsButton(shouldDisable,
						loadWordsHandler.getDirection());

				listViewManager.updateRowsPanel();
			}
		};

	}

	public void addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler,
			int numberOfListRows) {
		addSuccessiveWords(loadWordsHandler, MAXIMUM_WORDS_TO_SHOW,
				numberOfListRows);
	}

	public void addSuccessiveWords(LoadWordsHandler loadWordsHandler,
			int numberOfElementsToAdd, int numberOfListRows) {
		int i = 0;
		recalculateLastRowVisible(loadWordsHandler, numberOfListRows);
		while (i < numberOfElementsToAdd && loadWordsHandler.shouldContinue(
				lastRowVisible, allWordsToRowNumberMap.size())) {
			showNextWord();
			i++;
		}
		lastRowVisible--;
	}

	private void recalculateLastRowVisible(LoadWordsHandler loadWordsHandler,
			int numberOfListRows) {
		if (loadWordsHandler instanceof LoadNextWordsHandler) {
			lastRowVisible++;
		}
		else {
			lastRowVisible = Math.max(
					lastRowVisible - numberOfListRows - MAXIMUM_WORDS_TO_SHOW
							+ 1, 0);
		}
	}

	public int getFirstVisibleRowNumber() {
		return lastRowVisible - listViewManager.getNumberOfListRows() + 1;
	}

	public void showNextWord() {

		ListRow<Word> wordListRow = allWordsToRowNumberMap.get(lastRowVisible);
		ListRow<Word> visibleRow = listViewManager.addRow(wordListRow.getWord(),
				lastRowVisible + 1, true, ListWordsLoadingDirection.NEXT,
				listViewManager.getInputGoal());
		if (wordListRow.isHighlighted()) {
			listViewManager.highlightRow(visibleRow.getJPanel());
		}
		allWordsToRowNumberMap.set(lastRowVisible, visibleRow);
		lastRowVisible++;
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listViewManager.clear();
		if (firstRowToLoad > MAXIMUM_WORDS_TO_SHOW) {
			listViewManager.enableButtonShowPreviousWords();
		}
		lastRowVisible = Math.max(firstRowToLoad - 1, -1);
		LoadNextWordsHandler loadNextWordsHandler = listViewManager.getLoadNextWordsHandler();
		for (int i = 0;
			 i < getMaximumWordsToShow() && loadNextWordsHandler.shouldContinue(
					 lastRowVisible, allWordsToRowNumberMap.size()); i++) {
			showNextWord();
			//TODO do not pass around load words handler, use some enum: next/previous word
			progressUpdater.updateProgress();
		}
		lastRowVisible--;
		listViewManager.updateRowsPanel();

	}

	public void removeRowsFromRangeInclusive(Range range) {
		listViewManager.removeWordsFromRangeInclusive(range);
	}

	public AbstractAction createActionAddNewWord(InputGoal inputGoal) {
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

				listViewManager.updateRowsPanel();
				requestFirstTextfieldInPanel();

			}
		};

	}

	private void requestFirstTextfieldInPanel() {
		JComponent jPanel = allWordsToRowNumberMap.get(
				allWordsToRowNumberMap.size() - 1)
												  .getJPanel();
		if (jPanel.getComponents().length == 1
				&& jPanel.getComponents()[0] instanceof JPanel) {
			JPanel panel = (JPanel) jPanel.getComponents()[0];
			Optional<Component> firstTextField = Arrays.stream(
					panel.getComponents())
													   .filter(JTextComponent.class::isInstance)
													   .findFirst();
			SwingUtilities.invokeLater(() -> firstTextField.ifPresent(
					Component::requestFocusInWindow));
		}
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
								getStartOfRangeOfDisplayedWords()));
			}
		};
	}

	private int getStartOfRangeOfDisplayedWords() {
		return lastRowVisible - MAXIMUM_WORDS_TO_SHOW + 2;
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

	public boolean isLastRowVisible() {
		return lastRowVisible == allWordsToRowNumberMap.size() - 1;
	}

	public void showWord(Word word) {
		listViewManager.clear();
		lastRowVisible = get0BasedRowNumberOfWord(word);
		showNextWord();
		lastRowVisible--;

	}
}