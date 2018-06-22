package com.guimaker.splitpane;

import com.guimaker.enums.SplitPanePanelLocation;
import com.guimaker.model.SplitPanePanelData;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SplitPane {

	private Map<SplitPanePanelLocation, List<SplitPanePanelData>> contentPanelsWithInformation = new LinkedHashMap<>();
	private SplitPanePainter splitPanePainter = new SplitPanePainter();

	public SplitPane(JComponent centerComponent, String title) {
		this(centerComponent, title, null);
	}

	public SplitPane(JComponent centerComponent, String title, Double weightY) {
		initialize();
		addComponent(centerComponent, title, weightY,
				SplitPanePanelLocation.CENTER);

	}

	private void initialize (){
		contentPanelsWithInformation.put(SplitPanePanelLocation.LEFT, new ArrayList<>());
		contentPanelsWithInformation.put(SplitPanePanelLocation.CENTER, new ArrayList<>());
		contentPanelsWithInformation.put(SplitPanePanelLocation.RIGHT, new ArrayList<>());
	}

	private void validateWeight(Double weightY) {
		if (weightY != null && (weightY < 0 || weightY > 1)) {
			throw new IllegalArgumentException(
					"Weighty must be between 0 and 1, but was: " + weightY);
		}
	}

	private void addComponent(JComponent component, String title,
			Double weightY, SplitPanePanelLocation splitPanePanelLocation) {
		validateWeight(weightY);
		List<SplitPanePanelData> splitPanePanelData = contentPanelsWithInformation
				.get(splitPanePanelLocation);
		splitPanePanelData
				.add(new SplitPanePanelData(title, weightY, component));
	}

	public void addCenterComponent(JComponent centerComponent, String title) {
		addComponent(centerComponent, title, null,
				SplitPanePanelLocation.CENTER);
	}

	public void addCenterComponent(JComponent centerComponent, String title,
			Double weightY) {
		addComponent(centerComponent, title, weightY,
				SplitPanePanelLocation.CENTER);
	}

	public void addLeftComponent(JComponent leftComponent, String title) {
		addComponent(leftComponent, title, null, SplitPanePanelLocation.LEFT);
	}

	public void addLeftComponent(JComponent leftComponent, String title,
			double weightY) {
		addComponent(leftComponent, title, weightY,
				SplitPanePanelLocation.LEFT);
	}

	public void addRightComponent(JComponent rightComponent, String title) {
		addComponent(rightComponent, title, null, SplitPanePanelLocation.RIGHT);
	}

	public void addRightComponent(JComponent rightComponent, String title,
			Double weightY) {
		addComponent(rightComponent, title, weightY,
				SplitPanePanelLocation.RIGHT);
	}

	public void collapseCenterComponents() {

	}

	public void collapseLeftComponents() {

	}

	public void collapseRightComponents() {

	}

	public void setWeightsX(double leftPanel, double centerPanel,
			double rightPanel) {
		splitPanePainter.setWeightsX(leftPanel, centerPanel, rightPanel);
	}

	public void paint() {
		splitPanePainter.paint(contentPanelsWithInformation);
	}

	public JPanel getPanel() {
		return splitPanePainter.getPanel();
	}

}
