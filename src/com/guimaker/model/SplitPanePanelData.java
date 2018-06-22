package com.guimaker.model;

import javax.swing.*;

public class SplitPanePanelData {

	private String title;
	private Double weightY;
	private JComponent content;

	public SplitPanePanelData(String title, Double weightY,
			JComponent content) {
		this.title = title;
		this.weightY = weightY;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public Double getWeightY() {
		return weightY;
	}

	public JComponent getContent() {
		return content;
	}
}
