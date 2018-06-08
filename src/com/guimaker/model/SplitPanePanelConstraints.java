package com.guimaker.model;

import com.guimaker.enums.FillType;
import com.guimaker.enums.SplitPanePanelLocation;

import javax.swing.*;

public class SplitPanePanelConstraints {

	private FillType fillType;
	private SplitPanePanelLocation location;
	private int columnNumber;
	private JComponent content;

	public SplitPanePanelConstraints(FillType fillType,
			SplitPanePanelLocation location, int columnNumber,
			JComponent content) {
		this.fillType = fillType;
		this.location = location;
		this.columnNumber = columnNumber;
		this.content = content;
	}

	public FillType getFillType() {
		return fillType;
	}

	public SplitPanePanelLocation getLocation() {
		return location;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public JComponent getContent() {
		return content;
	}
}
