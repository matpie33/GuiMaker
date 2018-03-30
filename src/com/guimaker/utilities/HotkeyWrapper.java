package com.guimaker.utilities;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (o instanceof HotkeyWrapper == false) {
			return false;
		}
		HotkeyWrapper other = (HotkeyWrapper) o;
		return other.keyEvent == keyEvent && other.keyMask == keyMask;
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyEvent, keyMask);
	}

}
