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

		panelRows.getRows()
				 .forEach(row -> {
					 JPanel panel = new JPanel(new GridBagLayout());
					 for (int i = 0; i < row.getUiComponents()
											.size(); i++) {

						 GridBagConstraints constraints = createConstraintsForElementsInsideRow(
								 i);
						 panel.add(row.getUiComponents()
									  .get(i), constraints);

					 }
					 GridBagConstraints constraintsForRow = createConstraintsForNewRow();
					 this.panel.add(panel, constraintsForRow);
					 numberOfRows++;
				 });

	}

	private GridBagConstraints createConstraintsForNewRow() {
		GridBagConstraints constraints = new GridBagConstraints();
		int insetTop = 0;
		if (numberOfRows == 0) {
			insetTop = spaceBetweenElementsVertically;
		}
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(insetTop, 0,
				spaceBetweenElementsVertically, 0);
		return constraints;

	}

	private GridBagConstraints createConstraintsForElementsInsideRow(
			int indexOfElement) {
		GridBagConstraints constraints = new GridBagConstraints();
		int insetLeft = 0;
		if (indexOfElement == 0) {
			insetLeft = spaceBetweenElementsHorizontally;
		}
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = numberOfRows;
		constraints.insets = new Insets(0, insetLeft, 0,
				spaceBetweenElementsHorizontally);
		constraints.gridx = indexOfElement;
		return constraints;
	}

	public JPanel getPanel() {
		return panel;
	}
}
