package com.guimaker.keyBinding;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyBindingsMaker {

	public static void makeBindings(JComponent component, KeyStroke key,
			AbstractAction... actions) {
		for (AbstractAction action : actions) {
			String actionName = Long.toHexString(Double.doubleToLongBits(Math.random()));
			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, actionName);
			component.getActionMap().put(actionName, action);
		}
	}

}
