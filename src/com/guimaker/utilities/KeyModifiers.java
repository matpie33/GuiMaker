package com.guimaker.utilities;

import java.awt.event.InputEvent;

public enum KeyModifiers {
	CONTROL(InputEvent.CTRL_DOWN_MASK), SHIFT(InputEvent.SHIFT_DOWN_MASK), ALT(
			InputEvent.ALT_DOWN_MASK), NONE(0);
	private int keyMask;

	private KeyModifiers(int keyMask) {
		this.keyMask = keyMask;
	}

	public int getKeyMask() {
		return keyMask;
	}

	public static KeyModifiers of(int keyMask) {
		for (KeyModifiers keyModifiers : values()) {
			if (keyModifiers.getKeyMask() == keyMask) {
				return keyModifiers;
			}
		}
		throw new IllegalArgumentException("Key mask doesnt exist: " + keyMask);
	}

}
