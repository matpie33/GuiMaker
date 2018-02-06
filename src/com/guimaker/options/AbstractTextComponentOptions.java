package com.guimaker.options;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public abstract class AbstractTextComponentOptions<TextOptions extends AbstractTextComponentOptions<TextOptions>>
		extends AbstractComponentOptions<TextOptions> {

	private boolean editable = true;
	private boolean enabled = true;
	private boolean focusable = true;
	private boolean digitsOnly = false;
	private int numberOfRows = 0;
	private int numberOfColumns = 0;
	private int maximumCharacters = 0;
	private float fontSize;

	AbstractTextComponentOptions() {
		border(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public int getMaximumCharacters() {
		return maximumCharacters;
	}

	public TextOptions focusable(boolean focusable) {
		this.focusable = focusable;
		return getThis();
	}

	public TextOptions editable(boolean editable) {
		this.editable = editable;
		return getThis();
	}

	public TextOptions enabled(boolean enabled) {
		this.enabled = enabled;
		return getThis();
	}

	public TextOptions rowsAndColumns(int numberOfRows, int numberOfColumns) {
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		return getThis();
	}

	public TextOptions maximumCharacters(int maximumCharacters) {
		this.maximumCharacters = maximumCharacters;
		return getThis();
	}

	public TextOptions digitsOnly(boolean digitsOnly) {
		this.digitsOnly = digitsOnly;
		return getThis();
	}

	public boolean isDigitsOnly() {
		return digitsOnly;
	}

	public float getFontSize() {
		return fontSize;
	}

	public TextOptions fontSize(float fontSize) {
		this.fontSize = fontSize;
		return getThis();
	}

	public boolean isFocusable() {
		return focusable;
	}

}
