package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSimpleRow<Row extends AbstractSimpleRow<Row>> {
	private JComponent[] componentsInRow;
	private JComponent[] horizontallyFilledElements = new JComponent[] {};
	private JComponent[] verticallyFilledElements = new JComponent[] {};
	private Anchor anchor;
	private Color color;
	private FillType fillType;
	private boolean isOpaque = true;
	private boolean borderEnabled;
	private boolean useAllExtraVerticalSpace = false;
	private int columnToPutRowInto;
	private Border border;
	private boolean shouldAddRow = true;
	private Double[] weightsX;
	private double weightY;
	private boolean wrapWithPanel = true;
	private List<List<JComponent>> componentsSharingColumn = new ArrayList<>();
	private List<JComponent> componentsThatDidntMatchCondition = new
			ArrayList <> ();

	public Border getBorder() {
		return border;
	}

	public Row setBorder(Border border) {
		this.border = border;
		return getThis();
	}

	public int getColumnToPutRowInto() {
		return columnToPutRowInto;
	}

	public FillType getFillType() {
		return fillType;
	}

	public AbstractSimpleRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		if (fillingType.equals(FillType.BOTH)) {
			verticallyFilledElements = components;
			horizontallyFilledElements = components;
		}
		fillType = fillingType;
		this.anchor = anchor;
		this.componentsInRow = components;
		isOpaque = true;
		borderEnabled = true;
	}

	public Row fillHorizontallyEqually() {
		return fillHorizontallySomeElements(componentsInRow);
	}

	public Row fillHorizontallySomeElements(JComponent... filledElements) {
		horizontallyFilledElements = filledElements;
		return getThis();
	}

	public Row onlyAddIf(boolean condition) {
		shouldAddRow = condition;
		return getThis();
	}

	public Row fillVertically(JComponent... filledElements) {
		verticallyFilledElements = filledElements;
		return getThis();
	}

	public Row fillAllVertically() {
		verticallyFilledElements = componentsInRow;
		return getThis();
	}

	public JComponent[] getHorizontallyFilledElements() {
		return horizontallyFilledElements;
	}

	public JComponent[] getVerticallyFilledElements() {
		return verticallyFilledElements;
	}

	public JComponent[] getComponents() {
		return componentsInRow;
	}

	public Row setColor(Color c) {
		this.color = c;
		return getThis();
	}

	public Color getColor() {
		return color;
	}

	public boolean shouldAddRow() {
		return shouldAddRow;
	}

	public ComplexRow nextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		ComplexRow s = new ComplexRow(fillingType, anchor, components);
		s.setColumnToPutRowInto(getColumnToPutRowInto());
		RowsHolder rowsHolder = new RowsHolder();
		rowsHolder.addRow(this);
		rowsHolder.addRow(s);
		s.setRowsHolder(rowsHolder);
		return s;
	}

	public ComplexRow nextRow(FillType fillingType, JComponent... components) {
		return nextRow(fillingType, anchor, components);
	}

	public ComplexRow nextRow(JComponent... components) {
		return nextRow(fillType, getAnchor(), components);
	}

	public String toString() {
		StringBuilder ret = new StringBuilder(
				"Filling type?: " + fillType.name() + " \n");
		ret.append("horizontally filled: ");
		for (JComponent c : horizontallyFilledElements) {
			ret.append(c.getClass());
		}
		ret.append("\n");
		ret.append("vertically filled: ");
		for (JComponent c : verticallyFilledElements) {
			ret.append(c.getClass());
		}
		return ret.toString();
	}

	public int getFillTypeAsGridBagConstraint() {
		return fillType.getGridBagConstraintsFilling();
	}

	public Row setWeightsX(Double... weightsX) {
		if (weightsX.length != componentsInRow.length) {
			throw new IllegalArgumentException(
					"Number of weights should be equal"
							+ "to number of components, but number of weights: "
							+ weightsX.length + " and number of components: "
							+ componentsInRow.length);
		}
		else {
			this.weightsX = weightsX;
		}
		return getThis();
	}

	public Double[] getWeightsX() {
		return weightsX;
	}

	public Row inSameColumn(JComponent... components) {
		componentsSharingColumn.add(Arrays.asList(components));
		return getThis();
	}

	public List<List<JComponent>> getComponentsSharingColumn() {
		return componentsSharingColumn;
	}

	public Row setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return getThis();
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public Row wrapSingleComponentWithPanel(boolean isWrapWithPanel) {
		this.wrapWithPanel = isWrapWithPanel;
		return getThis();
	}

	public boolean isWrapWithPanel() {
		return wrapWithPanel;
	}

	public Row setNotOpaque() {
		isOpaque = false;
		return getThis();
	}

	public boolean isOpaque() {
		return isOpaque;
	}

	public Row disableBorder() {
		borderEnabled = false;
		return getThis();
	}

	public boolean isBorderEnabled() {
		return borderEnabled;
	}

	public Row useAllExtraVerticalSpace() {
		useAllExtraVerticalSpace = true;
		return getThis();
	}

	public boolean isUseAllExtraVerticalSpace() {
		return useAllExtraVerticalSpace;
	}

	public Row setColumnToPutRowInto(int columnToPutRowInto) {
		this.columnToPutRowInto = columnToPutRowInto;
		return getThis();
	}

	public abstract Row getThis();

	public Row setWeightY(double weightY) {
		this.weightY = weightY;
		return getThis();
	}
	public Row componentOnlyIfConditionMatches(JComponent component,
			boolean conditionPassed) {
		if (!conditionPassed){
			componentsThatDidntMatchCondition.add(component);
		}
		return getThis();
	}

	public List<JComponent> getComponentsThatDidntMatchCondition() {
		return componentsThatDidntMatchCondition;
	}

	public double getWeightY() {
		return weightY;
	}
}
