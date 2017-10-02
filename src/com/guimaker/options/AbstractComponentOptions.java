package com.guimaker.options;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.Border;

public abstract class AbstractComponentOptions<Options extends AbstractComponentOptions<Options>> {

	private boolean opaque = true;
	private Color foregroundColor;
	private Border border;
	private Color backgroundColor;
	private String text;
	private Dimension preferredSize;

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public Border getBorder() {
		return border;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public String getText() {
		return text;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public Options opaque(boolean opaque) {
		this.opaque = opaque;
		return getThis();
	}

	public Options foregroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		return getThis();
	}

	public Options border(Border border) {
		this.border = border;
		return getThis();
	}

	public Options backgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return getThis();
	}

	public Options text(String text) {
		this.text = text;
		return getThis();
	}

	public Options preferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
		return getThis();
	}

	public abstract Options getThis();

}
