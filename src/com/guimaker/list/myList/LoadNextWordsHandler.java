package com.guimaker.list.myList;

import com.guimaker.utilities.Range;

import javax.swing.*;

public class LoadNextWordsHandler implements LoadWordsHandler {
	private ListWordsController listWordsController;
	//TODO move to "loadAdditionalWordsHandling" package

	public LoadNextWordsHandler(ListWordsController listWordsController) {
		this.listWordsController = listWordsController;
	}

	@Override
	public void addWord() {
		listWordsController.showNextWord();
	}

	@Override
	public Range getRangeOfWordsToRemove(int numberOfAddedWords) {
		return new Range(1, numberOfAddedWords);
	}

	@Override
	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize) {
		return lastRowVisible < allWordsToRowNumberMapSize;
	}

	@Override
	public void enableOrDisableLoadWordsButtons(
			AbstractButton buttonLoadNextWords,
			AbstractButton buttonLoadPreviousWords,
			boolean hasMoreWordsToShow) {
		if (!hasMoreWordsToShow) {
			buttonLoadNextWords.setEnabled(false);
		}
		else if (!buttonLoadPreviousWords.isEnabled()) {
			buttonLoadPreviousWords.setEnabled(true);
		}
	}
}
