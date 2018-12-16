package com.guimaker.utilities;

import java.awt.*;

public class ColorChanger {

	private static final double TINT_FACTOR = 0.25;

	public static Color makeLighter(Color c) {
		return new Color(makeComponentLighter(c.getRed()),
				makeComponentLighter(c.getGreen()),
				makeComponentLighter(c.getBlue()));
	}

	private static int makeComponentLighter(int colorComponent) {
		return colorComponent + (int) ((255 - colorComponent) * TINT_FACTOR);
	}

}
