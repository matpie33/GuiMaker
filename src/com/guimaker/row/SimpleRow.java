package com.guimaker.row;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

public class SimpleRow {

	private boolean horizontallyFilled; // default is false
	private boolean verticallyFilled;
	protected JComponent[] componentsInRow;
	protected JComponent[] horizontallyFilledElements;
	protected JComponent[] verticallyFilledElements;
	protected int anchor;
	private Color color;

	public SimpleRow(boolean verticalFill, JComponent... components) {
		anchor = GridBagConstraints.NORTHWEST;
		verticallyFilledElements = new JComponent[] {};
		horizontallyFilledElements = new JComponent[] {};
		if (verticalFill)
			enableVerticalFill();
		this.componentsInRow = components;
	}

	protected void enableHorizontalFill() {
		horizontallyFilled = true;
	}

	protected void enableVerticalFill() {
		verticallyFilled = true;
	}

	protected void fillHorizontally(JComponent... filledElements) {
		horizontallyFilledElements = filledElements;
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

	public String toString() {
		String ret = "horizontal?: " + horizontallyFilled + " verticaly? :" + verticallyFilled
				+ " \n";
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

	public int getFillingType() {
		int fill = 0;
		if (horizontallyFilled && verticallyFilled) {
			fill = GridBagConstraints.BOTH;
		}
		else if (horizontallyFilled) {
			fill = GridBagConstraints.HORIZONTAL;
		}
		else if (verticallyFilled) {
			fill = GridBagConstraints.VERTICAL;
		}
		else {
			fill = GridBagConstraints.NONE;
		}
		return fill;
	}

	public SimpleRow setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public int getAnchor() {
		return anchor;
	}

}
