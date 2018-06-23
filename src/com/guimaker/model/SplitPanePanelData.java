package com.guimaker.model;

import javax.swing.*;

public class SplitPanePanelData {

	private String title;
	private double weightY;
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

	public double getWeightY() {
		return weightY;
	}

	public JComponent getContent() {
		return content;
	}
}
