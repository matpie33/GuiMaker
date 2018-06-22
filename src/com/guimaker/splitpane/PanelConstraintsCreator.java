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
		double sumOfWeights = MathUtils.sum(leftPanel, centerPanel, rightPanel);

		if (sumOfWeights > 1) {
			throw new IllegalArgumentException(
					"Sum of weights for column panels should not exceed 1, but was: "
							+ sumOfWeights);
		}
		else if (sumOfWeights < 1) {
			int numberOfZeroElements = MathUtils
					.numberOfZeroElements(leftPanel, centerPanel, rightPanel);
			double rest = 1 - sumOfWeights;
			rest = rest / numberOfZeroElements;
			leftPanel = replaceWeightIfIsZero(leftPanel, rest);
			rightPanel = replaceWeightIfIsZero(rightPanel, rest);
			centerPanel = replaceWeightIfIsZero(centerPanel, rest);
		}
		return new SplitPaneWeightsX(leftPanel, centerPanel, rightPanel);
	}

	private double replaceWeightIfIsZero(double valueToCheck, double newValue) {
		if (valueToCheck == 0) {
			valueToCheck = newValue;
		}
		return valueToCheck;
	}

}
