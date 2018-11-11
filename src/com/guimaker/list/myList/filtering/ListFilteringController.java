package com.guimaker.list.myList.filtering;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.panel.ListViewManager;
import com.guimaker.model.FilteredWordMatch;
import com.guimaker.model.ListRow;
import com.guimaker.utilities.WordSearching;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.SortedMap;

public class ListFilteringController<Word extends ListElement> {

	private ListFilteringScheduler listFilteringScheduler;
	private ListFilteringPanel<Word> listFilteringPanel;
	private ListViewManager<Word> listViewManager;
	private ListWordsController<Word> listWordsController;
	private final int numberOfWordsToDisplayByFilter = 10;

	public ListFilteringController(ListViewManager<Word> listViewManager,
			ListWordsController<Word> listWordsController) {
		listFilteringScheduler = new ListFilteringScheduler(this);
		listFilteringPanel = new ListFilteringPanel<>(this);
		this.listViewManager = listViewManager;
		this.listWordsController = listWordsController;
	}

	public ListFilteringPanel<Word> getListFilteringPanel() {
		return listFilteringPanel;
	}

	public DocumentListener createActionFilterImmediately() {
		return new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				listFilteringScheduler.scheduleFiltering();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				listFilteringScheduler.scheduleFiltering();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		};
	}

	public void filterWords() {
		String text = listFilteringPanel.getFilteringInput()
										.getText();
		ListElementPropertyManager<?, Word> filterInputPropertyManager = listFilteringPanel.getPropertyManagerForInput();
		SortedMap<FilteredWordMatch, ListRow<Word>> words = WordSearching.filterWords(
				listWordsController.getWordsWithDetails(), text,
				filterInputPropertyManager);
		listViewManager.clear();

		int newRowNumber = 1;
		for (ListRow<Word> listRow : words.values()) {
			if (newRowNumber > numberOfWordsToDisplayByFilter) {
				break;
			}
			int rowNumber = listRow.getRowNumber() - 1;
			listRow.setPanel(listViewManager.addRow(
					listWordsController.getWordInRow(rowNumber), newRowNumber++,
					true, ListWordsLoadingDirection.NEXT,
					listViewManager.getInputGoal())
											.getWrappingPanel());
			if (listRow.isHighlighted()) {
				listViewManager.highlightRow(listRow.getJPanel());
			}
		}
		listWordsController.scrollToTop();
		listViewManager.updateRowsPanel();
	}

}
