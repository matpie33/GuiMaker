package com.guimaker.splitpane;

import com.guimaker.colors.BasicColors;
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

	private static final Color BORDER_COLOR = BasicColors.BLUE_BRIGHT_3;
	private static final Color BOTTOM_BORDER_COLOR = BasicColors.BLUE_NORMAL_1;
	private Map<Integer, Double> panelColumnNumberToWeightX = new LinkedHashMap<>();
	private SplitPaneActionsCreator splitPaneActionsCreator = new SplitPaneActionsCreator();
	private MainPanel rootSplitPanePanel;
	private PanelConstraintsCreator panelConstraintsCreator;
	private final int borderThickness = 5;

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
		columnPanel.setWrappingPanelBorder(getBorderForLocation(location));
		return columnPanel;
	}

	private Border getBorderForLocation(SplitPanePanelLocation location) {
		switch (location) {
		case LEFT:
			return createBorderWithoutRightEdge();
		case CENTER:
			return createBorderWithoutLeftAndRightEdge();
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
		return BorderFactory
				.createMatteBorder(borderThickness, 0, borderThickness,
						borderThickness, BORDER_COLOR);
	}

	private Border createBorderWithoutRightEdge() {
		return BorderFactory.createMatteBorder(borderThickness, borderThickness,
				borderThickness, 0, BORDER_COLOR);
	}

	private Border createBorderWithoutLeftAndRightEdge() {
		return BorderFactory.createMatteBorder(borderThickness, borderThickness,
				borderThickness, borderThickness, BORDER_COLOR);
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
			boolean manyPanels = panels.getValue().size() > 1;
			int indexOfPanel = 0;
			for (SplitPanePanelData splitPanePanelData : panels.getValue()) {
				panel = new MainPanel(null);
				panel.setGapsBetweenRowsTo0();
				if (indexOfPanel < panels.getValue().size() - 1) {
					panel.setWrappingPanelBorder(createBorderBottom());
				}
				else if (panels.getValue().size() == 1) {
					panel.setWrappingPanelBorder(
							getBorderForLocation(panels.getKey()));
				}

				JLabel title = createTitleLabel(splitPanePanelData.getTitle());
				panel.addRow(SimpleRowBuilder
						.createRow(FillType.HORIZONTAL, title,
								createButtonCollapsePanel())
						.setColor(Color.GRAY)
						.fillHorizontallySomeElements(title)
						.setBorder(createBorderBottom()));
				panel.addRow(SimpleRowBuilder.createRow(FillType.BOTH,
						splitPanePanelData.getContent()));
				if (manyPanels) {
					wrappingPanel.addRow(SimpleRowBuilder
							.createRow(FillType.BOTH, panel.getPanel())
							.setWeightY(splitPanePanelData.getWeightY()));
				}

				indexOfPanel++;
			}
			if (panel == null) {
				continue;
			}

			wrappingPanels.add(manyPanels ? wrappingPanel : panel);

		}
		return wrappingPanels;
	}

	private Border createBorderBottom() {
		return BorderFactory.createMatteBorder(0, 0, 2, 0, BOTTOM_BORDER_COLOR);
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
