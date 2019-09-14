package com.guimaker.model;

import com.guimaker.enums.PanelDisplayMode;

import java.awt.*;

public class PanelConfiguration {

	private PanelDisplayMode panelDisplayMode = PanelDisplayMode.EDIT;
	private boolean putRowsAsHighestAsPossible = false;
	private boolean scrollHorizontally = true;
	private boolean opaque = true;
	private Color colorToUse;
	private boolean skipInsetsForExtremeEdges = false;
	private static final int paddingDefaultValue = 4;
	private int paddingRight = paddingDefaultValue;
	private int paddingLeft = paddingDefaultValue;
	private int paddingTop = paddingDefaultValue;
	private int paddingBottom = paddingDefaultValue;
	private int gapBetweenRows = 4;

	public PanelConfiguration setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public boolean isSkipInsetsForExtremeEdges() {
		return skipInsetsForExtremeEdges;
	}

	public void setSkipInsetsForExtremeEdges(
			boolean skipInsetsForExtremeEdges) {
		this.skipInsetsForExtremeEdges = skipInsetsForExtremeEdges;
	}

	public static int getPaddingDefaultValue() {
		return paddingDefaultValue;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getGapBetweenRows() {
		return gapBetweenRows;
	}

	public void setGapBetweenRows(int gapBetweenRows) {
		this.gapBetweenRows = gapBetweenRows;
	}

	public PanelDisplayMode getPanelDisplayMode() {
		return panelDisplayMode;
	}

	public PanelConfiguration setPanelDisplayMode(
			PanelDisplayMode panelDisplayMode) {
		this.panelDisplayMode = panelDisplayMode;
		return this;
	}

	public PanelConfiguration putRowsAsHighestAsPossible() {
		putRowsAsHighestAsPossible = true;
		return this;
	}

	public boolean shouldPutRowsAsHighestAsPossible() {
		return putRowsAsHighestAsPossible;
	}

	public PanelConfiguration doNotScrollHorizontally() {
		scrollHorizontally = false;
		return this;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public PanelConfiguration setNotOpaque() {
		this.opaque = false;
		return this;
	}

	public Color getColorToUse() {
		return colorToUse;
	}

	public PanelConfiguration setColorToUse(Color colorToUse) {
		this.colorToUse = colorToUse;
		return this;
	}

	public boolean isScrollHorizontally() {
		return scrollHorizontally;
	}
}
