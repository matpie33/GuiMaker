package com.guimaker.list.myList;

import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementInitializer;
import com.guimaker.list.ListObserver;
import com.guimaker.list.loadAdditionalWordsHandling.*;
import com.guimaker.list.myList.panel.ListViewManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.ListRow;
import com.guimaker.model.ParentListData;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.panels.MainPanel;
import com.guimaker.swingUtilities.ProgressUpdater;
import com.guimaker.utilities.Range;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListWordsController<Word extends ListElement> {

	private ListViewManager<Word> listViewManager;
	private final int MAXIMUM_WORDS_TO_SHOW = 201;
	private int lastRowVisible = -1;
	private final List<LoadWordsForFoundWord> strategiesForFoundWord = new ArrayList<>();
	private ListRow<Word> currentlyHighlightedWord;
	private ListElementInitializer<Word> wordInitializer;
	private List<SwitchBetweenInputsFailListener> switchBetweenInputsFailListeners = new ArrayList<>();
	private ProgressUpdater progressUpdater;
	private Set<ListObserver<Word>> observers = new HashSet<>();
	private ParentListData<?, Word> parentListAndWord;
	private boolean finishEditActionRequested;
	private boolean isInEditMode;
	private ListWordsHolder<Word> listWordsHolder;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;

	private MyList<Word> myList;
	//TODO switchBetweenInputsFailListeners should be deleted from here

	public ListWordsController(InsertWordPanel<Word> insertWordPanel,
			ListConfiguration<Word> listConfiguration, MyList<Word> myList,
			ListWordsHolder<Word> listWordsHolder) {
		this.myList = myList;
		this.wordInitializer = listConfiguration.getListElementInitializer();
		parentListAndWord = listConfiguration.getParentListAndWordContainingThisList();
		progressUpdater = new ProgressUpdater();
		listViewManager = new ListViewManager<>(insertWordPanel,
				listConfiguration, this);
		this.listWordsHolder = listWordsHolder;
		loadNextWordsHandler = new LoadNextWordsHandler();
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(this,
				listViewManager.getRowsPanel());
		initializeFoundWordStrategies();
	}

	public ListWordsHolder<Word> getListWordsHolder() {
		return listWordsHolder;
	}

	public ListElementInitializer<Word> getWordInitializer() {
		return wordInitializer;
	}

	private void initializeFoundWordStrategies() {
		strategiesForFoundWord.add(new FoundWordInsideVisibleRangeStrategy());
		strategiesForFoundWord.add(
				new FoundWordInsideVisibleRangePlusMaximumWordsStrategy(
						MAXIMUM_WORDS_TO_SHOW, this, loadPreviousWordsHandler,
						loadNextWordsHandler, listWordsHolder));
		strategiesForFoundWord.add(
				new FoundWordOutsideRangeStrategy(MAXIMUM_WORDS_TO_SHOW, this,
						listWordsHolder));
	}

	public int getMaximumWordsToShow() {
		return MAXIMUM_WORDS_TO_SHOW;
	}

	public boolean add(Word r, InputGoal inputGoal, boolean tryToShowWord) {
		return add(r, inputGoal, tryToShowWord, true);
	}

	public boolean add(Word r, InputGoal inputGoal, boolean tryToShowWord,
			boolean validate) {

		if (r == null || (validate && listWordsHolder.isWordDefined(r)
													 .exists())) {
			return false;
		}
		boolean canNewWordBeDisplayed = canNewWordBeDisplayed();
		if (tryToShowWord && !canNewWordBeDisplayed) {
			removeFirstRow();
			canNewWordBeDisplayed = true;
			listViewManager.enableButtonShowPreviousWords();
		}

		if (parentListAndWord != null) {
			parentListAndWord.addElement(r);
		}
		ListRow<Word> newWord = listViewManager.addRow(r,
				listWordsHolder.getNumberOfWords() + 1,
				canNewWordBeDisplayed && tryToShowWord,
				ListWordsLoadingDirection.NEXT, inputGoal);
		listWordsHolder.add(newWord);
		if (canNewWordBeDisplayed && tryToShowWord) {
			lastRowVisible = listWordsHolder.getNumberOfWords() - 1;
		}

		return true;
	}

	private void removeFirstRow() {
		ListRow<Word> rowToRemove = listWordsHolder.getWordInRow0Based(
				getFirstVisibleRowNumber());
		listViewManager.removeRow(rowToRemove.getJPanel());
		rowToRemove.setPanel(null);
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
			parentListAndWord.updateObservers(
					ListElementModificationType.DELETE);
			parentListAndWord.removeElement(word);
		}
		else {
			observers.forEach(list -> list.update(word,
					ListElementModificationType.DELETE));
		}

		ListRow<Word> listRow = listWordsHolder.findListRowContainingWord(word);
		if (listRow == null) {
			return;
		}
		listViewManager.removeRow(listRow.getJPanel());
		int indexOfRemovedWord = listWordsHolder.getIndexOfWord(listRow);
		listWordsHolder.remove(listRow);
		updateRowNumbers(indexOfRemovedWord);
		if (currentlyHighlightedWord == listRow) {
			currentlyHighlightedWord = null;
		}
		if (listWordsHolder.hasNoWords()) {
			listViewManager.addElementsForEmptyList();
		}

	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex;
			 i < listWordsHolder.getNumberOfWords(); i++) {
			ListRow<Word> listRow = listWordsHolder.getWordInRow0Based(i);
			listRow.decrementRowNumber();
			JLabel label = listRow.getIndexLabel();
			label.setText(listViewManager.createTextForRowNumber(i + 1));
		}

	}

	public void highlightRowAndScroll(int rowNumber,
			boolean clearLastHighlightedWord) {
		if (rowNumber == -1) {
			return;
		}
		loadWordsIfNecessary(rowNumber);
		ListRow<Word> foundWord = listWordsHolder.getWordInRow0Based(rowNumber);
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
		for (ListRow<Word> listRow : listWordsHolder.getWordsWithDetails()) {
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

	public void scrollToBottom() {
		loadWordsIfNecessary(listWordsHolder.getNumberOfWords() - 1);
		listViewManager.scrollToBottom();
	}

	public JPanel getPanel() {
		return listViewManager.getPanel();
	}

	public void clear() {
		listWordsHolder.clearWords();
		listViewManager.clear();
		lastRowVisible = 0;
	}

	public void scrollToTop() {
		listViewManager.scrollToTop();
	}

	public void showNextOrPreviousWords(
			ListWordsLoadingDirection loadingDirection) {
		int numberOfListRows = listViewManager.getNumberOfListRows();
		listViewManager.clearRowsPanel();
		LoadWordsHandler loadWordsHandler = getLoadWordsHandler(
				loadingDirection);
		addNextHalfOfMaximumWords(loadWordsHandler, numberOfListRows);
		boolean shouldDisable = loadWordsHandler.shouldDisableLoadWordsButton(
				ListWordsController.this);
		listViewManager.enableOrDisableLoadWordsButton(shouldDisable,
				loadWordsHandler.getDirection());

		listViewManager.updateRowsPanel();
	}

	private LoadWordsHandler getLoadWordsHandler(
			ListWordsLoadingDirection loadingDirection) {
		if (loadingDirection.equals(ListWordsLoadingDirection.NEXT)) {
			return loadNextWordsHandler;
		}
		else {
			return loadPreviousWordsHandler;
		}
	}

	private void addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler,
			int numberOfListRows) {
		addSuccessiveWords(loadWordsHandler, MAXIMUM_WORDS_TO_SHOW,
				numberOfListRows);
	}

	public void addSuccessiveWords(LoadWordsHandler loadWordsHandler,
			int numberOfElementsToAdd, int numberOfListRows) {
		int i = 0;
		recalculateLastRowVisible(loadWordsHandler, numberOfListRows);
		while (i < numberOfElementsToAdd && loadWordsHandler.shouldContinue(
				lastRowVisible, listWordsHolder.getNumberOfWords())) {
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

	private void showNextWord() {

		ListRow<Word> wordListRow = listWordsHolder.getWordInRow0Based(
				lastRowVisible);
		ListRow<Word> visibleRow = listViewManager.addRow(wordListRow.getWord(),
				lastRowVisible + 1, true, ListWordsLoadingDirection.NEXT,
				listViewManager.getInputGoal());
		if (wordListRow.isHighlighted()) {
			listViewManager.highlightRow(visibleRow.getJPanel());
		}
		listWordsHolder.setWordInRow0Based(lastRowVisible, visibleRow);
		lastRowVisible++;
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listViewManager.clear();
		if (firstRowToLoad > MAXIMUM_WORDS_TO_SHOW) {
			listViewManager.enableButtonShowPreviousWords();
		}
		lastRowVisible = Math.max(firstRowToLoad - 1, 0);
		for (int i = 0;
			 i < getMaximumWordsToShow() && loadNextWordsHandler.shouldContinue(
					 lastRowVisible, listWordsHolder.getNumberOfWords()); i++) {
			showNextWord();
			progressUpdater.updateProgress();
		}
		lastRowVisible--;
		listViewManager.updateRowsPanel();

	}

	public void removeRowsFromRangeInclusive(Range range) {
		listViewManager.removeWordsFromRangeInclusive(range);
	}

	public void focusFirstTextfieldInPanel() {
		JComponent jPanel = listWordsHolder.getWordInRow0Based(
				listWordsHolder.getNumberOfWords() - 1)
										   .getJPanel();
		listViewManager.focusFirstTextfieldInPanel(jPanel);

	}

	public MainPanel getPanelWithSelectedInput() {
		ListRow<Word> rowWithSelectedInput = listWordsHolder.getRowWithSelectedInput();
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
		for (ListRow<Word> listRow : listWordsHolder.getWordsWithDetails()) {
			listRow.getWrappingPanel()
				   .addSwitchBetweenInputsFailedListener(listener);
		}
	}

	public void selectPanelBelowOrAboveSelected(MoveDirection moveDirection) {
		//TODO this should also be handled in main panel in his selection manager
		// -> in order for this to be possible, all list rows should be contained in one
		// main panel, currently for each row theres new main panel created
		ListRow<Word> selectedRow = listWordsHolder.getRowWithSelectedInput();
		if (selectedRow == null) {
			MainPanel firstVisiblePanel = findFirstVisiblePanelInScrollPane();
			firstVisiblePanel.selectNextInputInSameRow();
			return;
		}
		int rowNumberOfSelectedPanel = selectedRow.getRowNumber();
		int columnNumber = selectedRow.getWrappingPanel()
									  .getSelectedInputIndex();
		MainPanel panelBelowOrAbove = null;
		for (ListRow<Word> listRow : listWordsHolder.getWordsWithDetails()) {
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

	public MainPanel findFirstVisiblePanelInScrollPane() {
		for (ListRow<Word> row : listWordsHolder.getWordsWithDetails()) {
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

	public void editTemporarily(Word word){
		setInEditMode(true);
		repaint(word, InputGoal.EDIT_TEMPORARILY);
	}

	public void setInEditMode(boolean inEditMode) {
		isInEditMode = inEditMode;
	}

	public void repaintWordAndHighlightIfNeeded(Word word) {
		repaint(word);
		if (listWordsHolder.getWordsByHighlight(true)
						   .contains(word)) {
			highlightRowAndScroll(
					listWordsHolder.get0BasedRowNumberOfWord(word), false);
		}
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
		ListRow<Word> listRow = listWordsHolder.findListRowContainingWord(word);
		if (listRow == null || !listRow.isShowing()) {
			return;
		}
		MainPanel panel = listViewManager.repaintWord(word, listRow, inputGoal);
		SwingUtilities.invokeLater(() -> panel.getPanel()
											  .getParent()
											  .requestFocusInWindow());

		listRow.setPanel(panel);
	}

	public void setFinishEditActionRequested(
			boolean finishEditActionRequested) {
		this.finishEditActionRequested = finishEditActionRequested;
	}

	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		if (finishEditActionRequested && postValidationData.isValid()) {
			repaintWordAndHighlightIfNeeded(
					postValidationData.getValidatedWord());
		}

		this.finishEditActionRequested = false;
	}

	public boolean isInEditMode() {
		return isInEditMode;
	}

	public boolean isFilterInputFocused() {
		return listViewManager.isFilterInputFocused();
	}

	public int getStartOfRangeOfDisplayedWords() {
		return lastRowVisible - MAXIMUM_WORDS_TO_SHOW + 2;
	}

	public boolean isLastRowVisible() {
		return lastRowVisible == listWordsHolder.getNumberOfWords() - 1;
	}

	public void showWord(Word word) {
		listViewManager.clear();
		lastRowVisible = listWordsHolder.get0BasedRowNumberOfWord(word);
		showNextWord();
		lastRowVisible--;

	}

	public MyList<Word> getMyList() {
		return myList;
	}

	public void addAdditionalNavigationButtons(AbstractButton... buttons) {
		listViewManager.addAdditionalNavigationButtons(buttons);
	}
}