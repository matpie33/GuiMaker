package com.guimaker.model;

import com.guimaker.list.ListElement;

import java.util.Comparator;

public class FilteredWordMatch implements Comparable {

	private double averagePercentageOfLettersMatched;

	private int numberOfUnmatchedWords;

	private ListElement listElement;

	public FilteredWordMatch(double averagePercentageOfLettersMatched,
			int numberOfUnmatchedWords, ListElement listElement) {
		this.averagePercentageOfLettersMatched = averagePercentageOfLettersMatched;
		this.numberOfUnmatchedWords = numberOfUnmatchedWords;
		this.listElement = listElement;
	}

	private double getAveragePercentageOfLettersMatched() {
		return averagePercentageOfLettersMatched;
	}

	private int getNumberOfUnmatchedWords() {
		return numberOfUnmatchedWords;
	}

	private String getListElementDisplayedText() {
		return listElement.getDisplayedText();
	}

	@Override
	public int compareTo(Object o) {
		if (!getClass().equals(o.getClass())) {
			return -1;
		}
		FilteredWordMatch other = (FilteredWordMatch) o;

		return Comparator.comparing(
				FilteredWordMatch::getNumberOfUnmatchedWords)
						 .reversed()
						 .thenComparing(FilteredWordMatch
								 ::getAveragePercentageOfLettersMatched)
						 .thenComparing(FilteredWordMatch::getListElementDisplayedText)
						 .compare(this, other);
	}
}
