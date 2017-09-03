package com.guimaker.row;

import java.awt.Color;

import javax.swing.JComponent;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

public class SimpleRow {

	protected JComponent[] componentsInRow;
	protected JComponent[] horizontallyFilledElements;
	protected JComponent[] verticallyFilledElements;
	protected Anchor anchor;
	private Color color;
	private FillType fillType;

	public SimpleRow(FillType fillingType, Anchor anchor, JComponent... components) {
		fillType = fillingType;
		this.anchor = anchor;
		verticallyFilledElements = new JComponent[] {};
		horizontallyFilledElements = new JComponent[] {};
		this.componentsInRow = components;
	}

	public SimpleRow(FillType fillingType, JComponent... components) {
		this(fillingType, Anchor.SOUTHWEST, components);
	}

	public SimpleRow fillHorizontallyEqually() {
		return fillHorizontallySomeElements(componentsInRow);
	}

	public SimpleRow fillHorizontallySomeElements(JComponent... filledElements) {
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

	public Rows nextRow(JComponent... components) {
		return nextRow(fillType, anchor, components);
	}

	public Rows nextRow(FillType fillType, Anchor anchor, JComponent... components) {
		if (components == null) {
			return new Rows(this);
		}
		return new Rows(this, createSimpleRow(fillType, anchor, components));
	}

	protected SimpleRow createSimpleRow(FillType fillType, Anchor anchor,
			JComponent... components) {
		SimpleRow simpleRow = copyRow().setComponents(components);
		simpleRow.setFillType(fillType);
		simpleRow.setAnchor(anchor);
		return simpleRow;
	}

	public Rows nextRow(FillType fillType, JComponent... components) {
		return nextRow(fillType, anchor, components);
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

	public String toString() {
		String ret = "Filling type?: " + fillType.name() + " \n";
		ret += "horizontally filled: ";
		for (JComponent c : horizontallyFilledElements) {
			ret += c.getClass();
		}
		ret += "\n";
		ret += "vertically filled: ";
		for (JComponent c : verticallyFilledElements) {
			ret += c.getClass();
		}
		return ret;
	}

	public int getFillTypeAsGridBagConstraint() {
		return fillType.getGridBagConstraintsFilling();
	}

	public FillType getFillTypeAsEnum() {
		return fillType;
	}

	public SimpleRow setAnchor(Anchor anchor) {
		this.anchor = anchor;
		return this;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public SimpleRow copyRow() {
		SimpleRow simpleRow = new SimpleRow(fillType, new JComponent[] {});
		return simpleRow;
	}

	public SimpleRow setComponents(JComponent... components) {
		componentsInRow = components;
		return this;
	}

	public void setFillType(FillType fillType) {
		this.fillType = fillType;
	}

}
