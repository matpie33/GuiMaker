package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.loadAdditionalWordsHandling.LoadNextWordsHandler;
import com.guimaker.list.loadAdditionalWordsHandling.LoadPreviousWordsHandler;
import com.guimaker.list.loadAdditionalWordsHandling.LoadWordsHandler;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.filtering.ListFilteringController;
import com.guimaker.list.myList.filtering.ListFilteringPanel;
import com.guimaker.model.ListRow;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.utilities.Range;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ListViewManager<Word extends ListElement> {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private ListRowCreator<Word> listRowCreator;
	private ApplicationChangesManager applicationChangesManager;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private Color labelsColor = Color.WHITE;
	private InputGoal inputGoal;
	private ListFilteringPanel<Word> listFilteringPanel;
	private boolean isInitialized = false;
	private ListConfiguration listConfiguration;
	private ListPanel<Word> listPanel;
	private ListPanelUpdater listPanelUpdater;

	public ListViewManager(ListConfiguration<Word> listConfiguration,
			ListWordsController<Word> controller) {
		this.listConfiguration = listConfiguration;
		listPanel = new ListPanel<>(listConfiguration, this,
				controller);

		listFilteringPanel = new ListFilteringController<>(this,
				controller).getListFilteringPanel();
		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		listWordsController = controller;
		loadNextWordsHandler = new LoadNextWordsHandler();
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, listPanel.getRowsPanel());
		this.listRowCreator = listConfiguration.getListRowCreator();
		listInputsSelectionManager = listConfiguration.getAllInputsSelectionManager();
		listPanelUpdater = new ListPanelUpdater(listPanel,
				listConfiguration);
		listPanel.createPanel();
		listPanelUpdater.adjustVisibilityOfShowNextPreviousWordsButtons();
	}

	public void inheritScrollPane() {
		listConfiguration.inheritScrollbar(true);
	}

	public LoadNextWordsHandler getLoadNextWordsHandler() {
		return loadNextWordsHandler;
	}

	public LoadPreviousWordsHandler getLoadPreviousWordsHandler() {
		return loadPreviousWordsHandler;
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, ListWordsLoadingDirection loadingDirection,
			InputGoal inputGoal) {
		this.inputGoal = inputGoal;
		if (!isInitialized) {
			createElements();
		}

		CommonListElements commonListElements = listPanel.createCommonListElements(
				word, inputGoal, rowNumber, labelsColor);
		MainPanel rowPanel = null;
		if (shouldShowWord) {
			ListRowData<Word> listRow = this.listRowCreator.createListRow(word,
					commonListElements, inputGoal);
			rowPanel = listRow.getRowPanel();
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder.createRow(
					FillType.HORIZONTAL, Anchor.NORTH, rowPanel.getPanel());
			listPanelUpdater.loadWords(loadingDirection, abstractSimpleRow);
		}
		else {
			listPanelUpdater.enableButtonLoadNextWords();
			listPanelUpdater.updateRowsPanel();
		}
		if (rowPanel != null && listInputsSelectionManager != null) {
			rowPanel.addManager(listInputsSelectionManager);
		}

		return new ListRow<>(word, rowPanel,
				commonListElements.getRowNumberLabel(), rowNumber);
	}

	public AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {
		return listWordsController.createButtonShowNextOrPreviousWords(
				loadWordsHandler);
	}

	public String createTextForRowNumber(int rowNumber) {
		return listPanel.createTextForRowNumber(rowNumber);
	}

	public void createElements() {

		listPanelUpdater.removeFirstRowInRowsPanel();
		isInitialized = true;

		if (listConfiguration.isWordSearchingEnabled()) {
			ListRowData<Word> listRow = this.listRowCreator.createListRow(
					listWordsController.getWordInitializer()
									   .initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE),
					InputGoal.SEARCH);
			if (!listRow.isEmpty()) {

				JPanel panel = listFilteringPanel.createPanel(listRow,
						listPanel.createButtonClearFilter());

				listPanelUpdater.addPanelToFilterPanel(panel);
				listPanel.addHotkey(KeyModifiers.CONTROL,
						KeyEvent.VK_SPACE,
						listFilteringPanel.createActionSwitchComboboxValue(),
						listPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				listPanel.addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						listFilteringPanel.createActionFocusAndSelectAllInFilterTextField(),
						listPanel.getPanel(),
						HotkeysDescriptions.FOCUS_FILTERING_PANEL);
			}
			else {
				listPanelUpdater.removeFilterPanel();
			}
		}

		listPanelUpdater.disablePanelOpacityIfWithoutAddAndSearch();

	}

	public JTextComponent getFilterComponent() {
		return listFilteringPanel.getFilteringInput();
	}

	public ListElementPropertyManager getFilterInputPropertyManager() {
		return listFilteringPanel.getPropertyManagerForInput();
	}

	public void removeWordsFromRangeInclusive(Range range) {
		listPanel.getRowsPanel()
				 .removeRowsInclusive(range.getRangeStart(),
								range.getRangeEnd());
	}

	public void clearHighlightedRow(JComponent row) {
		listPanelUpdater.clearHighlightedRow(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = highlightRow(row);
		scrollTo(listPanel.getRowsPanel()
						  .getRows()
						  .get(rowNumber));
	}

	public int highlightRow(JComponent row) {
		int rowNumber = listPanel.getRowsPanel()
								 .getIndexOfPanel(row);
		changePanelColor(rowNumber,
				applicationChangesManager.getApplicationWindow()
										 .getApplicationConfiguration()
										 .getListRowHighlightColor());

		listPanel.getRowsPanel()
				 .getPanel()
				 .repaint();
		return rowNumber;
	}

	private void changePanelColor(int rowNumber, Color color) {
		listPanel.getRowsPanel()
				 .setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		listPanelUpdater.scrollTo(panel);
	}

	public void scrollToBottom() {
		listPanelUpdater.scrollToBottom();

	}

	public void removeRow(JComponent panel) {
		listPanelUpdater.removeRow(panel);
	}

	public void clear() {
		listPanelUpdater.clearRowsPanel();
	}

	public void scrollToTop() {
		listPanelUpdater.scrollToTop();
	}

	public int getNumberOfListRows() {
		return listPanel.getRowsPanel()
						.getNumberOfRows();
	}

	public void enableButtonShowPreviousWords() {
		listPanelUpdater.enableButtonLoadPreviousWords();
	}

	public void toggleEnabledState() {
		listPanelUpdater.toggleRowsPanelEnabledState();
	}

	public MainPanel repaintWord(Word word, int rowNumber, JComponent oldPanel,
			InputGoal customInputGoal, boolean highlighted) {
		CommonListElements commonListElements = listPanel.createCommonListElements(
				word, this.inputGoal, rowNumber, labelsColor);
		MainPanel newPanel = listRowCreator.createListRow(word,
				commonListElements,
				customInputGoal == null ? this.inputGoal : customInputGoal)
										   .getRowPanel();
		if (highlighted) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
											 .getApplicationConfiguration()
											 .getListRowHighlightColor());
		}
		if (customInputGoal != null && customInputGoal.equals(
				InputGoal.EDIT_TEMPORARILY)) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
											 .getApplicationConfiguration()
											 .getListRowEditTemporarilyColor());
			newPanel.updateView();
		}
		listPanelUpdater.replacePanelsInRowsPanel(oldPanel,
				newPanel.getPanel());

		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listFilteringPanel.getFilteringInput()
								 .hasFocus();
	}

	public InputGoal getInputGoal() {
		return inputGoal;
	}

	public void addElementsForEmptyList() {
		listPanel.addElementsForEmptyList();
	}

	public JPanel getPanel() {
		return listPanel.getPanel();
	}

	public void updateRowsPanel() {
		listPanelUpdater.updatePanel();
	}

	public void clearRowsPanel() {
		listPanelUpdater.clearRowsPanel();
	}

	public void enableOrDisableLoadWordsButton(boolean shouldDisable,
			ListWordsLoadingDirection direction) {
		listPanelUpdater.enableOrDisableLoadWordsButton(shouldDisable,
				direction);
	}
}
