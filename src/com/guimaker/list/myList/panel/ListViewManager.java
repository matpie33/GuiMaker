package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.ListRow;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.Range;

import javax.swing.*;
import java.awt.*;

public class ListViewManager<Word extends ListElement> {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private ListRowCreator<Word> listRowCreator;
	private ApplicationChangesManager applicationChangesManager;

	private Color labelsColor = Color.WHITE;
	private InputGoal inputGoal;
	private boolean isInitialized = false;
	private ListConfiguration listConfiguration;
	private ListPanel<Word> listPanel;
	private ListPanelUpdater listPanelUpdater;

	public ListViewManager(InsertWordPanel<Word> insertWordPanel,
			ListConfiguration<Word> listConfiguration,
			ListWordsController<Word> controller) {
		this.listConfiguration = listConfiguration;
		listPanel = new ListPanel<>(insertWordPanel, listConfiguration, this,
				controller);
		listPanelUpdater = listPanel.getListPanelUpdater();

		this.applicationChangesManager = listConfiguration.getApplicationChangesManager();
		listWordsController = controller;

		this.listRowCreator = listConfiguration.getListRowCreator();
		listInputsSelectionManager = listConfiguration.getAllInputsSelectionManager();
		initializeListPanel();

	}

	public void inheritScrollPane() {
		listConfiguration.inheritScrollbar(true);
	}

	public MainPanel getRowsPanel() {
		return listPanel.getRowsPanel();
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, ListWordsLoadingDirection loadingDirection,
			InputGoal inputGoal) {
		this.inputGoal = inputGoal;
		if (!isInitialized) {
			listPanelUpdater.removeFirstRowInRowsPanel();
			isInitialized = true;
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
			listPanelUpdater.addWord(loadingDirection, abstractSimpleRow);
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

	public String createTextForRowNumber(int rowNumber) {
		return listPanel.createTextForRowNumber(rowNumber);
	}

	private void initializeListPanel() {

		if (listConfiguration.isWordSearchingEnabled()) {
			ListRowData<Word> listRow = this.listRowCreator.createListRow(
					listWordsController.getWordInitializer()
									   .initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE,
							listWordsController.getMyList()), InputGoal.SEARCH);
			listPanel.setRowForFilteringPanel(listRow);
		}
		listPanel.createPanel();
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

	public MainPanel repaintWord(Word word, ListRow<Word> listRow,
			InputGoal customInputGoal) {
		CommonListElements commonListElements = listPanel.createCommonListElements(
				word, this.inputGoal, listRow.getRowNumber(), labelsColor);
		MainPanel newPanel = listRowCreator.createListRow(word,
				commonListElements,
				customInputGoal == null ? this.inputGoal : customInputGoal)
										   .getRowPanel();

		if (listRow.isHighlighted()) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
											 .getApplicationConfiguration()
											 .getListRowHighlightColor());
		}
		else{
			newPanel.getPanel().setOpaque(false);
		}
		if (customInputGoal != null && customInputGoal.equals(
				InputGoal.EDIT_TEMPORARILY)) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
											 .getApplicationConfiguration()
											 .getListRowEditTemporarilyColor());
			newPanel.updateView();
		}
		listPanelUpdater.replacePanelsInRowsPanel(listRow.getJPanel(),
				newPanel.getPanel());

		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listPanel.getFilteringPanel()
						.getFilteringInput()
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

	public void focusFirstTextfieldInPanel(JComponent panel) {
		listPanelUpdater.focusFirstTextFieldInPanel(panel);
	}

	public void addAdditionalNavigationButtons(AbstractButton... buttons) {
		listPanel.addAdditionalNavigationButtons(buttons);
	}
}
