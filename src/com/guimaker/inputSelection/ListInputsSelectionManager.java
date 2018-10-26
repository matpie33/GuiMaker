package com.guimaker.inputSelection;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class ListInputsSelectionManager {

	private List<InputSelectionManager> inputsSelectionManagers = new ArrayList<>();

	public void addInputSelectionManager(
			InputSelectionManager inputSelectionManager) {
		inputsSelectionManagers.add(inputSelectionManager);
	}

	public void clearOtherInputsManagersSelections() {
		for (InputSelectionManager inputsSelectionManager : inputsSelectionManagers) {
			inputsSelectionManager.deselectCurrentInput();
		}
	}

	public JTextComponent getSelectedInput() {
		for (InputSelectionManager inputsSelectionManager : inputsSelectionManagers) {
			if (inputsSelectionManager.hasSelectedInput()) {
				return inputsSelectionManager.getSelectedInput();
			}
		}
		return null;

	}
}
