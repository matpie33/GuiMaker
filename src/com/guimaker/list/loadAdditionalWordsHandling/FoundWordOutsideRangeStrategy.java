package com.guimaker.list.loadAdditionalWordsHandling;

import com.guimaker.list.myList.ListWordsController;
import com.guimaker.utilities.Range;

public class FoundWordOutsideRangeStrategy implements LoadWordsForFoundWord {

	private int maximumWordsDisplayed;
	private ListWordsController listWordsController;
	private int foundWordRowNumber;

	public FoundWordOutsideRangeStrategy(int maximumWordsDisplayed,
			ListWordsController listWordsController) {
		this.maximumWordsDisplayed = maximumWordsDisplayed;
		this.listWordsController = listWordsController;
	}

	@Override
	public boolean isApplicable(int foundWordRowNumber,
			Range visibleWordsRange) {
		int distanceFromLastRow = Math.abs(
				foundWordRowNumber - visibleWordsRange.getRangeEnd());
		int distanceFromFirstRow = Math.
											   abs(foundWordRowNumber
													   - visibleWordsRange.getRangeStart());
		this.foundWordRowNumber = foundWordRowNumber;
		return distanceFromLastRow > maximumWordsDisplayed
				|| distanceFromFirstRow > maximumWordsDisplayed;
	}

	@Override
	public void execute() {
		int indexOfFirstWordToLoad;
		int lastWordMinusMaximumWordsToShow =
				listWordsController.getNumberOfWords() - maximumWordsDisplayed;
		if (foundWordRowNumber < lastWordMinusMaximumWordsToShow) {
			indexOfFirstWordToLoad = foundWordRowNumber;
		}
		else {
			indexOfFirstWordToLoad = lastWordMinusMaximumWordsToShow;
		}
		listWordsController.showWordsStartingFromRow(indexOfFirstWordToLoad);
	}
}
