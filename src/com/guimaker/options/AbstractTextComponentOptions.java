package com.guimaker.options;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

public abstract class AbstractTextComponentOptions<TextOptions extends AbstractTextComponentOptions<TextOptions>>
		extends AbstractComponentOptions<TextOptions> {

	private boolean editable = true;
	private boolean enabled = true;
	private boolean wrapStyleWord = true;
	private boolean lineWrap = true;
	private boolean moveToNextComponentWhenTabbed = true;
	private boolean digitsOnly = false;
	private int numberOfRows = 0;
	private int numberOfColumns = 0;
	private int maximumCharacters = 0;

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

	public boolean isWrapStyleWord() {
		return wrapStyleWord;
	}

	public boolean isLineWrap() {
		return lineWrap;
	}

	public boolean isMoveToNextComponentWhenTabbed() {
		return moveToNextComponentWhenTabbed;
	}

	public TextOptions editable(boolean editable) {
		this.editable = editable;
		return getThis();
	}

	public TextOptions enabled(boolean enabled) {
		this.enabled = enabled;
		return getThis();
	}

	public TextOptions wrapStyleWord(boolean wrapStyleWord) {
		this.wrapStyleWord = wrapStyleWord;
		return getThis();
	}

	public TextOptions lineWrap(boolean lineWrap) {
		this.lineWrap = lineWrap;
		return getThis();
	}

	public TextOptions moveToNextComponentWhenTabbed(boolean moveToNextComponentWhenTabbed) {
		this.moveToNextComponentWhenTabbed = moveToNextComponentWhenTabbed;
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

}
