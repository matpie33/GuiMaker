package com.guimaker.model;

import com.guimaker.enums.PanelDisplayMode;

import javax.swing.border.Border;
import java.awt.*;

public class PanelConfiguration {

	private PanelDisplayMode panelDisplayMode = PanelDisplayMode.EDIT;
	private boolean putRowsAsHighestAsPossible = false;
	private boolean scrollHorizontally = true;
	private boolean opaque = true;
	private Color colorToUse;
	private static final int paddingDefaultValue = 6;
	private int distanceBetweenElementsInsideRow = paddingDefaultValue;
	private int distanceBetweenRowAndPanelEdges = paddingDefaultValue;
	private int gapBetweenRows = 4;
	private Border border;

	public PanelConfiguration setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public PanelConfiguration setBorder(Border border) {
		this.border = border;
		return this;
	}

	public static int getPaddingDefaultValue() {
		return paddingDefaultValue;
	}

	public int getDistanceBetweenElementsInsideRow() {
		return distanceBetweenElementsInsideRow;
	}

	public void setDistanceBetweenElementsInsideRow(int distanceBetweenElementsInsideRow) {
		this.distanceBetweenElementsInsideRow = distanceBetweenElementsInsideRow;
	}

	public int getDistanceBetweenRowAndPanelEdges() {
		return distanceBetweenRowAndPanelEdges;
	}

	public PanelConfiguration setDistanceBetweenRowAndPanelEdges(int
			distanceBetweenRowAndPanelEdges) {
		this.distanceBetweenRowAndPanelEdges = distanceBetweenRowAndPanelEdges;
		return this;
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
