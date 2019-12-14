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
			for (int i = 0; i < row.getUiComponents()
								   .size(); i++) {

				GridBagConstraints constraints = createConstraintsForElementsInsideRow(
						i, numberOfRowsSharingColumnSize);
				currentPanel.add(row.getUiComponents()
									.get(i), constraints);

			}
			GridBagConstraints constraintsForRow = createConstraintsForNewRow();
			this.panel.add(currentPanel, constraintsForRow);
			numberOfRows++;
		}
	}

	private GridBagConstraints createConstraintsForNewRow() {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(0, 0, 0, 0);
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			int indexOfElement, int numberOfRowsSharingColumnSize) {
		GridBagConstraints constraints = new GridBagConstraints();
		int insetLeft = 0;
		int insetTop = 0;
		if (indexOfElement == 0) {
			insetLeft = spaceBetweenElementsHorizontally;
		}
		if (numberOfRows == 0) {
			insetTop = spaceBetweenElementsVertically;
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
