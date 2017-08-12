package com.guimaker.row;

import javax.swing.JComponent;

public class RowMaker {

	public static HorizontallyFilledRow createHorizontallyFilledRow(JComponent... components) {
		return new HorizontallyFilledRow(false, components);
	}

	public static SimpleRow createVerticallyFilledRow(JComponent... components) {
		return new SimpleRow(true, components);
	}

	public static HorizontallyFilledRow createBothSidesFilledRow(JComponent... components) {
		return new HorizontallyFilledRow(true, components);
	}

	public static SimpleRow createUnfilledRow(Anchor anchor, JComponent... components) {
		SimpleRow s = new SimpleRow(false, components);
		s.setAnchor(anchor.getAnchor());
		return s;
	}

}
