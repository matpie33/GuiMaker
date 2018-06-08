package com.guimaker.model;

import com.guimaker.enums.SplitPanePanelLocation;

public class SplitPanePanelData {

	private String title;
	private Double weightY;
	private SplitPanePanelLocation splitpanePanelLocation;

	public SplitPanePanelData(String title, Double weightY,
			SplitPanePanelLocation splitpanePanelLocation) {
		this.title = title;
		this.weightY = weightY;
		this.splitpanePanelLocation = splitpanePanelLocation;
	}

	public String getTitle() {
		return title;
	}

	public Double getWeightY() {
		return weightY;
	}

	public SplitPanePanelLocation getSplitpanePanelLocation() {
		return splitpanePanelLocation;
	}
}
