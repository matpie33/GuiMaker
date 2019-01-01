package com.guimaker.application;

import com.guimaker.customPositioning.CustomPositioner;
import com.guimaker.model.ListColors;

import java.awt.*;

public class ApplicationConfiguration {

	private CustomPositioner insertWordPanelPositioner;
	private ListColors listColors;
	private String title;
	private Color panelBackgroundColor;
	private Color contentPanelColor;
	private Color hotkeysPanelColor;

	public ApplicationConfiguration(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public CustomPositioner getInsertWordPanelPositioner() {
		return insertWordPanelPositioner;
	}

	public ApplicationConfiguration setInsertWordPanelPositioner(
			CustomPositioner insertWordPanelPositioner) {
		this.insertWordPanelPositioner = insertWordPanelPositioner;
		return this;
	}


	public Color getPanelBackgroundColor() {
		return panelBackgroundColor;
	}

	public ApplicationConfiguration setPanelBackgroundColor(
			Color panelBackgroundColor) {
		this.panelBackgroundColor = panelBackgroundColor;
		return this;
	}

	public ListColors getListColors() {
		return listColors;
	}

	public ApplicationConfiguration setListColors(ListColors listColors) {
		this.listColors = listColors;
		return this;
	}

	public Color getContentPanelColor() {
		return contentPanelColor;
	}

	public ApplicationConfiguration setContentPanelColor(
			Color contentPanelColor) {
		this.contentPanelColor = contentPanelColor;
		return this;
	}

	public Color getHotkeysPanelColor() {
		return hotkeysPanelColor;
	}

	public ApplicationConfiguration setHotkeysPanelColor(Color hotkeysPanelColor) {
		this.hotkeysPanelColor = hotkeysPanelColor;
		return this;
	}
}
