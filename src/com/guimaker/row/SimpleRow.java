package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SimpleRow {
	private JComponent[] componentsInRow;
	private JComponent[] horizontallyFilledElements = new JComponent[] {};
	private JComponent[] verticallyFilledElements = new JComponent[] {};
	private Anchor anchor;
	private Color color;
	private FillType fillType;
	private boolean isOpaque;
	private boolean borderEnabled;
	private boolean useAllExtraVerticalSpace = false;
	private SimpleRowBuilder builder;

	public Border getBorder() {
		return border;
	}

	public SimpleRow setBorder(Border border) {
		this.border = border;
		return this;
	}

	private Border border;

	SimpleRow(SimpleRowBuilder builder, FillType fillingType, Anchor anchor,
			JComponent... components) {
		this.builder = builder;
		fillType = fillingType;
		this.anchor = anchor;
		this.componentsInRow = components;
		isOpaque = true;
		borderEnabled = true;
	}

	public SimpleRow fillHorizontallyEqually() {
		return fillHorizontallySomeElements(componentsInRow);
	}

	public SimpleRow fillHorizontallySomeElements(
			JComponent... filledElements) {
		horizontallyFilledElements = filledElements;
		return this;
	}

	public SimpleRow fillVertically(JComponent... filledElements) {
		verticallyFilledElements = filledElements;
		return this;
	}

	public SimpleRow fillAllVertically() {
		verticallyFilledElements = componentsInRow;
		return this;
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

	public SimpleRow setColor(Color c) {
		this.color = c;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public SimpleRow nextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		SimpleRow s = new SimpleRow(builder, fillingType, anchor, components);
		builder.addRow(s);
		return s;
	}

	public SimpleRow nextRow(FillType fillingType, JComponent... components) {
		return nextRow(fillingType, anchor, components);
	}

	public SimpleRow nextRow(JComponent... components) {
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

	public SimpleRow setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return this;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public SimpleRow setNotOpaque() {
		isOpaque = false;
		return this;
	}

	public boolean isOpaque() {
		return isOpaque;
	}

	public SimpleRow disableBorder() {
		borderEnabled = false;
		return this;
	}

	public boolean isBorderEnabled() {
		return borderEnabled;
	}

	public SimpleRow useAllExtraVerticalSpace() {
		useAllExtraVerticalSpace = true;
		return this;
	}

	public boolean isUseAllExtraVerticalSpace() {
		return useAllExtraVerticalSpace;
	}

	public SimpleRowBuilder getBuilder() {
		return builder;
	}

}
