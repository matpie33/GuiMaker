package com.guimaker.enums;

import java.awt.*;

public enum FillType {
	HORIZONTAL(GridBagConstraints.HORIZONTAL), VERTICAL(GridBagConstraints.VERTICAL), NONE(
			GridBagConstraints.NONE), BOTH(GridBagConstraints.BOTH);

	private int gridBagConstraintsFilling;

	FillType(int gridBagConstraintsFilling) {
		this.gridBagConstraintsFilling = gridBagConstraintsFilling;
	}

	public int getGridBagConstraintsFilling() {
		return gridBagConstraintsFilling;
	}
}
