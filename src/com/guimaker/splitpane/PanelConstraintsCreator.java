package com.guimaker.splitpane;

import com.guimaker.enums.FillType;
import com.guimaker.enums.SplitPanePanelLocation;
import com.guimaker.model.SplitPanePanelConstraints;
import com.guimaker.model.SplitPanePanelData;
import com.guimaker.model.SplitPaneWeightsX;
import com.guimaker.panels.MainPanel;
import com.guimaker.utilities.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelConstraintsCreator {

	public List<SplitPanePanelConstraints> getConstraints(
			Map<JComponent, SplitPanePanelData> components) {

		List<SplitPanePanelConstraints> panelsConstraints = new ArrayList<>();

		for (Map.Entry<JComponent, SplitPanePanelData> panel : components
				.entrySet()) {
			SplitPanePanelConstraints splitPanePanelConstraints;
			FillType fillType;
			int columnNumber;
			if (panel.getValue().getWeightY() == null) {
				fillType = FillType.HORIZONTAL;
			}
			else {
				fillType = FillType.BOTH;
			}
			SplitPanePanelLocation panelLocation = panel.getValue()
					.getSplitpanePanelLocation();
			switch (panelLocation) {
			case LEFT:
				columnNumber = 0;
				break;
			case CENTER:
				columnNumber = 1;
				break;
			case RIGHT:
				columnNumber = 2;
				break;
			default:
				throw new IllegalArgumentException();
			}
			splitPanePanelConstraints = new SplitPanePanelConstraints(fillType,
					panelLocation, columnNumber, panel.getKey());
			panelsConstraints.add(splitPanePanelConstraints);

		}
		return panelsConstraints;
	}

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
