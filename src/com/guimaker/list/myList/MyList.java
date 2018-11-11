package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.*;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.MainPanel;
import com.guimaker.strings.ExceptionsMessages;
import com.guimaker.swingUtilities.ProgressUpdater;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.List;

public class MyList<Word extends ListElement>
		implements ObservableList<Word>, InputValidationListener<Word> {

	private DialogWindow parent;
	private ApplicationChangesManager applicationChangesManager;
	private ListWordsController<Word> listController;
	private ListElementInitializer<Word> wordInitializer;
	private Class listElementClass;
	private String title;
	private ListRowCreator<Word> listRowCreator;
	private MyList<Word> sourceList;

	public MyList(ListConfiguration<Word> listConfiguration) {
		this.listRowCreator = listConfiguration.getListRowCreator();
		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		this.parent = listConfiguration.getDialogWindow();
		this.wordInitializer = listConfiguration.getListElementInitializer();
		this.title = listConfiguration.getTitle();
		listController = new ListWordsController<>(listConfiguration, this);

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

	public boolean addWord(Word word, InputGoal inputGoal,
			boolean tryToShowWord) {
		return word != null && listController.add(word, inputGoal,
				tryToShowWord);
	}

	public void addWords(List<Word> words, InputGoal inputGoal,
			boolean tryToShowWords, boolean validate) {
		listController.addWords(words, inputGoal, tryToShowWords, validate);
	}

	public Word createWord() {
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

	private <Property> int findRowNumberBasedOnProperty(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MoveDirection searchDirection,
			boolean displayMessage) {

		int incrementValue = searchDirection.getIncrementValue();

		int rowNumber = 0;
		boolean shouldContinueSearching;
		do {
			if (isRowNumberOutOfRange(rowNumber)) {
				rowNumber = setRowNumberToTheOtherEndOfList(rowNumber);
			}
			else {
				Word word = listController.getWordInRow(rowNumber);
				if (propertyChecker.isPropertyFound(searchedPropertyValue,
						word)) {
					return rowNumber;
				}
			}
			rowNumber += incrementValue;
			shouldContinueSearching =
					rowNumber < listController.getNumberOfWords();
		}
		while (shouldContinueSearching);

		if (displayMessage) {
			parent.showMessageDialog(
					ExceptionsMessages.WORD_NOT_FOUND_EXCEPTION);
		}

		return -1;
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MoveDirection searchDirection,
			boolean displayMessage) {
		int rowNumber = findRowNumberBasedOnProperty(propertyChecker,
				searchedPropertyValue, searchDirection, displayMessage);
		if (rowNumber != -1) {
			return listController.getWordInRow(rowNumber);
		}
		else {
			return null;
		}

	}

	public <Property> WordInMyListExistence<Word> doesWordWithPropertyExist(
			Property property,
			ListElementPropertyManager<Property, Word> propertyManager,
			Word wordToExclude) {
		for (int indexOfWord = 0;
			 indexOfWord < getWords().size(); indexOfWord++) {
			Word word = getWords().get(indexOfWord);
			if (word == wordToExclude) {
				continue;
			}
			if (propertyManager.isPropertyFound(property, word)) {
				return new WordInMyListExistence<>(true, word, indexOfWord + 1);
			}
		}
		if (sourceList != null) {
			WordInMyListExistence<Word> wordInSourceListExistence = sourceList.doesWordWithPropertyExist(
					property, propertyManager, wordToExclude);
			wordInSourceListExistence.clearRowNumber();
			return wordInSourceListExistence;
		}
		return new WordInMyListExistence<>(false, null, -1);
	}

	private Word getHighlightedWord() {
		int highlightedRow = listController.getHighlightedRowNumber();
		if (highlightedRow < 0) {
			return null;
		}
		Word word = listController.getWordInRow(highlightedRow);
		return word;
	}

	private boolean isRowNumberOutOfRange(int rowNumber) {
		return (rowNumber < 0) || (rowNumber
				> listController.getNumberOfWords() - 1);
	}

	private int setRowNumberToTheOtherEndOfList(int rowNumber) {
		if (rowNumber < 0) {
			return listController.getNumberOfWords();
		}
		if (rowNumber >= listController.getNumberOfWords()) {
			return -1;
		}
		return rowNumber;
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
		return listController.getNumberOfWords();
	}

	public boolean isEmpty() {
		return getNumberOfWords() == 0;
	}

	public List<Word> getWords() {
		return listController.getWords();
	}

	public int get1BasedRowNumberOfWord(Word word) {
		return listController.get0BasedRowNumberOfWord(word) + 1;
	}

	public Word getWordInRow(int rowNumber1Based) {
		return listController.getWordInRow(rowNumber1Based - 1);
	}

	public List<Word> getHighlightedWords() {
		return listController.getWordsByHighlight(true);
	}

	public List<Word> getNotHighlightedWords() {
		return listController.getWordsByHighlight(false);
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
		return listController.getRowWithSelectedInput() != null;
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

	public JTextComponent getSelectedInput() {
		return getPanelWithSelectedInput().getSelectedInput();
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
}
