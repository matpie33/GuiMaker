package com.guimaker.splitpane;

import com.guimaker.enums.FillType;
import com.guimaker.enums.SplitPanePanelLocation;
import com.guimaker.model.SplitPanePanelConstraints;
import com.guimaker.model.SplitPanePanelData;
import com.guimaker.model.SplitPaneWeightsX;
import com.guimaker.utilities.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelConstraintsCreator {

	//TODO recalculate weights y too if contains null

	public SplitPaneWeightsX recalculateMissingWeightsX(double leftPanel,
			double centerPanel, double rightPanel) {
		return new SplitPaneWeightsX(leftPanel, centerPanel, rightPanel);
	}


}
