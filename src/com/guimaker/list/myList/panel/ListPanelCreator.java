package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.DialogWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.*;
import com.guimaker.model.ListRow;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.utilities.ColorChanger;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.utilities.Range;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListPanelCreator<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(550, 100);
	private JLabel titleLabel;
	private ListRowCreator<Word> listRow;
	private ApplicationChangesManager applicationChangesManager;
	private AbstractButton buttonLoadNextWords;
	private AbstractButton buttonLoadPreviousWords;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private List<AbstractButton> navigationButtons;
	private JComponent listElementsPanel;
	private Color labelsColor = Color.WHITE;
	private InputGoal inputGoal;
	private ListSearchPanelCreator<Word> listSearchPanelCreator;
	private final static String UNIQUE_NAME = "list panel creator";
	private boolean isInitialized = false;
	private MainPanel filterPanel;
	private ListElementsCreator<Word> listElementsCreator;
	private ListConfiguration listConfiguration;

	public ListPanelCreator(ListConfiguration listConfiguration,
			ApplicationChangesManager applicationChangesManager,
			ListRowCreator<Word> listRow, ListWordsController<Word> controller,
			MyList<Word> myList) {
		this.listConfiguration = listConfiguration;
		mainPanel.setRowsBorder(null);
		listElementsCreator = new ListElementsCreator<>(controller, this,
				myList, applicationChangesManager.getApplicationWindow());
		filterPanel = new MainPanel();
		filterPanel.setGapsBetweenRowsTo0();
		filterPanel.setRowsBorder(getDefaultBorder());
		listSearchPanelCreator = new ListSearchPanelCreator<>();
		this.applicationChangesManager = applicationChangesManager;
		listWordsController = controller;

		Color contentColor = BasicColors.PURPLE_DARK_1;
		rowsPanel = new MainPanel(
				new PanelConfiguration().setColorToUse(contentColor)
										.setPanelDisplayMode(
												listConfiguration.getDisplayMode())
										.putRowsAsHighestAsPossible());
		boolean hasParentList =
				listConfiguration.getParentListAndWordContainingThisList()
						!= null;
		setParentDialog(applicationChangesManager.getApplicationWindow());
		if (hasParentList) {
			mainPanel.setRowColor(ColorChanger.makeLighter(contentColor));
			mainPanel.setBackgroundColor(null);
			rowsPanel.setWrappingPanelBorder(getDefaultBorder());
		}

		titleLabel = GuiElementsCreator.createLabel(new ComponentOptions());
		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				rowsPanel);
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, rowsPanel);
		this.listRow = listRow;

		navigationButtons = new ArrayList<>();
		addNavigationButtons(
				listConfiguration.getAdditionalNavigationButtons());
		listInputsSelectionManager = listConfiguration.getAllInputsSelectionManager();
		addElementsForEmptyList();
		createButtonsShowNextAndPrevious();
		initializeNavigationButtons();
		createRootPanel();
		createMainPanelElements();
	}

	private void initializeNavigationButtons() {
		if (listConfiguration.isWordAddingEnabled()) {
			navigationButtons.add(listElementsCreator.createButtonAddWord());
		}
		setNavigationButtons(
				navigationButtons.toArray(new AbstractButton[] {}));
	}

	public void addElementsForEmptyList() {
		rowsPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				GuiElementsCreator.createLabel(new ComponentOptions().text(
						com.guimaker.strings.Prompts.EMPTY_LIST)),
				listElementsCreator.createButtonAddRow(InputGoal.EDIT)));
	}

	public void inheritScrollPane() {
		listConfiguration.inheritScrollbar(true);
	}

	private void addNavigationButtons(AbstractButton... buttons) {
		Collections.addAll(navigationButtons, buttons);
	}

	public LoadNextWordsHandler getLoadNextWordsHandler() {
		return loadNextWordsHandler;
	}

	public LoadPreviousWordsHandler getLoadPreviousWordsHandler() {
		return loadPreviousWordsHandler;
	}

	private void createButtonsShowNextAndPrevious() {
		buttonLoadNextWords = listElementsCreator.createAndAddButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST);
		buttonLoadPreviousWords = listElementsCreator.createAndAddButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST);
		buttonLoadNextWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadNextWordsHandler));
		buttonLoadPreviousWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadPreviousWordsHandler));
		if (!listConfiguration.isShowButtonsLoadNextPreviousWords()) {
			buttonLoadPreviousWords.setVisible(false);
			buttonLoadNextWords.setVisible(false);
		}
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler,
			InputGoal inputGoal) {
		this.inputGoal = inputGoal;
		if (!isInitialized) {
			createElements();
		}

		CommonListElements commonListElements = listElementsCreator.createCommonListElements(
				word, inputGoal, rowNumber, labelsColor);
		MainPanel rowPanel = null;
		if (shouldShowWord) {
			ListRowData<Word> listRow = this.listRow.createListRow(word,
					commonListElements, inputGoal);
			rowPanel = listRow.getRowPanel();
			rowPanel.getPanel()
					.setOpaque(false);
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder.createRow(
					FillType.HORIZONTAL, Anchor.NORTH, rowPanel.getPanel());
			loadWordsHandler.showWord(abstractSimpleRow);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
		if (rowPanel != null && listInputsSelectionManager != null) {
			rowPanel.addManager(listInputsSelectionManager);
		}
		rowsPanel.updateView();
		return new ListRow<>(word, rowPanel,
				commonListElements.getRowNumberLabel(), rowNumber);
	}

	private AbstractAction createButtonShowNextOrPreviousWords(
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
						buttonLoadNextWords, buttonLoadPreviousWords,
						hasMoreWordsToShow);
				rowsPanel.updateView();
			}
		};

	}

	public String createTextForRowNumber(int rowNumber) {
		return listElementsCreator.createTextForRowNumber(rowNumber);
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	private void createMainPanelElements() {
		if (!listConfiguration.isSkipTitle()) {
			mainPanel.addRow(
					SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
							titleLabel));
		}
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				filterPanel.getPanel()));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				buttonLoadPreviousWords));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.BOTH, listElementsPanel));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, buttonLoadNextWords));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				navigationButtons.toArray(new JComponent[] {})));

	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		mainPanel.setBackgroundColor(dialog.getParentConfiguration()
										   .getContentPanelColor());
		filterPanel.setRowColor(BasicColors.GREEN_BRIGHT_1);
	}

	@Override
	public void createElements() {

		rowsPanel.removeRow(0);
		isInitialized = true;

		if (listConfiguration.isWordSearchingEnabled()) {
			ListRowData<Word> listRow = this.listRow.createListRow(
					listWordsController.getWordInitializer()
									   .initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE),
					InputGoal.SEARCH);
			if (!listRow.isEmpty()) {

				JPanel panel = listSearchPanelCreator.createPanel(listRow,
						listElementsCreator.createButtonClearFilter());
				listRow.getRowPropertiesData()
					   .values()
					   .forEach(listProperty -> {
						   listProperty.getFilteringTextComponent()
									   .getDocument()
									   .addDocumentListener(
											   listWordsController.createActionFilterImmediately());
					   });
				filterPanel.addRow(
						SimpleRowBuilder.createRow(FillType.HORIZONTAL,
								Anchor.WEST, panel));
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_SPACE,
						listSearchPanelCreator.createActionSwitchComboboxValue(),
						mainPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								listSearchPanelCreator.getFilteringInput()
													  .requestFocusInWindow();
								listSearchPanelCreator.getFilteringInput()
													  .selectAll();
							}
						}, mainPanel.getPanel(),
						HotkeysDescriptions.FOCUS_FILTERING_PANEL);
			}
			else {
				mainPanel.removeRowWithElements(filterPanel.getPanel());
			}
		}

		if (!listConfiguration.isWordSearchingEnabled()
				&& !listConfiguration.isWordAddingEnabled()) {
			mainPanel.getPanel()
					 .setOpaque(false);
		}

	}

	public JTextComponent getFilterComponent() {
		return listSearchPanelCreator.getFilteringInput();
	}

	public ListElementPropertyManager getFilterInputPropertyManager() {
		return listSearchPanelCreator.getPropertyManagerForInput();
	}

	private void createRootPanel() {
		if (!listConfiguration.isScrollBarInherited()) {
			parentScrollPane = listElementsCreator.createWrappingScrollPane(
					rowsPanel);
			if (!listConfiguration.isScrollBarSizeFittingContent()) {
				parentScrollPane.setPreferredSize(scrollPanesSize);
			}
			listElementsPanel = parentScrollPane;
		}
		else {
			listElementsPanel = rowsPanel.getPanel();
		}

	}

	public void removeWordsFromRangeInclusive(Range range) {
		rowsPanel.removeRowsInclusive(range.getRangeStart(),
				range.getRangeEnd());
	}

	public void clearHighlightedRow(JComponent row) {
		rowsPanel.clearPanelColor(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = highlightRow(row);
		scrollTo(rowsPanel.getRows()
						  .get(rowNumber));
	}

	public int highlightRow(JComponent row) {
		int rowNumber = rowsPanel.getIndexOfPanel(row);
		changePanelColor(rowNumber,
				applicationChangesManager.getApplicationWindow()
										 .getApplicationConfiguration()
										 .getListRowHighlightColor());

		this.rowsPanel.getPanel()
					  .repaint();
		return rowNumber;
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		if (listConfiguration.isScrollBarInherited()) {
			//TODO keep reference to the inherited scrollbar and use it to scroll
			return;
		}
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			this.parentScrollPane.getViewport()
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
				JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public int removeRow(JComponent panel) {
		int rowNumber = rowsPanel.getIndexOfPanel(panel);
		rowsPanel.removeRow(rowNumber);
		return rowNumber;
	}

	public void clear() {
		rowsPanel.clear();
		createButtonsShowNextAndPrevious();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(() -> parentScrollPane.getVerticalScrollBar()
														 .setValue(0));
	}

	public int getNumberOfListRows() {
		return rowsPanel.getNumberOfRows();
	}

	public void enableButtonShowPreviousWords() {
		buttonLoadPreviousWords.setEnabled(true);
	}

	public void toggleEnabledState() {
		rowsPanel.toggleEnabledState();
	}

	public MainPanel repaintWord(Word word, int rowNumber, JComponent oldPanel,
			InputGoal customInputGoal, boolean highlighted) {
		CommonListElements commonListElements = listElementsCreator.createCommonListElements(
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
		rowsPanel.replacePanel(oldPanel, newPanel.getPanel());
		rowsPanel.updateView();
		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listSearchPanelCreator.getFilteringInput()
									 .hasFocus();
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	public InputGoal getInputGoal() {
		return inputGoal;
	}
}
