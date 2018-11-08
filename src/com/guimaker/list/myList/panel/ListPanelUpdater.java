package com.guimaker.list.myList.panel;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import java.awt.*;

public class ListPanelUpdater {

	private ListPanelCreator listPanelCreator;
	private ListConfiguration listConfiguration;

	public ListPanelUpdater(ListPanelCreator listPanelCreator,
			ListConfiguration listConfiguration) {
		this.listPanelCreator = listPanelCreator;
		this.listConfiguration = listConfiguration;
	}

	public void adjustVisibilityOfShowNextPreviousWordsButtons() {
		if (!listConfiguration.isShowButtonsLoadNextPreviousWords()) {
			listPanelCreator.getButtonLoadPreviousWords()
							.setVisible(false);
			listPanelCreator.getButtonLoadNextWords()
							.setVisible(false);
		}
	}

	public void removeFilterPanel() {
		listPanelCreator.getMainPanel()
						.removeRowWithElements(listPanelCreator.getFilterPanel()
															   .getPanel());
	}

	public void enableButtonLoadNextWords() {
		if (!listPanelCreator.getButtonLoadNextWords()
							 .isEnabled()) {
			listPanelCreator.getButtonLoadNextWords()
							.setEnabled(true);
		}
	}

	public void updateRowsPanel() {
		listPanelCreator.getRowsPanel()
						.updateView();
	}

	public void removeFirstRowInRowsPanel() {
		listPanelCreator.getRowsPanel()
						.removeRow(0);
	}

	public void addPanelToFilterPanel(JPanel panelToAdd) {
		listPanelCreator.getFilterPanel()
						.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
								Anchor.WEST, panelToAdd));
	}

	public void disablePanelOpacityIfWithoutAddAndSearch() {
		if (!listConfiguration.isWordSearchingEnabled()
				&& !listConfiguration.isWordAddingEnabled()) {
			listPanelCreator.getPanel()
							.setOpaque(false);
		}
	}

	public void clearHighlightedRow(JComponent row) {
		listPanelCreator.getRowsPanel()
						.clearPanelColor(row);
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

	public void removeRow(JComponent panel) {
		int rowNumber = listPanelCreator.getRowsPanel()
										.getIndexOfPanel(panel);
		listPanelCreator.getRowsPanel()
						.removeRow(rowNumber);
	}

	public void clearRowsPanel() {
		listPanelCreator.getRowsPanel()
						.clear();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(() -> listPanelCreator.getScrollPane()
														 .getVerticalScrollBar()
														 .setValue(0));
	}

	public void enableButtonLoadPreviousWords() {
		listPanelCreator.getButtonLoadPreviousWords()
						.setEnabled(true);
	}

	public void toggleRowsPanelEnabledState() {
		listPanelCreator.getRowsPanel()
						.toggleEnabledState();
	}

	public void replacePanelsInRowsPanel(JComponent oldPanel, JPanel panel) {
		listPanelCreator.getRowsPanel()
						.replacePanel(oldPanel, panel);
		listPanelCreator.getRowsPanel()
						.updateView();
	}
}
