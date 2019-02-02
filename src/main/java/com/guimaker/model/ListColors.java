package com.guimaker.model;

import com.guimaker.colors.BasicColors;

import java.awt.*;

public class ListColors {

	private Color rowColor = BasicColors.PURPLE_DARK_1;
	private Color selectedRowColor = BasicColors.BLUE_NORMAL_7;
	private Color editRowColor = BasicColors.BLUE_NORMAL_4;
	private Color filterPanelColor = BasicColors.GREEN_BRIGHT_1;
	private Color backgroundColor = BasicColors.PURPLE_NORMAL_2;

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ListColors backgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getRowColor() {
		return rowColor;
	}

	public ListColors rowColor(Color backgroundColor) {
		this.rowColor = backgroundColor;
		return this;
	}

	public Color getSelectedRowColor() {
		return selectedRowColor;
	}

	public ListColors selectedRowColor(Color selectedRowColor) {
		this.selectedRowColor = selectedRowColor;
		return this;
	}

	public Color getEditRowColor() {
		return editRowColor;
	}

	public ListColors editRowColor(Color editRowColor) {
		this.editRowColor = editRowColor;
		return this;
	}

	public Color getFilterPanelColor() {
		return filterPanelColor;
	}

	public ListColors filterPanelColor(Color filterPanelColor) {
		this.filterPanelColor = filterPanelColor;
		return this;
	}
}
