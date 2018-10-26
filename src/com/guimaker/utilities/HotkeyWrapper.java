package com.guimaker.utilities;

import com.guimaker.enums.ConditionForHotkey;

import java.util.Objects;

public class HotkeyWrapper {
	private int keyEvent, keyMask;
	private ConditionForHotkey conditionForHotkey;

	public HotkeyWrapper(KeyModifiers keyModifier, int keyEvent) {
		this(keyModifier, keyEvent,
				ConditionForHotkey.COMPONENT_IN_FOCUSED_WINDOW);
	}

	public HotkeyWrapper(int keyEvent) {
		this(KeyModifiers.NONE, keyEvent);
	}

	public HotkeyWrapper(KeyModifiers keyModifier, int keyEvent,
			ConditionForHotkey conditionForHotkey) {
		this.keyEvent = keyEvent;
		this.keyMask = keyModifier.getKeyMask();
		this.conditionForHotkey = conditionForHotkey;
	}

	public int getConditionForHotkey() {
		return conditionForHotkey.getIntValue();
	}

	public int getKeyModifier() {
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
