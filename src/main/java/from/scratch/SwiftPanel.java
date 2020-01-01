package from.scratch;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwiftPanel {

	private JPanel panel;
	private RowsPreprocessor rowsPreprocessor;
	private static final boolean DEBUG_ON = false;

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
		for (PanelBuilder panelBuilder : panelBuilders) {
			JPanel currentPanel = new JPanel(new GridBagLayout());
			debug(currentPanel);
			addElementsToPanel(panelBuilder, currentPanel);
			GridBagConstraints constraintsForRow = createConstraintsForNewRow(
					panelBuilder);
			this.panel.add(currentPanel, constraintsForRow);
		}

	}

	private void debug(JPanel currentPanel) {
		if (DEBUG_ON){
			currentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		}
	}

	private void addElementsToPanel(PanelBuilder panelBuilder,
			JPanel currentPanel) {
		List<UIElementBuilder> elementsBuilders = panelBuilder.getElementsBuilders();
		for (UIElementBuilder uiElementBuilder : elementsBuilders) {
			GridBagConstraints constraints = createConstraintsForElementsInsideRow(
					uiElementBuilder, panelBuilder);
			currentPanel.add(uiElementBuilder.getUiElement(), constraints);
		}
	}

	private GridBagConstraints createConstraintsForNewRow(
			PanelBuilder panelBuilder) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = panelBuilder.getRowIndex();
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = panelBuilder.getFillType()
										  .getWeightX();
		constraints.weighty = panelBuilder.getFillType()
										  .getWeightY();
		constraints.fill = panelBuilder.getFillType()
									   .getGridBagConstraintsFilling();
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			UIElementBuilder uiElementBuilder, PanelBuilder panelBuilder) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(uiElementBuilder.getInsetTop(),
				uiElementBuilder.getInsetLeft(),
				uiElementBuilder.getInsetBottom(),
				uiElementBuilder.getInsetRight());

		constraints.fill = uiElementBuilder.getFillType()
										   .getGridBagConstraintsFilling();
		constraints.weightx = uiElementBuilder.getFillType()
											  .getWeightX();
		constraints.weighty = uiElementBuilder.getFillType()
											  .getWeightY();

		constraints.anchor = panelBuilder.isLast()
				&& uiElementBuilder.getRowIndex()
				== panelBuilder.getNumberOfRowsInPanel() - 1 ?
				GridBagConstraints.NORTHWEST :
				GridBagConstraints.WEST;
		constraints.gridy = uiElementBuilder.getRowIndex();
		constraints.gridx = uiElementBuilder.getColumnIndex();
		return constraints;
	}

	public JPanel getPanel() {
		return panel;
	}
}
