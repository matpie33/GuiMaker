package com.guimaker.listeners;

import com.guimaker.enums.MoveDirection;
import com.guimaker.list.myList.MyList;

import javax.swing.text.JTextComponent;

public interface SwitchBetweenInputsFailListener {

	public void switchBetweenInputsFailed(JTextComponent input,
			MoveDirection direction);

}
