package com.guimaker.enums;

import javax.swing.*;

public enum ConditionForHotkey {
	COMPONENT_FOCUSED(JComponent.WHEN_FOCUSED), COMPONENT_IN_FOCUSED_WINDOW(
			JComponent.WHEN_IN_FOCUSED_WINDOW);

	private int intValue;

	private ConditionForHotkey(int intValue) {
		this.intValue = intValue;
	}

	public int getIntValue() {
		return intValue;
	}
}
