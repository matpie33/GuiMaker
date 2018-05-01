package com.guimaker.listeners;

import com.guimaker.enums.MoveDirection;

import javax.swing.text.JTextComponent;

public interface SwitchBetweenInputsFailListener {

	public void switchFailed (JTextComponent input, MoveDirection direction);

}
