package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.*;
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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ListViewManager<Word extends ListElement> {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private ListRowCreator<Word> listRow;
	private ApplicationChangesManager applicationChangesManager;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private Color labelsColor = Color.WHITE;
	private InputGoal inputGoal;
	private ListSearchPanelCreator<Word> listSearchPanelCreator;
	private final static String UNIQUE_NAME = "list panel creator";
	private boolean isInitialized = false;
	private ListConfiguration listConfiguration;
	private ListPanelCreator listPanelCreator;

	public ListViewManager(ListConfiguration listConfiguration,
			ApplicationChangesManager applicationChangesManager,
			ListRowCreator<Word> listRow, ListWordsController<Word> controller,
			String title) {
		this.listConfiguration = listConfiguration;
		listPanelCreator = new ListPanelCreator(listConfiguration, title, this,
				controller);
		listPanelCreator.createPanel();
		listPanelCreator.setParentDialog(
				applicationChangesManager.getApplicationWindow());

		listSearchPanelCreator = new ListSearchPanelCreator<>();
		this.applicationChangesManager = applicationChangesManager;
		listWordsController = controller;

		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				listPanelCreator.getRowsPanel());
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, listPanelCreator.getRowsPanel());
		this.listRow = listRow;

		listInputsSelectionManager = listConfiguration.getAllInputsSelectionManager();

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
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler,
			InputGoal inputGoal) {
		this.inputGoal = inputGoal;
		if (!isInitialized) {
			createElements();
		}

		CommonListElements commonListElements = listPanelCreator.createCommonListElements(
				word, inputGoal, rowNumber, labelsColor);
		MainPanel rowPanel = null;
		if (shouldShowWord) {
			ListRowData<Word> listRow = this.listRow.createListRow(word,
					commonListElements, inputGoal);
			rowPanel = listRow.getRowPanel();
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder.createRow(
					FillType.HORIZONTAL, Anchor.NORTH, rowPanel.getPanel());
			loadWordsHandler.showWord(abstractSimpleRow);
		}
		else if (!listPanelCreator.getButtonLoadNextWords()
								  .isEnabled()) {
			listPanelCreator.getButtonLoadNextWords()
							.setEnabled(true);
		}
		if (rowPanel != null && listInputsSelectionManager != null) {
			rowPanel.addManager(listInputsSelectionManager);
		}
		listPanelCreator.getRowsPanel()
						.updateView();
		return new ListRow<>(word, rowPanel,
				commonListElements.getRowNumberLabel(), rowNumber);
	}

	public AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numberOfAddedWords = listWordsController.addNextHalfOfMaximumWords(
						loadWordsHandler);
				if (numberOfAddedWords > 0) {
					removeWordsFromRangeInclusive(
							loadWordsHandler.getRangeOfWordsToRemove(
									numberOfAddedWords));
				}
				boolean hasMoreWordsToShow = numberOfAddedWords == Math.ceil(
						(double) listWordsController.getMaximumWordsToShow()
								/ 2);
				loadWordsHandler.enableOrDisableLoadWordsButtons(
						listPanelCreator.getButtonLoadNextWords(),
						listPanelCreator.getButtonLoadPreviousWords(),
						hasMoreWordsToShow);
				listPanelCreator.getRowsPanel()
								.updateView();
			}
		};

	}

	public String createTextForRowNumber(int rowNumber) {
		return listPanelCreator.createTextForRowNumber(rowNumber);
	}

	public void createElements() {

		listPanelCreator.getRowsPanel()
						.removeRow(0);
		isInitialized = true;

		if (listConfiguration.isWordSearchingEnabled()) {
			ListRowData<Word> listRow = this.listRow.createListRow(
					listWordsController.getWordInitializer()
									   .initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE),
					InputGoal.SEARCH);
			if (!listRow.isEmpty()) {

				JPanel panel = listSearchPanelCreator.createPanel(listRow,
						listPanelCreator.createButtonClearFilter());
				listRow.getRowPropertiesData()
					   .values()
					   .forEach(listProperty -> {
						   listProperty.getFilteringTextComponent()
									   .getDocument()
									   .addDocumentListener(
											   listWordsController.createActionFilterImmediately());
					   });
				listPanelCreator.addPanelToFilterPanel(panel);
				listPanelCreator.addHotkey(KeyModifiers.CONTROL,
						KeyEvent.VK_SPACE,
						listSearchPanelCreator.createActionSwitchComboboxValue(),
						listPanelCreator.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				listPanelCreator.addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								listSearchPanelCreator.getFilteringInput()
													  .requestFocusInWindow();
								listSearchPanelCreator.getFilteringInput()
													  .selectAll();
							}
						}, listPanelCreator.getPanel(),
						HotkeysDescriptions.FOCUS_FILTERING_PANEL);
			}
			else {
				listPanelCreator.removeFilterPanel();
			}
		}

		if (!listConfiguration.isWordSearchingEnabled()
				&& !listConfiguration.isWordAddingEnabled()) {
			listPanelCreator.getPanel()
							.setOpaque(false);
		}

	}

	public JTextComponent getFilterComponent() {
		return listSearchPanelCreator.getFilteringInput();
	}

	public ListElementPropertyManager getFilterInputPropertyManager() {
		return listSearchPanelCreator.getPropertyManagerForInput();
	}

	public void removeWordsFromRangeInclusive(Range range) {
		listPanelCreator.getRowsPanel()
						.removeRowsInclusive(range.getRangeStart(),
								range.getRangeEnd());
	}

	public void clearHighlightedRow(JComponent row) {
		listPanelCreator.getRowsPanel()
						.clearPanelColor(row);
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
		if (listConfiguration.isScrollBarInherited()) {
			//TODO keep reference to the inherited scrollbar and use it to scroll
			return;
		}
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			listPanelCreator.getScrollPane()
							.getViewport()
							.setViewPosition(new Point(0, r));
		});
	}

	public void scrollToBottom() {
		if (listConfiguration.isScrollBarInherited()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				JScrollBar scrollBar = listPanelCreator.getScrollPane()
													   .getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public int removeRow(JComponent panel) {
		int rowNumber = listPanelCreator.getRowsPanel()
										.getIndexOfPanel(panel);
		listPanelCreator.getRowsPanel()
						.removeRow(rowNumber);
		return rowNumber;
	}

	public void clear() {
		listPanelCreator.getRowsPanel()
						.clear();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(() -> listPanelCreator.getScrollPane()
														 .getVerticalScrollBar()
														 .setValue(0));
	}

	public int getNumberOfListRows() {
		return listPanelCreator.getRowsPanel()
							   .getNumberOfRows();
	}

	public void enableButtonShowPreviousWords() {
		listPanelCreator.getButtonLoadPreviousWords()
						.setEnabled(true);
	}

	public void toggleEnabledState() {
		listPanelCreator.getRowsPanel()
						.toggleEnabledState();
	}

	public MainPanel repaintWord(Word word, int rowNumber, JComponent oldPanel,
			InputGoal customInputGoal, boolean highlighted) {
		CommonListElements commonListElements = listPanelCreator.createCommonListElements(
				word, this.inputGoal, rowNumber, labelsColor);
		MainPanel newPanel = listRow.createListRow(word, commonListElements,
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
		listPanelCreator.getRowsPanel()
						.replacePanel(oldPanel, newPanel.getPanel());
		listPanelCreator.getRowsPanel()
						.updateView();
		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listSearchPanelCreator.getFilteringInput()
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
}
