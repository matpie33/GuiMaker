package from.scratch;

import javax.swing.*;
import java.awt.*;

public class SwiftPanel {

	private JPanel panel;

	private final int spaceBetweenElementsHorizontally = 4;
	private final int spaceBetweenElementsVertically = 3;
	private int numberOfRows = 0;

	public SwiftPanel() {
		panel = createPanel();
	}

	private JPanel createPanel() {
		return new JPanel(new GridBagLayout());
	}

	public void addElements(PanelRows panelRows) {

		JPanel currentPanel = null;
		int numberOfRowsSharingColumnSize = 1;
		for (PanelRows row : panelRows.getRows()) {
			if (!row.shouldKeepColumnSizeWithRowAbove()
					|| currentPanel == null) {
				currentPanel = new JPanel(new GridBagLayout());
				numberOfRowsSharingColumnSize = 1;
			}
			else {
				numberOfRowsSharingColumnSize++;
			}
			boolean isLast = row.isLast();
			addElementsToPanel(currentPanel, numberOfRowsSharingColumnSize, row,
					isLast);
			GridBagConstraints constraintsForRow = createConstraintsForNewRow(
					isLast);
			this.panel.add(currentPanel, constraintsForRow);
			numberOfRows++;
		}
	}

	private void addElementsToPanel(JPanel currentPanel,
			int numberOfRowsSharingColumnSize, PanelRows row,
			boolean isLastRow) {
		for (int i = 0; i < row.getUiComponents()
							   .size(); i++) {

			JComponent element = row.getUiComponents()
									.get(i);
			GridBagConstraints constraints = createConstraintsForElementsInsideRow(
					i, element, numberOfRowsSharingColumnSize, row, isLastRow);
			currentPanel.add(element, constraints);

		}
	}

	private GridBagConstraints createConstraintsForNewRow(boolean last) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = last ? 1 : 0;
		constraints.fill = GridBagConstraints.BOTH;
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			int indexOfElement, JComponent element,
			int numberOfRowsSharingColumnSize, PanelRows row,
			boolean isLastRow) {
		GridBagConstraints constraints = new GridBagConstraints();
		createInsetsForElementInRow(indexOfElement, constraints);
		createFillAndWeightsForElementInRow(indexOfElement, element, row,
				isLastRow, constraints);

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRowsSharingColumnSize - 1;
		constraints.gridy = numberOfRows;
		constraints.gridx = indexOfElement;
		return constraints;
	}

	private void createFillAndWeightsForElementInRow(int indexOfElement,
			JComponent element, PanelRows row, boolean isLastRow,
			GridBagConstraints constraints) {
		if (row.shouldFillElement(element)) {
			constraints.fill = row.getFillType()
								  .getGridBagConstraintsFilling();
			constraints.weightx = row.getFillType()
									 .getWeightX();
			constraints.weighty = row.getFillType()
									 .getWeightY();
		}
		else if (row.shouldKeepColumnSizeWithRowAbove() ?
				indexOfElement == row.getHighestNumberOfColumns() - 1 :
				indexOfElement == row.getUiComponents()
									 .size() - 1) {
			constraints.weightx = 1;
		}
		if (isLastRow) {
			constraints.weighty = 1;
		}
	}

	private void createInsetsForElementInRow(int indexOfElement,
			GridBagConstraints constraints) {
		int insetLeft = 0;
		int insetTop = 0;
		if (indexOfElement == 0) {
			insetLeft = spaceBetweenElementsHorizontally;
		}
		if (numberOfRows == 0) {
			insetTop = spaceBetweenElementsVertically;
		}
		constraints.insets = new Insets(insetTop, insetLeft,
				spaceBetweenElementsVertically,
				spaceBetweenElementsHorizontally);
	}

	public JPanel getPanel() {
		return panel;
	}
}
