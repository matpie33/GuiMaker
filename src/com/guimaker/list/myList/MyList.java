package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.*;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.ParentListData;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.panels.MainPanel;
import com.guimaker.swingUtilities.ProgressUpdater;

import javax.swing.*;
import java.util.List;

public class MyList<Word extends ListElement>
		implements ObservableList<Word>, InputValidationListener<Word> {

	private final ParentListData<?, Word> parentListAndWordContainingThisList;
	private ListWordsHolder<Word> listWordsHolder;
	private ApplicationChangesManager applicationChangesManager;
	private ListWordsController<Word> listController;
	private ListElementInitializer<Word> wordInitializer;
	private Class listElementClass;
	private String title;
	private ListRowCreator<Word> listRowCreator;
	private ListPropertySearcher<Word> listPropertySearcher;
	private InsertWordPanel<Word> insertWordPanel;
	private Object rootList;

	public MyList(ListConfiguration<Word> listConfiguration) {
		parentListAndWordContainingThisList = listConfiguration.getParentListAndWordContainingThisList();
		this.listRowCreator = listConfiguration.getListRowCreator();
		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		this.wordInitializer = listConfiguration.getListElementInitializer();
		this.title = listConfiguration.getTitle();
		this.insertWordPanel = new InsertWordPanel<>(this,
				applicationChangesManager);
		listWordsHolder = new ListWordsHolder<>();
		listController = new ListWordsController<>(insertWordPanel,
				listConfiguration, this, listWordsHolder);
		listPropertySearcher = new ListPropertySearcher<>(listWordsHolder,
				listConfiguration);
	}

	public InsertWordPanel<Word> getInsertWordPanel() {
		return insertWordPanel;
	}

	@Override
	public void addListObserver(ListObserver<Word> listObserver) {
		listController.addObserver(listObserver);
	}

	public void addSwitchBetweenInputsFailListener(
			SwitchBetweenInputsFailListener listener) {
		listController.addSwitchBetweenInputsFailListener(listener);
	}

	public ListElementInitializer<Word> getWordInitializer() {
		return wordInitializer;
	}

	public ListRowCreator<Word> getListRowCreator() {
		return listRowCreator;
	}

	public void scrollToTop() {
		listController.scrollToTop();
	}

	public String getTitle() {
		return title;
	}

	public Class getListElementClass() {
		if (listElementClass == null) {
			listElementClass = createWord().getClass();
		}
		return listElementClass;
	}

	public boolean addWord(Word word) {
		return addWord(word, InputGoal.EDIT, true);
	}

	public boolean addWord(Word word, InputGoal inputGoal) {
		return word != null && listController.add(word, inputGoal, true);
	}

	private boolean addWord(Word word, InputGoal inputGoal,
			boolean tryToShowWord) {
		return word != null && listController.add(word, inputGoal,
				tryToShowWord);
	}

	public void addWords(List<Word> words, InputGoal inputGoal,
			boolean tryToShowWords, boolean validate) {
		listController.addWords(words, inputGoal, tryToShowWords, validate);
	}

	private Word createWord() {
		Word word = wordInitializer.initializeElement();
		listElementClass = word.getClass();
		return word;
	}

	public void highlightRow(int rowNumber) {
		highlightRow(rowNumber, false);
	}

	public void highlightRow(int rowNumber, boolean clearLastHighlightedWord) {
		listController.highlightRowAndScroll(rowNumber,
				clearLastHighlightedWord);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MoveDirection searchDirection,
			boolean displayMessage) {
		return listPropertySearcher.findRowBasedOnPropertyStartingFromBeginningOfList(
				propertyChecker, searchedPropertyValue, searchDirection,
				displayMessage);
	}

	public void scrollToBottom() {
		listController.scrollToBottom();
	}

	public void cleanWords() {
		listController.clear();
	}

	public void save() {
		this.applicationChangesManager.save();
	}

	public JPanel getPanel() {
		return listController.getPanel();
	}

	public int getNumberOfWords() {
		return listWordsHolder.getNumberOfWords();
	}

	public boolean isEmpty() {
		return getNumberOfWords() == 0;
	}

	public List<Word> getWords() {
		return listWordsHolder.getWords();
	}

	public int get1BasedRowNumberOfWord(Word word) {
		return listWordsHolder.get0BasedRowNumberOfWord(word) + 1;
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listWordsHolder.getWordInRow(rowNumber1Based - 1);
	}

	public List<Word> getHighlightedWords() {
		return listWordsHolder.getWordsByHighlight(true);
	}

	public List<Word> getNotHighlightedWords() {
		return listWordsHolder.getWordsByHighlight(false);
	}

	public int getMaximumDisplayedWords() {
		return listController.getMaximumWordsToShow();
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listController.showWordsStartingFromRow(firstRowToLoad);
	}

	public void clearHighlightedWords() {
		listController.clearHighlightedWords();
	}

	public boolean hasSelectedInput() {
		return listWordsHolder.getRowWithSelectedInput() != null;
	}

	public MainPanel getPanelWithSelectedInput() {
		return listController.getPanelWithSelectedInput();
	}

	public void selectNextInputInSameRow() {
		MainPanel panelWithSelectedInput = getPanelWithSelectedInput();
		if (panelWithSelectedInput == null) {
			panelWithSelectedInput = findFirstVisiblePanelInScrollpane();
		}
		panelWithSelectedInput.selectNextInputInSameRow();
	}

	private MainPanel findFirstVisiblePanelInScrollpane() {
		return listController.findFirstVisiblePanelInScrollPane();
	}

	public void selectPreviousInputInSameRow() {
		getPanelWithSelectedInput().selectPreviousInputInSameRow();
	}

	public void selectInputBelowCurrent() {
		listController.selectPanelBelowOrAboveSelected(MoveDirection.BELOW);
	}

	public void selectInputAboveCurrent() {
		listController.selectPanelBelowOrAboveSelected(MoveDirection.ABOVE);
	}

	public ProgressUpdater getProgressUpdater() {
		return listController.getProgressUpdater();
	}

	public void remove(Word word) {
		listController.remove(word);
	}

	public void removeWordInRow(int rowNumber0Based) {
		listController.remove(getWordInRow(rowNumber0Based + 1));
	}

	public void updateObservers(Word word,
			ListElementModificationType modificationType) {
		listController.updateObservers(word, modificationType);
	}

	public void update(Word word,
			ListElementModificationType modificationType) {
		if (modificationType.equals(ListElementModificationType.EDIT)) {
			listController.repaint(word);
		}
		else {
			listController.remove(word);
		}
	}

	@Override
	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		listController.inputValidated(postValidationData);
	}

	public boolean isInEditMode() {
		return listController.isInEditMode();
	}

	public boolean isFilterInputFocused() {
		return listController.isFilterInputFocused();
	}

	public boolean containsWord(Word word) {
		return getWords().contains(word);
	}

	public void showWord(Word word) {
		listController.showWord(word);
	}

	public <Property> WordInMyListExistence<Word> doesWordWithPropertyExist(
			Property propertyNewValue,
			ListElementPropertyManager<Property, Word> listElementPropertyManager,
			Word propertyHolder) {
		return listPropertySearcher.doesWordWithPropertyExist(propertyNewValue,
				listElementPropertyManager, propertyHolder);
	}

	public MyList getRootList() {
		MyList parentList = getParentList();
		while (parentList != null && parentList.getParentList() != null) {
			parentList = parentList.getParentList();
		}
		return parentList;
	}

	private MyList getParentList() {
		return parentListAndWordContainingThisList != null ?
				parentListAndWordContainingThisList.getList() :
				null;
	}

}
