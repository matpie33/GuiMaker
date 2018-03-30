package com.guimaker.utilities;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ElementCopier {

	public static AbstractButton copyButton(AbstractButton buttonToCopy) {
		ActionListener[] actionListeners = buttonToCopy.getActionListeners();
		AbstractButton copiedButton = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						buttonToCopy.getText(), null);
		for (ActionListener actionListener : actionListeners) {
			copiedButton.addActionListener(actionListener);
		}
		return copiedButton;
	}

	public static JLabel copyLabel(JLabel labelToCopy) {
		JLabel copiedLabel = GuiMaker.createLabel(new ComponentOptions());
		copiedLabel.setText(labelToCopy.getText());
		return copiedLabel;
	}

}
