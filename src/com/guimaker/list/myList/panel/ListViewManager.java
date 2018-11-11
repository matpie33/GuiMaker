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
import com.guimaker.list.myList.*;
import com.guimaker.list.myList.filtering.ListFilteringPanelCreator;
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
	private ListFilteringPanelCreator<Word> listFilteringPanelCreator;
	private boolean isInitialized = false;
	private ListConfiguration listConfiguration;
	private ListPanelCreator<Word> listPanelCreator;
	private ListPanelUpdater listPanelUpdater;

	public ListViewManager(ListConfiguration<Word> listConfiguration,
			ListWordsController<Word> controller) {
		this.listConfiguration = listConfiguration;
		listPanelCreator = new ListPanelCreator<>(listConfiguration, this,
				controller);

		listFilteringPanelCreator = new ListFilteringPanelCreator<>();
		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		listWordsController = controller;
		loadNextWordsHandler = new LoadNextWordsHandler();
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, listPanelCreator.getRowsPanel());
		this.listRowCreator = listConfiguration.getListRowCreator();
		listInputsSelectionManager = listConfiguration.getAllInputsSelectionManager();
		listPanelUpdater = new ListPanelUpdater(listPanelCreator,
				listConfiguration);
		listPanelCreator.createPanel();
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

		CommonListElements commonListElements = listPanelCreator.createCommonListElements(
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
		return listWordsController.createButtonShowNextOrPreviousWords
				(loadWordsHandler);
	}

	public String createTextForRowNumber(int rowNumber) {
		return listPanelCreator.createTextForRowNumber(rowNumber);
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

				JPanel panel = listFilteringPanelCreator.createPanel(listRow,
						listPanelCreator.createButtonClearFilter());
				listRow.getRowPropertiesData()
					   .values()
					   .forEach(listProperty -> {
						   listProperty.getFilteringTextComponent()
									   .getDocument()
									   .addDocumentListener(
											   listWordsController.createActionFilterImmediately());
					   });
				listPanelUpdater.addPanelToFilterPanel(panel);
				listPanelCreator.addHotkey(KeyModifiers.CONTROL,
						KeyEvent.VK_SPACE,
						listFilteringPanelCreator.createActionSwitchComboboxValue(),
						listPanelCreator.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				listPanelCreator.addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						listFilteringPanelCreator.createActionFocusAndSelectAllInFilterTextField(),
						listPanelCreator.getPanel(),
						HotkeysDescriptions.FOCUS_FILTERING_PANEL);
			}
			else {
				listPanelUpdater.removeFilterPanel();
			}
		}

		listPanelUpdater.disablePanelOpacityIfWithoutAddAndSearch();

	}

	public JTextComponent getFilterComponent() {
		return listFilteringPanelCreator.getFilteringInput();
	}

	public ListElementPropertyManager getFilterInputPropertyManager() {
		return listFilteringPanelCreator.getPropertyManagerForInput();
	}

	public void removeWordsFromRangeInclusive(Range range) {
		listPanelCreator.getRowsPanel()
						.removeRowsInclusive(range.getRangeStart(),
								range.getRangeEnd());
	}

	public void clearHighlightedRow(JComponent row) {
		listPanelUpdater.clearHighlightedRow(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = highlightRow(row);
		scrollTo(listPanelCreator.getRowsPanel()
								 .getRows()
								 .get(rowNumber));
	}

	public int highlightRow(JComponent row) {
		int rowNumber = listPanelCreator.getRowsPanel()
										.getIndexOfPanel(row);
		changePanelColor(rowNumber,
				applicationChangesManager.getApplicationWindow()
										 .getApplicationConfiguration()
										 .getListRowHighlightColor());

		listPanelCreator.getRowsPanel()
						.getPanel()
						.repaint();
		return rowNumber;
	}

	private void changePanelColor(int rowNumber, Color color) {
		listPanelCreator.getRowsPanel()
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
		return listPanelCreator.getRowsPanel()
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
		CommonListElements commonListElements = listPanelCreator.createCommonListElements(
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
		return listFilteringPanelCreator.getFilteringInput()
										.hasFocus();
	}

	public InputGoal getInputGoal() {
		return inputGoal;
	}

	public void addElementsForEmptyList() {
		listPanelCreator.addElementsForEmptyList();
	}

	public JPanel getPanel() {
		return listPanelCreator.getPanel();
	}

	public void updateRowsPanel() {
		listPanelUpdater.updatePanel();
	}

	public void clearRowsPanel() {
		listPanelUpdater.clearRowsPanel();
	}

	public void enableOrDisableLoadWordsButton(boolean shouldDisable,
			ListWordsLoadingDirection direction) {
		listPanelUpdater.enableOrDisableLoadWordsButton(shouldDisable, direction);
	}
}
