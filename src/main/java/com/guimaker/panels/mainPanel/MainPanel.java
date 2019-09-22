package com.guimaker.panels.mainPanel;

import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.HorizontallyNonscrollablePanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.ComplexRow;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;

public class MainPanel {

	private JPanel panel;

	private static Color defaultColor;

	private MainPanelViewUpdater mainPanelViewUpdater;

	public MainPanel(PanelConfiguration panelConfiguration) {
		createPanel(panelConfiguration);
		mainPanelViewUpdater = new MainPanelViewUpdater(panelConfiguration,
				panel);
	}

	public static void setDefaultColor(Color defaultColor) {
		MainPanel.defaultColor = defaultColor;
	}

	public void setGapsBetweenRowsTo0() {
		mainPanelViewUpdater.setGapsBetweenRowsTo0();
	}

	public void setRowsBorder(Border border) {
		mainPanelViewUpdater.setRowsBorder(border);
	}

	public void setWrappingPanelBorder(Border border) {
		mainPanelViewUpdater.setWrappingPanelBorder(border);
	}

	public void setRowColor(Color color) {
		mainPanelViewUpdater.setRowColor(color);
	}

	public MainPanel() {
		this(new PanelConfiguration());
	}
	//TODO "do not create many top level containers (JPanel?), instead reuse existing

	// by calling removAll" - try to optimize drawing

	private void createPanel(PanelConfiguration panelConfiguration) {
		if (panelConfiguration.isScrollHorizontally()) {
			panel = new JPanel();
		}
		else {
			panel = new HorizontallyNonscrollablePanel();
		}
		if (panelConfiguration.getColorToUse() == null
				&& panelConfiguration.isOpaque()) {
			panel.setBackground(defaultColor);
		}
		else if (panelConfiguration.getColorToUse() != null) {
			panel.setBackground(panelConfiguration.getColorToUse());
		}
		else if (!panelConfiguration.isOpaque()) {
			panel.setOpaque(false);
		}
		panel.setBorder(panelConfiguration.getBorder());
		panel.setLayout(new GridBagLayout());
	}

	public void addSwitchBetweenInputsFailedListener(
			SwitchBetweenInputsFailListener listener) {
		mainPanelViewUpdater.addSwitchBetweenInputsFailedListener(listener);
	}

	public JComponent addRow(AbstractSimpleRow abstractSimpleRows) {
		return mainPanelViewUpdater.addRow(abstractSimpleRows);
	}

	public JTextComponent getSelectedInput() {
		return mainPanelViewUpdater.getSelectedInput();
	}

	public boolean hasSelectedInput() {
		return mainPanelViewUpdater.hasSelectedInput();
	}

	public int getSelectedInputIndex() {
		return mainPanelViewUpdater.getSelectedInputIndex();
	}

	public void selectInputInColumn(int columnNumber) {
		mainPanelViewUpdater.selectInputInColumn(columnNumber);
	}

	public void addManager(
			ListInputsSelectionManager listInputsSelectionManager) {
		mainPanelViewUpdater.addManager(listInputsSelectionManager);
	}

	public void toggleEnabledState() {
		mainPanelViewUpdater.toggleEnabledState();
	}

	public void clearSelectedInput() {
		mainPanelViewUpdater.clearSelectedInput();
	}

	public void setPadding(int padding) {
		mainPanelViewUpdater.setPadding(padding);
	}

	public int getNumberOfRows() {
		return mainPanelViewUpdater.getNumberOfRows();
	}

	public List<JComponent> getRows() {
		return mainPanelViewUpdater.getRows();
	}

	public JPanel getPanel() {
		return mainPanelViewUpdater.getPanel();
	}

	public void setBackgroundColor(Color c) {
		mainPanelViewUpdater.setBackgroundColor(c);
	}

	public void setPanelColor(int rowNumber, Color color) {
		mainPanelViewUpdater.setPanelColor(rowNumber, color);
	}

	public void clearPanelColor(JComponent panel) {
		mainPanelViewUpdater.clearPanelColor(panel);
	}

	public void selectNextInputInSameRow() {
		mainPanelViewUpdater.selectNextInputInSameRow();
	}

	public void selectPreviousInputInSameRow() {
		mainPanelViewUpdater.selectPreviousInputInSameRow();
	}

	public void updateView() {
		mainPanelViewUpdater.updateView();
	}

	public void removeRow(int number) {
		mainPanelViewUpdater.removeRow(number);
	}

	public void removeLastRow() {
		mainPanelViewUpdater.removeLastRow();
	}

	public void removeRowWithElements(Component... elements) {
		mainPanelViewUpdater.removeRowWithElements(elements);
	}

	public void replacePanel(JComponent oldPanel, JComponent newPanel) {
		mainPanelViewUpdater.replacePanel(oldPanel, newPanel);
	}

	public void insertRow(int number, AbstractSimpleRow row) {
		mainPanelViewUpdater.insertRow(number, row);
	}

	public int getIndexOfPanel(JComponent panel) {
		return mainPanelViewUpdater.getIndexOfPanel(panel);
	}

	public void clear() {
		mainPanelViewUpdater.clear();
	}

	public void removeRowsInclusive(int start, int end) {
		mainPanelViewUpdater.removeRowsInclusive(start, end);
	}

	public void addRows(ComplexRow complexRow) {
		mainPanelViewUpdater.addRows(complexRow);
	}

	public void addRowsOfElementsInColumn(ComplexRow complexRow) {
		mainPanelViewUpdater.addRowsOfElementsInColumn(complexRow);
	}

	public void insertElementInPlaceOfElement(JComponent elementToAdd,
			JComponent elementToReplace) {
		mainPanelViewUpdater.insertElementInPlaceOfElement(elementToAdd,
				elementToReplace);
	}

	public void changeEnabledStateOfLastElementInRow(int rowNumber,
			boolean enabled) {
		mainPanelViewUpdater.changeEnabledStateOfLastElementInRow(rowNumber,
				enabled);
	}

	public void addElementsInColumn(AbstractSimpleRow abstractSimpleRow) {
		mainPanelViewUpdater.addElementsInColumn(abstractSimpleRow);
	}

	public int getIndexOfRowContainingElements(Component... elements) {
		return mainPanelViewUpdater.getIndexOfRowContainingElements(elements);
	}

}
