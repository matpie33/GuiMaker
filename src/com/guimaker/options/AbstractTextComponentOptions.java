package com.guimaker.options;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public abstract class AbstractTextComponentOptions<TextOptions extends AbstractTextComponentOptions<TextOptions>>
		extends AbstractComponentOptions<TextOptions> {

	private boolean editable = true;
	private boolean focusable = true;
	private boolean digitsOnly = false;
	private int numberOfRows = 0;
	private int numberOfColumns = 0;
	private int maximumCharacters = 0;
	private String promptWhenEmpty = "";
	private boolean selectable = false;

	AbstractTextComponentOptions() {
		border(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public String getPromptWhenEmpty(){
		return promptWhenEmpty;
	}

	public boolean isEditable() {
		return editable;
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

	public TextOptions promptWhenEmpty(String promptWhenEmpty){
		this.promptWhenEmpty = promptWhenEmpty;
		return getThis();
	}

	public TextOptions focusable(boolean focusable) {
		this.focusable = focusable;
		return getThis();
	}

	public TextOptions editable(boolean editable) {
		this.editable = editable;
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

	public boolean isFocusable() {
		return focusable;
	}

	public TextOptions selectable(boolean selectable) {
		this.selectable = selectable;
		return getThis();
	}

	public boolean isSelectable() {
		return selectable;
	}
}
