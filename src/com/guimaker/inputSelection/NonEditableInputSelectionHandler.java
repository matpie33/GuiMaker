package com.guimaker.inputSelection;

import com.guimaker.colors.BasicColors;

import javax.swing.text.JTextComponent;
import java.awt.*;

public class NonEditableInputSelectionHandler implements InputSelectionHandler {

	public static final Color NOT_SELECTED_COLOR = Color.GRAY;
	public static final Color SELECTED_COLOR = BasicColors.PURPLE_NORMAL_1;

	@Override
	public void markInputAsSelected(JTextComponent input) {
		input.setBackground(SELECTED_COLOR);
		input.setOpaque(true);

	}

	@Override
	public void markInputAsDeselected(JTextComponent input) {
		input.setBackground(NOT_SELECTED_COLOR);
		input.setOpaque(false);
	}
}
