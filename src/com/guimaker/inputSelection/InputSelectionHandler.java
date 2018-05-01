package com.guimaker.inputSelection;

import javax.swing.text.JTextComponent;

public interface InputSelectionHandler {
	public void markInputAsSelected(JTextComponent input);

	public void markInputAsDeselected(JTextComponent input);
}
