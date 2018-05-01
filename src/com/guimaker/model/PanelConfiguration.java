package com.guimaker.model;

import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.utilities.HotkeyWrapper;

import java.util.HashMap;
import java.util.Map;

public class PanelConfiguration {

	private PanelDisplayMode panelDisplayMode;

	public PanelConfiguration(PanelDisplayMode panelDisplayMode) {
		this.panelDisplayMode = panelDisplayMode;
	}

	public PanelDisplayMode getPanelDisplayMode() {
		return panelDisplayMode;
	}

}
