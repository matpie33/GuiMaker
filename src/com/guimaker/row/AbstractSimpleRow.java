package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class AbstractSimpleRow<Row extends AbstractSimpleRow<Row>> {
	private JComponent[] componentsInRow;
	private JComponent[] horizontallyFilledElements = new JComponent[] {};
	private JComponent[] verticallyFilledElements = new JComponent[] {};
	private Anchor anchor;
	private Color color;
	private FillType fillType;
	private boolean isOpaque;
	private boolean borderEnabled;
	private boolean useAllExtraVerticalSpace = false;
	private int columnToPutRowInto;
	private Border border;

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

	public AbstractSimpleRow( FillType fillingType,
			Anchor anchor, JComponent... components) {
		fillType = fillingType;
		this.anchor = anchor;
		this.componentsInRow = components;
		isOpaque = true;
		borderEnabled = true;
	}

	public Row fillHorizontallyEqually() {
		return fillHorizontallySomeElements(componentsInRow);
	}

	public Row fillHorizontallySomeElements(
			JComponent... filledElements) {
		horizontallyFilledElements = filledElements;
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

	public ComplexRow nextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		ComplexRow s = new ComplexRow( fillingType, anchor, components);
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
		return nextRow(fillType, Anchor.NORTHWEST, components);
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

	public Row setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return getThis();
	}

	public Anchor getAnchor() {
		return anchor;
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

}
