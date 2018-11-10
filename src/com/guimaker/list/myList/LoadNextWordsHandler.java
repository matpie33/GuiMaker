package com.guimaker.list.myList;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.utilities.Range;

import javax.swing.*;

public class LoadNextWordsHandler implements LoadWordsHandler {
	//TODO move to "loadAdditionalWordsHandling" package


	@Override
	public Range getRangeOfWordsToRemove(int numberOfAddedWords) {
		return new Range(0, numberOfAddedWords - 1);
	}

	@Override
	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize) {
		return lastRowVisible < allWordsToRowNumberMapSize;
	}

	@Override
	public boolean shouldDisableLoadWordsButton(
			ListWordsController listWordsController) {
		return listWordsController.isLastRowVisible();
	}

	@Override
	public ListWordsLoadingDirection getDirection() {
		return ListWordsLoadingDirection.NEXT;
	}

}
