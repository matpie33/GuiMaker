package com.guimaker.panels;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.InputSelectionManager;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTextInputsManager {

	private InputSelectionManager inputSelectionManager;
	private PanelDisplayMode panelDisplayMode;

	public PanelTextInputsManager(PanelDisplayMode displayMode) {
		inputSelectionManager = new InputSelectionManager(displayMode);
		this.panelDisplayMode = displayMode;
	}

	public boolean manageTextInput(JComponent compo,
			JTextComponent firstTextComponentInRow) {

		if (compo instanceof JTextComponent) {
			JTextComponent input = (JTextComponent) compo;
			inputSelectionManager.addInput(input, firstTextComponentInRow);
			if (panelDisplayMode.equals(PanelDisplayMode.EDIT)) {
				compo.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						inputSelectionManager.selectInput(input);
					}

					@Override
					public void focusLost(FocusEvent e) {
						inputSelectionManager.deselectInput(input);
					}
				});
			}
			else {
				compo.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						inputSelectionManager.toggleSelection(input);
					}
				});
			}

			return true;
		}
		return false;

	}

	public void addSwitchBetweenInputsFailedListener(
			SwitchBetweenInputsFailListener listener) {
		inputSelectionManager.addSwitchBetweenInputsFailListener(listener);
	}

	public JTextComponent getSelectedInput() {
		return inputSelectionManager.getSelectedInput();
	}

	public boolean hasSelectedInput() {
		return inputSelectionManager.hasSelectedInput();
	}

	public int getSelectedInputIndex() {
		return inputSelectionManager.getSelectedInputIndex();
	}

	public void selectInputInColumn(int columnNumber) {
		inputSelectionManager.selectInputInColumn(columnNumber);
	}

	public void addManager(
			ListInputsSelectionManager listInputsSelectionManager) {
		inputSelectionManager.addManager(listInputsSelectionManager);
	}

	public void clearSelectedInput() {
		inputSelectionManager.deselectCurrentInput();
	}

	public void selectNextInputInSameRow() {
		inputSelectionManager.selectNextInputInSameRow();
	}

	public void selectPreviousInputInSameRow() {
		inputSelectionManager.selectPreviousInputInSameRow();
	}


}
