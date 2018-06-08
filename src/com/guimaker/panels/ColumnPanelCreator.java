package com.guimaker.panels;

import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.row.AbstractSimpleRow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ColumnPanelCreator {

	private JPanel wrappingPanel;
	private int numberOfColumns;
	private int numberOfRows;
	private PanelDisplayMode panelDisplayMode;
	private int gapBetweenRows;
	private int gapsBetweenColumns;

	public ColumnPanelCreator(PanelDisplayMode panelDisplayMode,
			int gapBetweenRows) {
		this.panelDisplayMode = panelDisplayMode;
		this.gapBetweenRows = gapBetweenRows;
	}

	public boolean isInitialized() {
		return wrappingPanel != null;
	}

	public void initializePanel() {
		wrappingPanel = new JPanel(new GridBagLayout());
		wrappingPanel.setOpaque(false);
	}

	public JPanel getPanel() {
		return wrappingPanel;
	}

	public void addElementsInColumn(AbstractSimpleRow abstractSimpleRow) {
		if (!abstractSimpleRow.shouldAddRow()) {
			return;
		}
		JComponent[] elements = abstractSimpleRow.getComponents();
		if (numberOfColumns < elements.length) {
			numberOfColumns = elements.length;
		}
		int indexOfElement = 0;
		List<JComponent> verticallyFilledElements = Arrays
				.asList(abstractSimpleRow.getVerticallyFilledElements());
		List<JComponent> horizontallyFilledElements = Arrays
				.asList(abstractSimpleRow.getHorizontallyFilledElements());
		int startingColumn = abstractSimpleRow.getColumnToPutRowInto();
		for (JComponent element : elements) {

			if (element == null) {
				startingColumn++;
				continue;
			}
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = startingColumn++;
			c.gridy = numberOfRows;
			c.anchor = abstractSimpleRow.getAnchor().getAnchor();
			int xGap = gapsBetweenColumns;
			int yGap = gapBetweenRows;
			c.insets = new Insets(yGap, xGap, yGap, xGap);
			c.weighty = 1;
			if (panelDisplayMode.equals(PanelDisplayMode.VIEW)) {
				element.setEnabled(false);
			}

			if (horizontallyFilledElements.contains(element)) {
				c.fill = FillType.HORIZONTAL.getGridBagConstraintsFilling();
				c.weightx = 1;

			}
			if (verticallyFilledElements.contains(element)) {
				c.weighty = 1;
			}
			if (indexOfElement == elements.length - 1
					&& indexOfElement == numberOfColumns - 1) {
				c.weightx = 1;
			}

			wrappingPanel.add(element, c);
			indexOfElement++;
		}
		numberOfRows++;
	}

	public void clear() {
		if (wrappingPanel != null) {
			wrappingPanel.removeAll();
			wrappingPanel = null;
		}

	}

	public void setGapsBetweenColumns(int gapsBetweenColumns) {
		this.gapsBetweenColumns = gapsBetweenColumns;
	}
}
