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
			addElementsToPanel(currentPanel, numberOfRowsSharingColumnSize,
					row);
			GridBagConstraints constraintsForRow = createConstraintsForNewRow();
			this.panel.add(currentPanel, constraintsForRow);
			numberOfRows++;
		}
	}

	private void addElementsToPanel(JPanel currentPanel,
			int numberOfRowsSharingColumnSize, PanelRows row) {
		for (int i = 0; i < row.getUiComponents()
							   .size(); i++) {

			JComponent element = row.getUiComponents()
									.get(i);
			GridBagConstraints constraints = createConstraintsForElementsInsideRow(
					i, element, numberOfRowsSharingColumnSize, row);
			currentPanel.add(element, constraints);

		}
	}

	private GridBagConstraints createConstraintsForNewRow() {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			int indexOfElement, JComponent element,
			int numberOfRowsSharingColumnSize, PanelRows row) {
		GridBagConstraints constraints = new GridBagConstraints();
		int insetLeft = 0;
		int insetTop = 0;
		if (indexOfElement == 0) {
			insetLeft = spaceBetweenElementsHorizontally;
		}
		if (numberOfRows == 0) {
			insetTop = spaceBetweenElementsVertically;
		}
		if (row.shouldFillElement(element)) {
			constraints.fill = row.getFillType()
								  .getGridBagConstraintsFilling();
			constraints.weightx = row.getFillType()
									 .getWeightX();
			constraints.weighty = row.getFillType()
									 .getWeightY();
		}

		constraints.gridy = numberOfRowsSharingColumnSize - 1;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(insetTop, insetLeft,
				spaceBetweenElementsVertically,
				spaceBetweenElementsHorizontally);
		constraints.gridx = indexOfElement;
		return constraints;
	}

	public JPanel getPanel() {
		return panel;
	}
}
