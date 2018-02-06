package com.guimaker.utilities;

import com.guimaker.enums.ComponentType;
import com.guimaker.panels.GuiMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CommonActionsMaker {

	public static AbstractButton createButtonDispose(String text, int keyEventName,
			final Window window) {
		return GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, text, createDisposeAction(window),
						keyEventName);
	}

	public static AbstractAction createDisposeAction(final Window dialog) {
		return new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
	}

	public static AbstractButton createButtonHide(String text, int keyEventName,
			final Window dialog) {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON, text, action, keyEventName);
	}

	public static void addHotkey(int keyEvent, int keyModifier, AbstractAction a,
			JComponent component) {

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyEvent, keyModifier), KeyEvent.getKeyText(keyEvent));
		component.getActionMap().put(KeyEvent.getKeyText(keyEvent), a);
	}

	public static void makeBindings(JComponent component, KeyStroke key,
			AbstractAction... actions) {
		for (AbstractAction action : actions) {
			String actionName = Long.toHexString(Double.doubleToLongBits(Math.random()));
			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, actionName);
			component.getActionMap().put(actionName, action);
		}
	}

}
