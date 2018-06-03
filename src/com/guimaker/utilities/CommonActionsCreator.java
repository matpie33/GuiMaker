package com.guimaker.utilities;

import com.guimaker.enums.ButtonType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.panels.GuiElementsCreator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CommonActionsCreator {

	public static AbstractButton createButtonDispose(String text,
			int keyEventName, final Window window) {
		return GuiElementsCreator
				.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON),
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
				.createButtonlikeComponent(new ButtonOptions(ButtonType.BUTTON),
						action, keyEventName);
	}

	public static void addHotkey(int keyEvent, int keyModifier,
			AbstractAction a, JComponent component) {

	}

	public static void addHotkey(HotkeyWrapper hotkeyWrapper, AbstractAction a,
			JComponent component) {
		int keyEvent = hotkeyWrapper.getKeyEvent();
		KeyStroke keyStroke = KeyStroke
				.getKeyStroke(keyEvent, hotkeyWrapper.getKeyModifier());
		String keyRepresentation = keyStroke.toString();
		component.getInputMap(hotkeyWrapper.getConditionForHotkey())
				.put(keyStroke, keyRepresentation);
		component.getActionMap().put(keyRepresentation, a);
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
					((JTextComponent) e.getSource()).selectAll();
					((JTextComponent) e.getSource()).setForeground(Color.WHITE);
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
		textComponent.setForeground(Color.WHITE);
	}

}
