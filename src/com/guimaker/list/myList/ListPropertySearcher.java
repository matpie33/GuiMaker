package com.guimaker.list.myList;

import com.guimaker.enums.MoveDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.strings.ExceptionsMessages;

public class ListPropertySearcher<Word extends ListElement> {

	private ListWordsController<Word> listController;
	private ListConfiguration<Word> listConfiguration;

	public ListPropertySearcher(ListWordsController<Word> listController,
			ListConfiguration<Word> listConfiguration) {
		this.listController = listController;
		this.listConfiguration = listConfiguration;
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
			listConfiguration.getApplicationChangesManager()
							 .getApplicationWindow()
							 .showMessageDialog(
									 ExceptionsMessages.WORD_NOT_FOUND_EXCEPTION);
		}

		return -1;
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

	public <Property> WordInMyListExistence<Word> doesWordWithPropertyExist(
			Property property,
			ListElementPropertyManager<Property, Word> propertyManager,
			Word wordToExclude) {
		for (int indexOfWord = 0;
			 indexOfWord < listController.getWords().size(); indexOfWord++) {
			Word word = listController.getWords().get(indexOfWord);
			if (word == wordToExclude) {
				continue;
			}
			if (propertyManager.isPropertyFound(property, word)) {
				return new WordInMyListExistence<>(true, word, indexOfWord + 1);
			}
		}
		return new WordInMyListExistence<>(false, null, -1);
	}

	public <Property> Word findRowBasedOnPropertyStartingFromBeginningOfList(
			ListElementPropertyManager<Property, Word> propertyChecker,
			Property searchedPropertyValue, MoveDirection searchDirection,
			boolean displayMessage) {
		int rowNumber = findRowNumberBasedOnProperty(
				propertyChecker, searchedPropertyValue, searchDirection,
				displayMessage);
		if (rowNumber != -1) {
			return listController.getWordInRow(rowNumber);
		}
		else {
			return null;
		}

	}

}
