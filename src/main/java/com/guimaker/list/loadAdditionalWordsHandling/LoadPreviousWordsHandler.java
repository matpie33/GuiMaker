package com.guimaker.list.loadAdditionalWordsHandling;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.panels.MainPanel;
import com.guimaker.utilities.Range;

public class LoadPreviousWordsHandler implements LoadWordsHandler {

	private MainPanel rowsPanel;
	private ListWordsController listWordsController;

	public LoadPreviousWordsHandler(ListWordsController listWordsController,
			MainPanel rowsPanel) {
		this.rowsPanel = rowsPanel;
		this.listWordsController = listWordsController;
	}


	@Override
	public Range getRangeOfWordsToRemove(int numberOfAddedWords) {
		int lastRowIndex = rowsPanel.getNumberOfRows() - 1;
		return new Range(lastRowIndex - numberOfAddedWords + 1, lastRowIndex);
	}

	@Override
	public boolean shouldContinue(int lastRowVisible,
			int allWordsToRowNumbersMapSize) {
		return listWordsController.getFirstVisibleRowNumber() > 0;
	}

	@Override
	public boolean shouldDisableLoadWordsButton(
			ListWordsController listWordsController) {
		return listWordsController.getFirstVisibleRowNumber() == 0;
	}

	@Override
	public ListWordsLoadingDirection getDirection() {
		return ListWordsLoadingDirection.PREVIOUS;
	}

}
