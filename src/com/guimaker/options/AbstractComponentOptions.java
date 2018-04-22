package com.guimaker.options;

import javax.swing.border.Border;
import java.awt.*;

public abstract class AbstractComponentOptions<Options extends AbstractComponentOptions<Options>> {

	private boolean opaque = true;
	private Color foregroundColor = Color.WHITE;
	private Border border;
	private Color backgroundColor;
	private String text;
	private Dimension preferredSize;
	private float fontSize;
	private boolean hasPreferredSize;
	private Font font;

	public float getFontSize() {
		return fontSize;
	}

	public Font getFont() {
		return font;
	}

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

	public Options fontSize(float fontSize) {
		this.fontSize = fontSize;
		return getThis();
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
		hasPreferredSize = true;
		return getThis();
	}

	public Options font(Font font) {
		this.font = font;
		return getThis();
	}

	public boolean hasPreferredSize() {
		return hasPreferredSize;
	}

	public abstract Options getThis();

}
