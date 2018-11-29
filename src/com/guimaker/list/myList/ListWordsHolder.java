package com.guimaker.list.myList;

import com.guimaker.enums.WordDuplicationType;
import com.guimaker.list.ListElement;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.model.ListRow;

import java.util.ArrayList;
import java.util.List;

public class ListWordsHolder<Word extends ListElement> {

	private List<ListRow<Word>> allWordsToRowNumberMap = new ArrayList<>();

	public int getNumberOfWords() {
		return allWordsToRowNumberMap.size();
	}

	public void add(ListRow<Word> word) {
		allWordsToRowNumberMap.add(word);
	}

	public ListRow<Word> getWordInRow0Based(int index) {
		return allWordsToRowNumberMap.get(index);
	}

	public int getIndexOfWord(ListRow<Word> word) {
		return allWordsToRowNumberMap.indexOf(word);
	}

	public void remove(ListRow<Word> word) {
		allWordsToRowNumberMap.remove(word);
	}

	public boolean hasNoWords() {
		return allWordsToRowNumberMap.isEmpty();
	}

	public ListRow<Word> findListRowContainingWord(Word r) {
		for (int i = 0; i < allWordsToRowNumberMap.size(); i++) {
			Word word = allWordsToRowNumberMap.get(i)
											  .getWord();
			if (word.equals(r)) {
				return allWordsToRowNumberMap.get(i);
			}
		}
		return null;
	}

	public List<Word> getWords() {
		List<Word> words = new ArrayList<>();
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			words.add(listRow.getWord());
		}
		return words;
	}

	public int get0BasedRowNumberOfWord(Word word) {
		return getWords().indexOf(word);
	}

	public List<ListRow<Word>> getWordsWithDetails() {
		return allWordsToRowNumberMap;

	}

	public Word getWordInRow(int rowNumber1Based) {
		return allWordsToRowNumberMap.get(rowNumber1Based)
									 .getWord();
	}

	public void clearWords() {
		allWordsToRowNumberMap.clear();
	}

	public WordInMyListExistence<Word> isWordDefined(Word word) {
		if (word.isEmpty()) {
			return new WordInMyListExistence<>(false, null, 0, null);
		}
		for (int i = 0; i < allWordsToRowNumberMap.size(); i++) {
			ListRow<Word> listRow = allWordsToRowNumberMap.get(i);
			if (listRow.getWord()
					   .equals(word)) {
				return new WordInMyListExistence<>(true, listRow.getWord(),
						i + 1, WordDuplicationType.WORD);
			}
		}
		return new WordInMyListExistence<>(false, null, -1, null);
	}

	public List<Word> getWordsByHighlight(boolean highlighted) {
		List<Word> highlightedWords = new ArrayList<>();
		for (ListRow<Word> word : allWordsToRowNumberMap) {
			if (word.isHighlighted() == highlighted) {
				highlightedWords.add(word.getWord());
			}
		}
		return highlightedWords;
	}

	public void setWordInRow0Based(int lastRowVisible,
			ListRow<Word> visibleRow) {
		allWordsToRowNumberMap.set(lastRowVisible, visibleRow);
	}

	public ListRow<Word> getRowWithSelectedInput() {
		for (ListRow<Word> listRow : allWordsToRowNumberMap) {
			if (listRow.getWrappingPanel()
					   .hasSelectedInput()) {
				return listRow;
			}
		}
		return null;
	}

}
