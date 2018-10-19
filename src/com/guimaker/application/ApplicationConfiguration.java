package com.guimaker.application;

import com.guimaker.customPositioning.CustomPositioner;

import java.awt.*;

public class ApplicationConfiguration {

	private CustomPositioner insertWordPanelPositioner;
	private Color listRowHighlightColor;
	private Color listRowEditTemporarilyColor;
	private String title;
	private Color panelBackgroundColor;
	private Color contentPanelColor;

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
		this.insertWordPanelPositioner =
				insertWordPanelPositioner;
		return this;
	}

	public Color getListRowHighlightColor() {
		return listRowHighlightColor;
	}

	public ApplicationConfiguration setListRowHighlightColor(Color listRowHighlightColor) {
		this.listRowHighlightColor = listRowHighlightColor;
		return this;
	}

	public Color getListRowEditTemporarilyColor() {
		return listRowEditTemporarilyColor;
	}

	public ApplicationConfiguration setListRowEditTemporarilyColor(
			Color listRowEditTemporarilyColor) {
		this.listRowEditTemporarilyColor = listRowEditTemporarilyColor;
		return this;
	}

	public Color getPanelBackgroundColor() {
		return panelBackgroundColor;
	}

	public ApplicationConfiguration setPanelBackgroundColor(Color panelBackgroundColor) {
		this.panelBackgroundColor = panelBackgroundColor;
		return this;
	}

	public Color getContentPanelColor() {
		return contentPanelColor;
	}

	public ApplicationConfiguration setContentPanelColor(Color contentPanelColor) {
		this.contentPanelColor = contentPanelColor;
		return this;
	}
}
