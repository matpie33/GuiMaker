package com.guimaker.inputSelection;

import javax.swing.text.JTextComponent;

public class EditableInputSelectionHandler implements InputSelectionHandler {
	@Override
	public void markInputAsSelected(JTextComponent input) {
		if (!input.hasFocus()) {
			input.requestFocusInWindow();
		}
	}

	@Override
	public void markInputAsDeselected(JTextComponent input) {
		return;
	}
}
