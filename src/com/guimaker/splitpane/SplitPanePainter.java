package com.guimaker.splitpane;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.SplitPanePanelLocation;
import com.guimaker.model.SplitPanePanelConstraints;
import com.guimaker.model.SplitPanePanelData;
import com.guimaker.model.SplitPaneWeightsX;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SplitPanePainter {

	private static final Color BORDER_COLOR = Color.WHITE;
	private Map<Integer, Double> panelColumnNumberToWeightX = new HashMap<>();
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

	private Map<MainPanel, Integer> createColumnPanels(
			List<SplitPanePanelConstraints> splitPanePanelConstraintsList) {
		Map<MainPanel, Integer> columnPanelsWithColumnNumber = new LinkedHashMap<>();
		for (SplitPanePanelConstraints panelConstraints : splitPanePanelConstraintsList) {
			//sort panels: first left, then center, then right
			MainPanel panel = createColumnPanel(panelConstraints.getLocation());
			JLabel title = createTitleLabel("title");
			panel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL, title,
					createButtonCollapsePanel()).setColor(Color.GRAY)
					.fillHorizontallySomeElements(title)
					.setBorder(createBorderBottom()));
			panel.addRow(SimpleRowBuilder
					.createRow(panelConstraints.getFillType(),
							panelConstraints.getContent()));
			columnPanelsWithColumnNumber
					.put(panel, panelConstraints.getColumnNumber());
		}
		return columnPanelsWithColumnNumber;
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

	public void paint(Map<JComponent, SplitPanePanelData> panels) {
		List<SplitPanePanelConstraints> panelsConstraints = panelConstraintsCreator
				.getConstraints(panels);
		Map<MainPanel, Integer> columnPanels = createColumnPanels(
				panelsConstraints);
		addColumnPanels(columnPanels);

	}

	private void addColumnPanels(Map<MainPanel, Integer> columnPanels) {
		JPanel[] panels = columnPanels.keySet().stream()
				.map(mainPanel -> mainPanel.getPanel())
				.collect(Collectors.toList()).toArray(new JPanel[] {});
		AbstractSimpleRow row = SimpleRowBuilder
				.createRow(FillType.VERTICAL, panels).fillAllVertically();

		rootSplitPanePanel.addElementsInColumn(row.setColumnToPutRowInto(0));
	}

	public JPanel getPanel() {
		return rootSplitPanePanel.getPanel();
	}
}
