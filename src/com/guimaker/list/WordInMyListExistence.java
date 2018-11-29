package com.guimaker.list;

import com.guimaker.enums.WordDuplicationType;

public class WordInMyListExistence<Word extends ListElement> {

	private boolean existsInList;
	private Word word;
	private int oneBasedRowNumber;
	private WordDuplicationType duplicationType;

	public WordInMyListExistence(boolean existsInList, Word word,
			int oneBasedRowNumber, WordDuplicationType duplicationType) {
		this.existsInList = existsInList;
		this.word = word;
		this.oneBasedRowNumber = oneBasedRowNumber;
		this.duplicationType = duplicationType;
	}

	public WordDuplicationType getDuplicationType() {
		return duplicationType;
	}

	public boolean exists() {
		return existsInList;
	}

	public Word getWord() {
		return word;
	}

	public int getOneBasedRowNumber() {
		return oneBasedRowNumber;
	}

	public void clearRowNumber() {
		oneBasedRowNumber = -1;
	}

}
