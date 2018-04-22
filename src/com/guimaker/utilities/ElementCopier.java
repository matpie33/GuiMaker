package com.guimaker.utilities;

import com.guimaker.enums.ButtonType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ElementCopier {

	public static AbstractButton copyButton(AbstractButton buttonToCopy) {
		ActionListener[] actionListeners = buttonToCopy.getActionListeners();
		AbstractButton copiedButton = GuiElementsCreator
				.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON)
						.text(buttonToCopy.getText()), null);
		for (ActionListener actionListener : actionListeners) {
			copiedButton.addActionListener(actionListener);
		}
		return copiedButton;
	}

	public static JLabel copyLabel(JLabel labelToCopy) {
		JLabel copiedLabel = GuiElementsCreator
				.createLabel(new ComponentOptions());
		copiedLabel.setText(labelToCopy.getText());
		return copiedLabel;
	}

}
