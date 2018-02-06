package com.guimaker.enums;

import java.awt.*;

public enum Anchor {

	NORTHEAST(GridBagConstraints.NORTHEAST), NORTH(GridBagConstraints.NORTH), NORTHWEST(
			GridBagConstraints.NORTHWEST), SOUTH(GridBagConstraints.SOUTH), SOUTHEAST(
			GridBagConstraints.SOUTHEAST), SOUTHWEST(GridBagConstraints.SOUTHWEST), EAST(
			GridBagConstraints.EAST), WEST(GridBagConstraints.WEST), CENTER(
			GridBagConstraints.CENTER);

	private int anchor;

	private Anchor(int anchor) {
		this.anchor = anchor;
	}

	public int getAnchor() {
		return anchor;
	}

}
