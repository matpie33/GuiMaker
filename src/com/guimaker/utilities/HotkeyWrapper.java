package com.guimaker.utilities;

public class HotkeyWrapper {
	private int keyEvent, keyMask;

	public HotkeyWrapper(KeyModifiers keyModifier, int keyEvent) {
		this.keyEvent = keyEvent;
		keyMask = keyModifier.getKeyMask();
	}

	public HotkeyWrapper(int keyEvent) {
		this.keyEvent = keyEvent;
	}

	public int getKeyMask() {
		return keyMask;
	}

	public int getKeyEvent() {
		return keyEvent;
	}

	public boolean hasKeyModifier() {
		return keyMask != KeyModifiers.NONE.getKeyMask();
	}

}
