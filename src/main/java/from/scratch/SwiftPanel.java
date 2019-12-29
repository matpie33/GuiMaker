package from.scratch;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwiftPanel {

	private JPanel panel;
	private RowsPreprocessor rowsPreprocessor;

	public SwiftPanel() {
		panel = createPanel();
		rowsPreprocessor = new RowsPreprocessor();
	}

	private JPanel createPanel() {
		return new JPanel(new GridBagLayout());
	}

	public void addElements(PanelRow panelRow) {

		List<PanelBuilder> panelBuilders = rowsPreprocessor.preprocess(
				panelRow);
		JPanel currentPanel = null;
		for (PanelBuilder panelBuilder : panelBuilders) {
			if (currentPanel == null || panelBuilder.requiresNewRow()) {
				currentPanel = new JPanel(new GridBagLayout());
			}
			addElementsToPanel(panelBuilder, currentPanel);
			GridBagConstraints constraintsForRow = createConstraintsForNewRow(
					panelBuilder);
			this.panel.add(currentPanel, constraintsForRow);
		}

	}

	private void addElementsToPanel(PanelBuilder panelBuilder,
			JPanel currentPanel) {
		List<UIElementBuilder> elementsBuilders = panelBuilder.getElementsBuilders();
		for (int index = 0; index < elementsBuilders.size(); index++) {
			UIElementBuilder uiElementBuilder = elementsBuilders.get(index);
			GridBagConstraints constraints = createConstraintsForElementsInsideRow(
					uiElementBuilder, panelBuilder, index);
			currentPanel.add(uiElementBuilder.getUiElement(), constraints);
		}
	}

	private GridBagConstraints createConstraintsForNewRow(
			PanelBuilder panelBuilder) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = panelBuilder.getRowIndex();
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = panelBuilder.shouldTakeAllSpaceToBottom() ? 1 : 0;
		constraints.fill = panelBuilder.shouldTakeAllSpaceToBottom() ?
				GridBagConstraints.BOTH :
				GridBagConstraints.HORIZONTAL;
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			UIElementBuilder uiElementBuilder, PanelBuilder panelBuilder,
			int indexOfElement) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(uiElementBuilder.getInsetTop(),
				uiElementBuilder.getInsetLeft(),
				uiElementBuilder.getInsetBottom(),
				uiElementBuilder.getInsetRight());
		createFillAndWeightsForElementInRow(uiElementBuilder, panelBuilder,
				constraints, indexOfElement);
		constraints.anchor = panelBuilder.isLast() ?
				GridBagConstraints.NORTHWEST :
				GridBagConstraints.WEST;
		constraints.gridy = uiElementBuilder.getRowIndex();
		constraints.gridx = uiElementBuilder.getColumnIndex();
		return constraints;
	}

	private void createFillAndWeightsForElementInRow(
			UIElementBuilder uiElementBuilder, PanelBuilder panelBuilder,
			GridBagConstraints constraints, int indexOfElement) {
		constraints.fill = uiElementBuilder.getFillType()
										   .getGridBagConstraintsFilling();
		constraints.weightx = uiElementBuilder.getFillType()
											  .getWeightX();
		constraints.weighty = uiElementBuilder.getFillType()
											  .getWeightY();
		if (indexOfElement
				== panelBuilder.getHighestNumberOfColumnsInPanel() - 1
				&& !panelBuilder.hasElementFilledHorizontallyToTheRightEdgeOfPanel()) {
			constraints.weightx = 1;
		}
		if (panelBuilder.shouldTakeAllSpaceToBottom()) {
			constraints.weighty = 1;
		}
	}


	public JPanel getPanel() {
		return panel;
	}
}
