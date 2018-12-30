package com.guimaker.list.myList.filtering;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.panel.ListViewManager;
import com.guimaker.model.FilteredWordMatch;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.model.ListRow;
import com.guimaker.model.WordDictionaryData;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.WordSearching;
import com.guimaker.webPanel.WebPagePanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.SortedMap;

public class ListFilteringController<Word extends ListElement> {

	private ListFilteringScheduler listFilteringScheduler;
	private ListFilteringPanel<Word> listFilteringPanel;
	private ListViewManager<Word> listViewManager;
	private ListWordsController<Word> listWordsController;
	private final int numberOfWordsToDisplayByFilter = 10;
	private Component splitPaneRightComponentBeforeChange;

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
				listWordsController.getListWordsHolder()
								   .getWordsWithDetails(), text,
				filterInputPropertyManager);
		listViewManager.clear();

		int newRowNumber = 1;
		for (ListRow<Word> listRow : words.values()) {
			if (newRowNumber > numberOfWordsToDisplayByFilter) {
				break;
			}
			int rowNumber = listRow.getRowNumber() - 1;
			listRow.setPanel(listViewManager.addRow(
					listWordsController.getListWordsHolder()
									   .getWordInRow(rowNumber), newRowNumber++,
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

	public ItemListener createActionEnableSearchInDictionary(
			ListConfiguration listConfiguration) {
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JSplitPane splitPaneToPutDictionaryInto = listConfiguration.getWordDictionaryData()
																		   .getSplitPaneToPutDictionaryInto();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (splitPaneRightComponentBeforeChange == null) {
						splitPaneRightComponentBeforeChange = splitPaneToPutDictionaryInto.getRightComponent();
					}
					WebPagePanel dictionaryPanel = createDictionaryPanel(
							listConfiguration);
					CommonActionsCreator.addHotkey(
							new HotkeyWrapper(KeyEvent.VK_ENTER),
							createActionSearchInDictionary(dictionaryPanel,
									listConfiguration.getWordDictionaryData()),
							splitPaneToPutDictionaryInto);
				}
				else {
					splitPaneToPutDictionaryInto.setRightComponent(
							splitPaneRightComponentBeforeChange);
				}

			}
		};
	}

	private WebPagePanel createDictionaryPanel(
			ListConfiguration listConfiguration) {
		WebPagePanel webPagePanel = new WebPagePanel(null, null,
				listConfiguration.getApplicationChangesManager()
								 .getApplicationWindow());
		WordDictionaryData wordDictionaryData = listConfiguration.getWordDictionaryData();
		webPagePanel.showPage(
				String.format(wordDictionaryData.getSearchUrlPattern(), " "));
		JSplitPane splitPaneToPutDictionaryInto = wordDictionaryData.getSplitPaneToPutDictionaryInto();
		splitPaneToPutDictionaryInto.setRightComponent(
				(webPagePanel.getSwitchingPanel()));
		splitPaneToPutDictionaryInto.setResizeWeight(0.2D);
		return webPagePanel;
	}

	private AbstractAction createActionSearchInDictionary(
			WebPagePanel webPagePanel, WordDictionaryData wordDictionaryData) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listFilteringPanel.getFilteringInput()
									  .hasFocus()) {
					webPagePanel.showPageWithoutGrabbingFocus(String.format(
							wordDictionaryData.getSearchUrlPattern(),
							listFilteringPanel.getFilteringInput()
											  .getText()));
				}
			}
		};
	}
}
