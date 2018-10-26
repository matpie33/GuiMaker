package com.guimaker.model;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class TextInputsList {

	private int rowNumber;
	private List<JTextComponent> inputsList = new ArrayList<>();

	public TextInputsList(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public void addInput(JTextComponent component) {
		inputsList.add(component);
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public List<JTextComponent> getInputsList() {
		return inputsList;
	}
}
