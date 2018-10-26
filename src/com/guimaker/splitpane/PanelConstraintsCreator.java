package com.guimaker.splitpane;

import com.guimaker.model.SplitPaneWeightsX;

public class PanelConstraintsCreator {

	//TODO recalculate weights y too if contains null

	public SplitPaneWeightsX recalculateMissingWeightsX(double leftPanel,
			double centerPanel, double rightPanel) {
		return new SplitPaneWeightsX(leftPanel, centerPanel, rightPanel);
	}

}
