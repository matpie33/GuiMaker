package com.guimaker.utilities;

import com.guimaker.enums.ComponentType;
import com.guimaker.panels.GuiElementsCreator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

public class CommonActionsCreator {

	public static AbstractButton createButtonDispose(String text,
			int keyEventName, final Window window) {
		return GuiElementsCreator
				.createButtonlikeComponent(ComponentType.BUTTON, text,
				createDisposeAction(window), keyEventName);
	}

	public static AbstractAction createDisposeAction(final Window dialog) {
		return new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
	}

	public static AbstractButton createButtonHide(String text, int keyEventName,
			final Window dialog) {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		return GuiElementsCreator
				.createButtonlikeComponent(ComponentType.BUTTON, text, action,
						keyEventName);
	}

	public static void addHotkey(int keyEvent, int keyModifier,
			AbstractAction a, JComponent component) {

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyEvent, keyModifier),
						KeyEvent.getKeyText(keyEvent));
		component.getActionMap().put(KeyEvent.getKeyText(keyEvent), a);
	}

	public static void makeBindings(JComponent component, KeyStroke key,
			AbstractAction... actions) {
		for (AbstractAction action : actions) {
			String actionName = Long
					.toHexString(Double.doubleToLongBits(Math.random()));
			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(key, actionName);
			component.getActionMap().put(actionName, action);
		}
	}

	public static FocusListener addPromptWhenEmpty(String promptWhenEmpty) {
		return new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (((JTextComponent) e.getSource()).getText()
						.equals(promptWhenEmpty)) {
					((JTextComponent) e.getSource()).setText("");
					((JTextComponent) e.getSource()).setForeground(Color.BLACK);
				}
				super.focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				JTextComponent textComponent = (JTextComponent) e.getSource();
				if (textComponent.getText().isEmpty()) {
					setTextFieldToPromptValue(textComponent, promptWhenEmpty);
				}
				super.focusLost(e);
			}
		};
	}

	public static void setTextFieldToPromptValue(JTextComponent textComponent,
			String prompt) {
		textComponent.setText(prompt);
		textComponent.setForeground(Color.GRAY);
	}


}
