package com.guimaker.panels;

import com.guimaker.enums.Direction;

import javax.swing.*;
import java.awt.*;

public class ElementsShifter {

	private JPanel panel;
	private boolean shouldPutRowsHighestAsPossible;

	public ElementsShifter(JPanel panel,
			boolean shouldPutRowsHighestAsPossible) {
		this.panel = panel;
		this.shouldPutRowsHighestAsPossible = shouldPutRowsHighestAsPossible;
	}

	public void shiftElements (Direction direction, int startIndex,
			int absoluteIncrementDecrementValue){
		Component[] components = panel.getComponents();
		for (Component component : components) {
			GridBagConstraints c = getConstraintsForRow(component);
			if (c.gridy < startIndex){
				continue;
			}
			if (direction.equals(Direction.FORWARD)) {
				c.gridy += absoluteIncrementDecrementValue;
			}
			else if (direction.equals(Direction.BACKWARD)) {
				c.gridy -= absoluteIncrementDecrementValue;
			}
			if (!shouldPutRowsHighestAsPossible
					&& c.fill != GridBagConstraints.BOTH) {
				c.weighty = 0;
			}
			GridBagLayout g = (GridBagLayout) panel.getLayout();
			g.setConstraints(component, c);
		}
	}

	public GridBagConstraints getConstraintsForRow(Component row) {
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		return g.getConstraints(row);
	}


}
