package com.guimaker.model;

public class SplitPaneWeightsX {

	private double leftPanel;
	private double centerPanel;
	private double rightPanel;

	public SplitPaneWeightsX(double leftPanel, double centerPanel,
			double rightPanel) {
		this.leftPanel = leftPanel;
		this.centerPanel = centerPanel;
		this.rightPanel = rightPanel;
	}

	public double getLeftPanel() {
		return leftPanel;
	}

	public double getCenterPanel() {
		return centerPanel;
	}

	public double getRightPanel() {
		return rightPanel;
	}
}
