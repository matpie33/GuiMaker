package com.guimaker.panels;

import com.guimaker.enums.Direction;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.row.AbstractSimpleRow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColumnPanelCreator {

	private JPanel wrappingPanel;
	private int numberOfColumns;
	private int numberOfRows;
	private PanelDisplayMode panelDisplayMode;
	private int gapBetweenRows;
	private int gapsBetweenColumns = 0;
	private int paddingLeft, paddingRight, paddingTop, paddingBottom;
	private ElementsShifter elementsShifter;

	public ColumnPanelCreator(PanelDisplayMode panelDisplayMode,
			int gapBetweenRows) {
		this.panelDisplayMode = panelDisplayMode;
		this.gapBetweenRows = gapBetweenRows;
		wrappingPanel = new JPanel(new GridBagLayout());
		wrappingPanel.setOpaque(false);
		elementsShifter = new ElementsShifter(wrappingPanel, false);
	}

	public void setPadding(int top, int right, int bottom, int left) {
		paddingLeft = left;
		paddingBottom = bottom;
		paddingRight = right;
		paddingTop = top;
	}

	public boolean isInitialized() {
		return wrappingPanel != null && wrappingPanel.getComponentCount() > 0;
	}

	public void initializePanel() {

	}

	public JPanel getPanel() {
		return wrappingPanel != null ? wrappingPanel : new JPanel();
	}

	public void addElementsInColumn(AbstractSimpleRow abstractSimpleRow,
			int rowNumber) {
		if (!abstractSimpleRow.shouldAddRow()) {
			return;
		}
		List<JComponent> elementsInRow = new ArrayList<>(
				Arrays.asList(abstractSimpleRow.getComponents()));
		if (numberOfColumns < elementsInRow.size()) {
			numberOfColumns = elementsInRow.size();
		}
		List<List<JComponent>> groupsOfComponentsThatShareColumn = abstractSimpleRow.getComponentsSharingColumn();
		for (List<JComponent> componentsSharingColumn : groupsOfComponentsThatShareColumn) {
			JPanel panel = new JPanel();
			panel.setOpaque(false);
			boolean first = true;
			for (JComponent component : componentsSharingColumn) {
				panel.add(component);
				if (first) {
					elementsInRow.set(elementsInRow.indexOf(component), panel);
				}
				else {
					elementsInRow.remove(component);
				}
				first = false;
			}
		}
		int indexOfElement = 0;
		List<JComponent> verticallyFilledElements = Arrays.asList(
				abstractSimpleRow.getVerticallyFilledElements());
		List<JComponent> horizontallyFilledElements = Arrays.asList(
				abstractSimpleRow.getHorizontallyFilledElements());
		int startingColumn = abstractSimpleRow.getColumnToPutRowInto();
		for (JComponent element : elementsInRow) {

			if (element == null) {
				startingColumn++;
				continue;
			}
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = startingColumn++;
			c.gridy = rowNumber;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(paddingTop, paddingLeft, paddingBottom,
					paddingRight);
			c.weighty = 0;
			c.weightx = 0;
			if (panelDisplayMode.equals(PanelDisplayMode.VIEW)
					&& !(element instanceof AbstractButton)) {
				element.setEnabled(false);
			}

			if (horizontallyFilledElements.contains(element)) {
				c.fill = FillType.HORIZONTAL.getGridBagConstraintsFilling();
				c.weightx = 1;

			}
			if (verticallyFilledElements.contains(element)) {
				if (horizontallyFilledElements.contains(element)) {
					c.fill = FillType.BOTH.getGridBagConstraintsFilling();
				}
				else {
					c.fill = FillType.VERTICAL.getGridBagConstraintsFilling();
				}

				c.weighty = 1;
			}
			if (indexOfElement == elementsInRow.size() - 1
					&& indexOfElement == numberOfColumns - 1) {
				c.weightx = 1;
			}
			if (abstractSimpleRow.getWeightsX() != null) {
				Double weightx = abstractSimpleRow.getWeightsX()[indexOfElement];
				if (weightx != 0) {
					c.weightx = weightx;
				}
			}

			wrappingPanel.add(element, c);
			indexOfElement++;
		}
		numberOfRows++;
	}

	public void addElementsInColumn(AbstractSimpleRow abstractSimpleRow) {
		addElementsInColumn(abstractSimpleRow, numberOfRows);
	}

	public void clear() {
		numberOfRows = 0;
		numberOfColumns = 0;
		wrappingPanel.removeAll();
	}

	public void setGapsBetweenColumns(int gapsBetweenColumns) {
		this.gapsBetweenColumns = gapsBetweenColumns;
	}

	public void setGapBetweenRows(int gapBetweenRows) {
		this.gapBetweenRows = gapBetweenRows;
	}

	public int getIndexOfRowContainingElements(Component... elements) {
		return getConstraintsForComponent(elements[0]).gridy;
	}

	public GridBagConstraints getConstraintsForComponent(Component component) {
		GridBagLayout layout = (GridBagLayout) getPanel().getLayout();
		return layout.getConstraints(component);
	}

	public void shiftElements(Direction direction, int startIndex,
			int absoluteIncrementDecrementValue) {
		elementsShifter.shiftElements(direction, startIndex,
				absoluteIncrementDecrementValue);
	}
}
