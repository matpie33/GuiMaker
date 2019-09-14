package com.guimaker.list.myList.panel;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.*;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.filtering.ListFilteringController;
import com.guimaker.list.myList.filtering.ListFilteringPanel;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.ListColors;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.strings.Prompts;
import com.guimaker.utilities.ColorChanger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListColors listColors;
	private ListConfiguration listConfiguration;
	private ListElementsCreator<Word> listElementsCreator;
	private String title;
	private MainPanel filterPanel;
	private JScrollPane scrollPane;
	private JComponent listElementsPanel;
	private final Dimension scrollPanesSize = new Dimension(550, 100);
	private Color contentColor;
	private MainPanel rowsPanel;
	private AbstractButton buttonLoadPreviousWords;
	private AbstractButton buttonLoadNextWords;
	private ListPanelUpdater listPanelUpdater;
	private ListFilteringPanel<Word> listFilteringPanel;
	private ListRowData<Word> rowForFilteringPanel;
	private List<AbstractButton> navigationButtons;

	public ListPanel(InsertWordPanel<Word> insertWordPanel,
			ListConfiguration listConfiguration,
			ListViewManager<Word> listViewManager,
			ListWordsController<Word> controller) {
		listColors = listConfiguration.getApplicationChangesManager()
									  .getApplicationWindow()
									  .getApplicationConfiguration()
									  .getListColors();
		contentColor = listColors.getRowColor();
		listFilteringPanel = new ListFilteringController<>(listViewManager,
				controller).getListFilteringPanel();
		listPanelUpdater = new ListPanelUpdater(this, listConfiguration);
		listElementsCreator = new ListElementsCreator<>(this,
				new ListActionsCreator<>(insertWordPanel, controller,
						listConfiguration, listPanelUpdater));
		this.listConfiguration = listConfiguration;
		this.title = listConfiguration.getTitle();

		setParentDialog(listConfiguration.getDialogWindow());
		setMainPanelProperties();
		createRowsPanel();
		createFilterPanel();
	}

	public void setRowForFilteringPanel(ListRowData rowForFilteringPanel) {
		this.rowForFilteringPanel = rowForFilteringPanel;
	}

	public ListPanelUpdater getListPanelUpdater() {
		return listPanelUpdater;
	}

	private void createFilterPanel() {
		filterPanel = new MainPanel();
		filterPanel.setGapsBetweenRowsTo0();
		filterPanel.setRowsBorder(getDefaultBorder());
		filterPanel.setRowColor(listColors.getFilterPanelColor());
	}

	@Override
	public void createElements() {
		createRootPanel();
		addMainPanelElements();
		addHotkeys();
		listPanelUpdater.adjustVisibilityOfShowNextPreviousWordsButtons();
	}

	private void addHotkeys() {
		if (isFilteringEnabled()) {
			addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_SPACE,
					listFilteringPanel.createActionSwitchComboboxValue(),
					getPanel(), HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
			addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
					listFilteringPanel.createActionFocusAndSelectAllInFilterTextField(),
					getPanel(), HotkeysDescriptions.FOCUS_FILTERING_PANEL);
		}
	}

	private void setMainPanelProperties() {
		mainPanel.setRowColor(null);
		if (hasParentList()) {
			mainPanel.setGapsBetweenRowsTo0();
			contentColor = ColorChanger.makeLighter(contentColor);
			mainPanel.setBackgroundColor(null);
		}
		mainPanel.setRowsBorder(null);
	}

	private boolean hasParentList() {
		return listConfiguration.getParentListAndWordContainingThisList()
				!= null;
	}

	private void addMainPanelElements() {
		if (!listConfiguration.isSkipTitle()) {
			mainPanel.addRow(
					SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
							listElementsCreator.createTitleLabel(title)));
		}
		if (isFilteringEnabled()) {
			filterPanel.addRow(
					SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.WEST,
							listFilteringPanel.createPanel(this,
									rowForFilteringPanel,
									createButtonClearFilter(),
									listConfiguration)));
		}
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				filterPanel.getPanel()));
		buttonLoadPreviousWords = listElementsCreator.createButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST,
				ListWordsLoadingDirection.PREVIOUS);
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				buttonLoadPreviousWords));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.BOTH, listElementsPanel));
		buttonLoadNextWords = listElementsCreator.createButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST,
				ListWordsLoadingDirection.NEXT);
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, buttonLoadNextWords));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				getNavigationButtons()));
	}

	private boolean isFilteringEnabled() {
		return listConfiguration.isWordSearchingEnabled()
				&& !rowForFilteringPanel.isEmpty();
	}

	private List<AbstractButton> getNavigationButtons() {
		//TODO refactor this method so that it initializes navigation buttons
		// only once at constructor, and then it only gets navigation buttons
		List<AbstractButton> navigationButtons = new ArrayList<>(Arrays.asList(
				listConfiguration.getAdditionalNavigationButtons()));
		if (listConfiguration.isWordAddingEnabled()) {
			navigationButtons.add(listElementsCreator.getButtonAddWord());
		}
		this.navigationButtons = navigationButtons;
		return navigationButtons;
	}

	private void createRootPanel() {
		if (!listConfiguration.isScrollBarInherited()) {
			scrollPane = listElementsCreator.createWrappingScrollPane(
					rowsPanel);
			if (!listConfiguration.isScrollBarSizeFittingContent()) {
				scrollPane.setPreferredSize(scrollPanesSize);
			}
			listElementsPanel = scrollPane;
		}
		else {
			listElementsPanel = rowsPanel.getPanel();
		}

	}

	private void createRowsPanel() {
		rowsPanel = new MainPanel(
				new PanelConfiguration().setColorToUse(contentColor)
										.setPanelDisplayMode(
												listConfiguration.getDisplayMode())
										.setOpaque(false)
										.putRowsAsHighestAsPossible());
		rowsPanel.setGapsBetweenRowsTo0();

		addElementsForEmptyList();

		boolean hasParentList = hasParentList();
		if (hasParentList) {
			rowsPanel.setWrappingPanelBorder(getDefaultBorder());
		}
	}

	public CommonListElements createCommonListElements(Word word,
			InputGoal inputGoal, int rowNumber, Color labelsColor) {
		return listElementsCreator.createCommonListElements(word, inputGoal,
				rowNumber, labelsColor);
	}

	public AbstractButton getButtonLoadNextWords() {
		return buttonLoadNextWords;
	}

	public AbstractButton getButtonLoadPreviousWords() {
		return buttonLoadPreviousWords;
	}

	@Override
	public String getUniqueName() {
		return null;
	}

	public String createTextForRowNumber(int rowNumber) {
		return listElementsCreator.createTextForRowNumber(rowNumber);
	}

	public AbstractButton createButtonClearFilter() {
		return listElementsCreator.createButtonClearFilter();
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void addElementsForEmptyList() {
		rowsPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				GuiElementsCreator.createLabel(
						new ComponentOptions().text(Prompts.EMPTY_LIST)),
				listElementsCreator.createButtonAddRow(InputGoal.EDIT)));
	}

	public MainPanel getRowsPanel() {
		return rowsPanel;
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		mainPanel.setBackgroundColor(dialog.getParentConfiguration()
										   .getListColors()
										   .getBackgroundColor());

	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public MainPanel getFilterPanel() {
		return filterPanel;
	}

	public ListFilteringPanel getFilteringPanel() {
		return listFilteringPanel;
	}

	public void addAdditionalNavigationButtons(AbstractButton... buttons) {
		mainPanel.removeRowWithElements(
				navigationButtons.toArray(new JComponent[] {}));
		navigationButtons.addAll(Arrays.asList(buttons));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, navigationButtons));
	}
}
