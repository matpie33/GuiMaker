package com.guimaker.list.myList.panel;

import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.row.AbstractSimpleRow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class ListPanelUpdater {

	private ListPanel listPanel;
	private ListConfiguration listConfiguration;

	public ListPanelUpdater(ListPanel listPanel,
			ListConfiguration listConfiguration) {
		this.listPanel = listPanel;
		this.listConfiguration = listConfiguration;
	}

	public void adjustVisibilityOfShowNextPreviousWordsButtons() {
		if (!listConfiguration.isShowButtonsLoadNextPreviousWords()) {
			listPanel.getButtonLoadPreviousWords()
					 .setVisible(false);
			listPanel.getButtonLoadNextWords()
					 .setVisible(false);
		}
	}

	public void enableButtonLoadNextWords() {
		if (!listPanel.getButtonLoadNextWords()
					  .isEnabled()) {
			listPanel.getButtonLoadNextWords()
					 .setEnabled(true);
		}
	}

	public void updateRowsPanel() {
		listPanel.getRowsPanel()
				 .updateView();
	}

	public void removeFirstRowInRowsPanel() {
		listPanel.getRowsPanel()
				 .removeRow(0);
	}

	public void disablePanelOpacityIfWithoutAddAndSearch() {
		if (!listConfiguration.isWordSearchingEnabled()
				&& !listConfiguration.isWordAddingEnabled()) {
			listPanel.getPanel()
					 .setOpaque(false);
		}
	}

	public void clearHighlightedRow(JComponent row) {
		listPanel.getRowsPanel()
				 .clearPanelColor(row);
	}

	public void scrollTo(JComponent panel) {
		if (listConfiguration.isScrollBarInherited()) {
			//TODO keep reference to the inherited scrollbar and use it to scroll
			return;
		}
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			listPanel.getScrollPane()
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
				listConfiguration.getApplicationChangesManager()
								 .getApplicationWindow()
								 .getContainer()
								 .revalidate();
				JScrollBar scrollBar = listPanel.getScrollPane()
												.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});
	}

	public void removeRow(JComponent panel) {
		int rowNumber = listPanel.getRowsPanel()
								 .getIndexOfPanel(panel);
		listPanel.getRowsPanel()
				 .removeRow(rowNumber);
	}

	public void clearRowsPanel() {
		listPanel.getRowsPanel()
				 .clear();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(() -> listPanel.getScrollPane()
												  .getVerticalScrollBar()
												  .setValue(0));
	}

	public void enableButtonLoadPreviousWords() {
		listPanel.getButtonLoadPreviousWords()
				 .setEnabled(true);
	}

	public void toggleRowsPanelEnabledState() {
		listPanel.getRowsPanel()
				 .toggleEnabledState();
	}

	public void replacePanelsInRowsPanel(JComponent oldPanel, JPanel panel) {
		listPanel.getRowsPanel()
				 .replacePanel(oldPanel, panel);
		listPanel.getRowsPanel()
				 .updateView();
	}

	public void addWord(ListWordsLoadingDirection loadingDirection,
			AbstractSimpleRow abstractSimpleRow) {
		if (loadingDirection.equals(ListWordsLoadingDirection.NEXT)) {
			listPanel.getRowsPanel()
					 .addRow(abstractSimpleRow);
		}
		else {
			listPanel.getRowsPanel()
					 .insertRow(0, abstractSimpleRow);
		}

	}

	public void enableOrDisableLoadWordsButton(boolean shouldDisable,
			ListWordsLoadingDirection direction) {
		if (shouldDisable) {
			getButtonByDirection(direction).setEnabled(false);
		}
		else {
			AbstractButton oppositeButton = getOppositeButton(direction);
			if (!oppositeButton.isEnabled()) {
				oppositeButton.setEnabled(true);
			}
		}
	}

	private AbstractButton getOppositeButton(
			ListWordsLoadingDirection direction) {
		if (direction.equals(ListWordsLoadingDirection.NEXT)) {
			return listPanel.getButtonLoadPreviousWords();
		}
		else {
			return listPanel.getButtonLoadNextWords();
		}
	}

	private AbstractButton getButtonByDirection(
			ListWordsLoadingDirection direction) {
		if (direction.equals(ListWordsLoadingDirection.NEXT)) {
			return listPanel.getButtonLoadNextWords();
		}
		else {
			return listPanel.getButtonLoadPreviousWords();
		}
	}

	public void updatePanel() {
		listPanel.getPanel()
				 .repaint();
		listPanel.getPanel()
				 .revalidate();
	}

	public void focusFirstTextFieldInPanel (JComponent jPanel){
		if (jPanel.getComponents().length == 1
				&& jPanel.getComponents()[0] instanceof JPanel) {
			JPanel panel = (JPanel) jPanel.getComponents()[0];
			Optional<Component> firstTextField = Arrays.stream(
					panel.getComponents())
													   .filter(JTextComponent.class::isInstance)
													   .findFirst();
			SwingUtilities.invokeLater(() -> firstTextField.ifPresent(
					Component::requestFocusInWindow));
		}
	}

}
