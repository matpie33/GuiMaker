package from.scratch;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwiftPanel {

	private JPanel panel;
	private RowsPreprocessor rowsPreprocessor;
	private static final boolean DEBUG_ON = false;
	private JPanel placeholderPanel;

	public SwiftPanel() {
		panel = createPanel();
		rowsPreprocessor = new RowsPreprocessor();
	}

	private JPanel createPanel() {
		return new JPanel(new GridBagLayout());
	}

	public void addElements(PanelRow panelRow) {

		if (placeholderPanel != null) {
			this.panel.remove(placeholderPanel);
		}
		List<ProcessedPanelData> processedPanelsData = rowsPreprocessor.preprocess(
				panelRow);
		for (ProcessedPanelData processedPanelData : processedPanelsData) {
			JPanel currentPanel = createPanel(processedPanelData);
			debug(currentPanel);
			addElementsToPanel(processedPanelData, currentPanel);
			GridBagConstraints constraintsForRow = createConstraintsForNewRow(
					processedPanelData);
			if (processedPanelData.isLast()) {
				placeholderPanel = currentPanel;
			}
			this.panel.add(currentPanel, constraintsForRow);
		}

	}

	private JPanel createPanel(ProcessedPanelData processedPanelData) {
		JPanel currentPanel;
		if (processedPanelData.shouldAddElementsToPreviousPanel()) {
			currentPanel = (JPanel) this.panel.getComponents()[
					this.panel.getComponents().length - 1];
		}
		else {
			currentPanel = new JPanel(new GridBagLayout());

		}
		return currentPanel;
	}

	private void debug(JPanel currentPanel) {
		if (DEBUG_ON) {
			currentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		}
	}

	private void addElementsToPanel(ProcessedPanelData processedPanelData,
			JPanel currentPanel) {
		List<ProcessedUIElementData> processedUIElementsData = processedPanelData.getProcessedUIElementsData();
		for (ProcessedUIElementData processedUiElementData : processedUIElementsData) {
			GridBagConstraints constraints = createConstraintsForElementsInsideRow(
					processedUiElementData, processedPanelData);
			currentPanel.add(processedUiElementData.getUiElement(),
					constraints);
		}
	}

	private GridBagConstraints createConstraintsForNewRow(
			ProcessedPanelData processedPanelData) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = processedPanelData.getRowIndex();
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = processedPanelData.getFillType()
												.getWeightX();
		constraints.weighty = processedPanelData.getFillType()
												.getWeightY();
		constraints.fill = processedPanelData.getFillType()
											 .getGridBagConstraintsFilling();
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			ProcessedUIElementData processedUiElementData,
			ProcessedPanelData processedPanelData) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(processedUiElementData.getInsetTop(),
				processedUiElementData.getInsetLeft(),
				processedUiElementData.getInsetBottom(),
				processedUiElementData.getInsetRight());

		constraints.fill = processedUiElementData.getFillType()
												 .getGridBagConstraintsFilling();
		constraints.weightx = processedUiElementData.getFillType()
													.getWeightX();
		constraints.weighty = processedUiElementData.getFillType()
													.getWeightY();

		constraints.anchor = processedUiElementData.getAnchor()
												   .getAnchor();
		constraints.gridy = processedUiElementData.getRowIndex();
		constraints.gridx = processedUiElementData.getColumnIndex();
		return constraints;
	}

	public JPanel getPanel() {
		return panel;
	}
}
