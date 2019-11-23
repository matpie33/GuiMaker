package com.guimaker.panels.mainPanel;

import com.guimaker.row.AbstractSimpleRow;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConstraintsCreator {

	private int distanceBetweenElementsInsideRow;

	public ConstraintsCreator(int distanceBetweenElementsInsideRow) {
		this.distanceBetweenElementsInsideRow = distanceBetweenElementsInsideRow;
	}

	private GridBagConstraints initializeConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		return constraints;
	}

	public GridBagConstraints getConstraintsForComponent(JComponent component,
			Map<JComponent, Integer> componentFilling, AbstractSimpleRow row,
			int indexOfElementInRow, int numberOfElementsInRow) {

		GridBagConstraints gbc = initializeConstraints();
		gbc.gridx = indexOfElementInRow;
		if (componentFilling.containsKey(component)) {
			gbc.fill = componentFilling.get(component);
			gbc.weighty = row.isUseAllExtraVerticalSpace() ? 1 : 0;
			if (gbc.fill == GridBagConstraints.HORIZONTAL) {
				gbc.weightx = 1;
			}
			else if (gbc.fill == GridBagConstraints.VERTICAL) {
				gbc.weightx = 1;
			}
			else if (gbc.fill == GridBagConstraints.BOTH) {
				gbc.weightx = 1;
				gbc.weighty = 1;
			}
		}
		else {
			gbc.weightx = 0;
			gbc.weighty = 0;
		}
		if (indexOfElementInRow == numberOfElementsInRow - 1
				&& gbc.fill != GridBagConstraints.HORIZONTAL) {
			gbc.weightx = 1;
		}

		if (indexOfElementInRow != numberOfElementsInRow - 1) {
			gbc.insets.right = distanceBetweenElementsInsideRow;
		}
		return gbc;

	}

}
