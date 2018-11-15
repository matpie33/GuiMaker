package com.guimaker.list.myList;

import com.guimaker.enums.MoveDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.strings.ExceptionsMessages;

public class ListPropertySearcher<Word extends ListElement> {

	private ListWordsHolder<Word> listWordsHolder;
	private ListConfiguration<Word> listConfiguration;

	public ListPropertySearcher(ListWordsHolder<Word> listWordsHolder,
			ListConfiguration<Word> listConfiguration) {
		this.listWordsHolder = listWordsHolder;
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
				Word word = listWordsHolder.getWordInRow(rowNumber);
				if (propertyChecker.isPropertyFound(searchedPropertyValue,
						word)) {
					return rowNumber;
				}
			}
			rowNumber += incrementValue;
			shouldContinueSearching =
					rowNumber < listWordsHolder.getNumberOfWords();
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
				> listWordsHolder.getNumberOfWords() - 1);
	}

	private int setRowNumberToTheOtherEndOfList(int rowNumber) {
		if (rowNumber < 0) {
			return listWordsHolder.getNumberOfWords();
		}
		if (rowNumber >= listWordsHolder.getNumberOfWords()) {
			return -1;
		}
		return rowNumber;
	}

	public <Property> WordInMyListExistence<Word> doesWordWithPropertyExist(
			Property property,
			ListElementPropertyManager<Property, Word> propertyManager,
			Word wordToExclude) {
		for (int indexOfWord = 0;
			 indexOfWord < listWordsHolder.getWords().size(); indexOfWord++) {
			Word word = listWordsHolder.getWords().get(indexOfWord);
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
			return listWordsHolder.getWordInRow(rowNumber);
		}
		else {
			return null;
		}

	}

}
