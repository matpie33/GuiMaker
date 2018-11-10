package com.guimaker.list.myList;

import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.utilities.Range;

import javax.swing.*;

public interface LoadWordsHandler {
	public void addWord();

	public Range getRangeOfWordsToRemove(int numberOfAddedWords);

	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize);

	public void enableOrDisableLoadWordsButtons(
			AbstractButton buttonLoadNextWords,
			AbstractButton buttonLoadPreviousWords, boolean hasMoreWordsToShow);
}
