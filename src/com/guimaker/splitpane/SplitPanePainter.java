package com.guimaker.splitpane;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.SplitPanePanelLocation;
import com.guimaker.model.SplitPanePanelData;
import com.guimaker.model.SplitPaneWeightsX;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SplitPanePainter {

	private static final Color BORDER_COLOR = Color.WHITE;
	private Map<Integer, Double> panelColumnNumberToWeightX = new LinkedHashMap<>();
	private SplitPaneActionsCreator splitPaneActionsCreator = new SplitPaneActionsCreator();
	private MainPanel rootSplitPanePanel;
	private PanelConstraintsCreator panelConstraintsCreator;

	public SplitPanePainter() {
		panelConstraintsCreator = new PanelConstraintsCreator();
		initializeRootPanel();
	}

	private void initializeRootPanel() {
		rootSplitPanePanel = new MainPanel(null);
		rootSplitPanePanel.setGapsBetweenColumns(0);
	}

	private MainPanel createColumnPanel(SplitPanePanelLocation location) {
		MainPanel columnPanel = new MainPanel(null);
		columnPanel.setGapsBetweenRowsTo0();
		columnPanel.setBorder(getBorderForLocation(location));
		return columnPanel;
	}

	private Border getBorderForLocation(SplitPanePanelLocation location) {
		switch (location) {
		case LEFT:
			return createBorderWithoutRightEdge();
		case CENTER:
			return createBorderWithAllEdges();
		case RIGHT:
			return createBorderWithoutLeftEdge();

		default:
			throw new IllegalArgumentException(
					"Split pane panel's location must be one of: "
							+ SplitPanePanelLocation.values() + " but was: "
							+ location);
		}
	}

	private Border createBorderWithoutLeftEdge() {
		return BorderFactory.createMatteBorder(1, 0, 1, 1, BORDER_COLOR);
	}

	private Border createBorderWithoutRightEdge() {
		return BorderFactory.createMatteBorder(1, 1, 1, 0, BORDER_COLOR);
	}

	private Border createBorderWithAllEdges() {
		return BorderFactory.createLineBorder(BORDER_COLOR, 2);
	}

	public void setWeightsX(double leftPanel, double centerPanel,
			double rightPanel) {
		SplitPaneWeightsX recalculatedWeights = panelConstraintsCreator
				.recalculateMissingWeightsX(leftPanel, centerPanel, rightPanel);
		panelColumnNumberToWeightX.put(0, recalculatedWeights.getLeftPanel());
		panelColumnNumberToWeightX.put(1, recalculatedWeights.getCenterPanel());
		panelColumnNumberToWeightX.put(2, recalculatedWeights.getRightPanel());
	}

	private List<MainPanel> createColumnPanels(
			Map<SplitPanePanelLocation, List<SplitPanePanelData>> splitPanePanelLocations) {
		List<MainPanel> wrappingPanels = new ArrayList<>();
		for (Map.Entry<SplitPanePanelLocation, List<SplitPanePanelData>> panels : splitPanePanelLocations
				.entrySet()) {
			MainPanel wrappingPanel = createColumnPanel(panels.getKey());
			MainPanel panel = null;
			for (SplitPanePanelData splitPanePanelData : panels.getValue()) {
				panel = createColumnPanel(panels.getKey());
				JLabel title = createTitleLabel(splitPanePanelData.getTitle());
				panel.addRow(SimpleRowBuilder
						.createRow(FillType.HORIZONTAL, title,
								createButtonCollapsePanel())
						.setColor(Color.GRAY)
						.fillHorizontallySomeElements(title)
						.setBorder(createBorderBottom()));
				panel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
						splitPanePanelData.getContent()));
				wrappingPanel.addRow(SimpleRowBuilder
						.createRow(FillType.BOTH, panel.getPanel())
						.setWeightY(splitPanePanelData.getWeightY()));
			}
			if (panel == null) {
				continue;
			}

			if (panels.getValue().size() == 1) {
				wrappingPanels.add(panel);
			}
			else {
				wrappingPanels.add(wrappingPanel);
			}

		}
		return wrappingPanels;
	}

	private Border createBorderBottom() {
		return BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR);
	}

	private Border createPanelsBorders() {
		return createBorderWithAllEdges();
	}

	private JLabel createTitleLabel(String title) {
		return GuiElementsCreator
				.createLabel(new ComponentOptions().text(title));
	}

	private AbstractButton createButtonCollapsePanel() {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text("-"),
				splitPaneActionsCreator.createActionCollapsePanel());
	}

	public void paint(
			Map<SplitPanePanelLocation, List<SplitPanePanelData>> panels) {
		List<MainPanel> columnPanels = createColumnPanels(panels);
		addColumnPanels(columnPanels);

	}

	private void addColumnPanels(List<MainPanel> columnPanels) {

		rootSplitPanePanel.addElementsInColumn(SimpleRowBuilder
				.createRow(FillType.BOTH,
						columnPanels.stream().map(MainPanel::getPanel)
								.collect(Collectors.toList())
								.toArray(new JComponent[] {}))
				.setColumnToPutRowInto(0).setWeightsX(
						panelColumnNumberToWeightX.values()
								.toArray(new Double[] {})));
	}

	public JPanel getPanel() {
		return rootSplitPanePanel.getPanel();
	}
}
