package com.guimaker.list.loadAdditionalWordsHandling;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.utilities.Range;

import javax.swing.*;

public interface LoadWordsHandler {

	public Range getRangeOfWordsToRemove(int numberOfAddedWords);

	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumberMapSize);

	public boolean shouldDisableLoadWordsButton (ListWordsController listWordsController);

	public ListWordsLoadingDirection getDirection();

}
