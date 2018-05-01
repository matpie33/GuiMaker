package com.guimaker.inputSelection;

import java.util.ArrayList;
import java.util.List;

public class ListInputsSelectionManager {

	private List<InputSelectionManager> inputsSelectionManagers = new ArrayList<>();

	public void addInputSelectionManager (InputSelectionManager inputSelectionManager){
		inputsSelectionManagers.add(inputSelectionManager);
	}

	public void clearOtherInputsManagersSelections() {
		for (InputSelectionManager inputsSelectionManager : inputsSelectionManagers) {
			inputsSelectionManager.deselectCurrentInput();
		}
	}
}
