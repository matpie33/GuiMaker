package com.guimaker.model;

import com.guimaker.enums.PanelDisplayMode;

import java.awt.*;

public class PanelConfiguration {

	private PanelDisplayMode panelDisplayMode = PanelDisplayMode.EDIT;
	private boolean putRowsAsHighestAsPossible = false;
	private boolean scrollHorizontally = true;
	private boolean opaque = true;
	private Color colorToUse;


	public PanelDisplayMode getPanelDisplayMode() {
		return panelDisplayMode;
	}

	public PanelConfiguration setPanelDisplayMode (PanelDisplayMode
			panelDisplayMode){
		this.panelDisplayMode = panelDisplayMode;
		return this;
	}

	public PanelConfiguration putRowsAsHighestAsPossible(){
		putRowsAsHighestAsPossible = true;
		return this;
	}

	public boolean shouldPutRowsAsHighestAsPossible (){
		return putRowsAsHighestAsPossible;
	}

	public PanelConfiguration doNotScrollHorizontally (){
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
