package com.guimaker.list.myList.panel;

import com.guimaker.application.DialogWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.Prompts;
import com.guimaker.utilities.ColorChanger;
import com.guimaker.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListConfiguration listConfiguration;
	private ListElementsCreator<Word> listElementsCreator;
	private String title;
	private MainPanel filterPanel;
	private ListViewManager listViewManager;
	private JScrollPane scrollPane;
	private JComponent listElementsPanel;
	private final Dimension scrollPanesSize = new Dimension(550, 100);
	private Color contentColor = BasicColors.PURPLE_DARK_1;
	private MainPanel rowsPanel;
	private AbstractButton buttonLoadPreviousWords;
	private AbstractButton buttonLoadNextWords;

	public ListPanel(ListConfiguration listConfiguration,
			ListViewManager<Word> listViewManager,
			ListWordsController<Word> controller) {
		listElementsCreator = new ListElementsCreator<>(controller,
				this);
		this.listConfiguration = listConfiguration;
		this.title = listConfiguration.getTitle();
		this.listViewManager = listViewManager;
		setParentDialog(listConfiguration.getDialogWindow());
		setMainPanelProperties();
		createRowsPanel();
		createFilterPanel();
	}

	private void createFilterPanel() {
		filterPanel = new MainPanel();
		filterPanel.setGapsBetweenRowsTo0();
		filterPanel.setRowsBorder(getDefaultBorder());
		filterPanel.setRowColor(BasicColors.GREEN_BRIGHT_1);
	}

	@Override
	public void createElements() {
		createRootPanel();
		addMainPanelElements();
	}



	private void setMainPanelProperties() {
		if (hasParentList()) {
			mainPanel.setRowColor(ColorChanger.makeLighter(contentColor));
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
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				filterPanel.getPanel()));
		buttonLoadPreviousWords = listElementsCreator.createButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST,
				listViewManager.createButtonShowNextOrPreviousWords(
						listViewManager.getLoadPreviousWordsHandler()));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				buttonLoadPreviousWords));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.BOTH, listElementsPanel));
		buttonLoadNextWords = listElementsCreator.createButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST,
				listViewManager.createButtonShowNextOrPreviousWords(
						listViewManager.getLoadNextWordsHandler()));
		mainPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, buttonLoadNextWords));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				getNavigationButtons()));
	}

	private List<AbstractButton> getNavigationButtons() {
		List<AbstractButton> navigationButtons = new ArrayList<>(Arrays.asList(
				listConfiguration.getAdditionalNavigationButtons()));
		if (listConfiguration.isWordAddingEnabled()) {
			navigationButtons.add(listElementsCreator.createButtonAddWord());
		}
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
										.putRowsAsHighestAsPossible());

		addElementsForEmptyList();

		boolean hasParentList = hasParentList();
		if (hasParentList) {
			rowsPanel.setWrappingPanelBorder(getDefaultBorder());
		}
		rowsPanel.setOpaqueRows(false);
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
		if (hasMoreThan1Panel()) {
			mainPanel.setBackgroundColor(dialog.getParentConfiguration()
											   .getContentPanelColor());
		}
		else {
			mainPanel.getPanel()
					 .setOpaque(false);
		}

	}

	private boolean hasMoreThan1Panel() {
		return listConfiguration.isShowButtonsLoadNextPreviousWords()
				|| listConfiguration.isWordSearchingEnabled()
				|| listConfiguration.isWordAddingEnabled();
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public MainPanel getFilterPanel() {
		return filterPanel;
	}
}
