package com.guimaker.inputSelection;

import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.TextInputsList;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputSelectionManager {

	private Map<JTextComponent, TextInputsList> inputToRowMap = new HashMap<>();
	private JTextComponent currentlySelectedComponent;
	private InputSelectionHandler inputSelectionHandler;
	private List<SwitchBetweenInputsFailListener> switchBetweenInputsFailListeners = new ArrayList<>();
	private int numberOfRows = 0;
	private ListInputsSelectionManager manager;

	public InputSelectionManager(PanelDisplayMode panelDisplayMode) {
		getSelectionHandlerForDisplayMode(panelDisplayMode);
	}

	private void getSelectionHandlerForDisplayMode(
			PanelDisplayMode displayMode) {
		switch (displayMode) {
		case EDIT:
			inputSelectionHandler = new EditableInputSelectionHandler();
			break;
		case VIEW:
			inputSelectionHandler = new NonEditableInputSelectionHandler();
			break;
		}
	}

	public void addSwitchBetweenInputsFailListener(
			SwitchBetweenInputsFailListener listener) {
		switchBetweenInputsFailListeners.add(listener);
	}

	public void addInput(JTextComponent inputToAdd,
			JTextComponent inputThatAlreadyExists) {
		TextInputsList listOfInputsToWhichWeAdd = inputToRowMap
				.get(inputThatAlreadyExists);
		if (listOfInputsToWhichWeAdd == null) {
			listOfInputsToWhichWeAdd = new TextInputsList(numberOfRows);
			numberOfRows++;
		}
		listOfInputsToWhichWeAdd.addInput(inputToAdd);
		inputToRowMap.put(inputToAdd, listOfInputsToWhichWeAdd);
	}

	public void selectNextInputInSameRow() {
		if (currentlySelectedComponent == null) {
			selectInput(inputToRowMap.values().iterator().next().getInputsList()
					.get(0));
			return;
		}
		TextInputsList rowContainingCurrentlySelectedInput = inputToRowMap
				.get(currentlySelectedComponent);
		int indexOfCurrentlySelectedInput = rowContainingCurrentlySelectedInput
				.getInputsList().indexOf(currentlySelectedComponent);
		if (rowContainingCurrentlySelectedInput.getInputsList().size()
				> indexOfCurrentlySelectedInput + 1) {
			selectInput(rowContainingCurrentlySelectedInput.getInputsList()
					.get(indexOfCurrentlySelectedInput + 1));
		}
		else {
			notifyThatNextDoesntExist(MoveDirection.RIGHT);
		}

	}

	private void notifyThatNextDoesntExist(MoveDirection moveDirection) {
		switchBetweenInputsFailListeners.forEach(listener -> listener
				.switchBetweenInputsFailed(currentlySelectedComponent,
						moveDirection));

	}

	public void selectPreviousInputInSameRow() {
		if (currentlySelectedComponent == null) {
			List<JTextComponent> inputsList = inputToRowMap.values().iterator()
					.next().getInputsList();
			selectInput(inputsList.get(inputsList.size()-1));
			return;
		}
		TextInputsList rowContainingCurrentlySelectedInput = inputToRowMap
				.get(currentlySelectedComponent);
		int indexOfCurrentlySelectedInput = rowContainingCurrentlySelectedInput
				.getInputsList().indexOf(currentlySelectedComponent);
		if (indexOfCurrentlySelectedInput > 0) {
			selectInput(rowContainingCurrentlySelectedInput.getInputsList()
					.get(indexOfCurrentlySelectedInput - 1));
		}
		else{
			notifyThatNextDoesntExist(MoveDirection.LEFT);
		}
	}

	private TextInputsList getRowByNumber(int rowNumber) {
		for (TextInputsList rowOfInputs : inputToRowMap.values()) {
			if (rowOfInputs.getRowNumber() == rowNumber) {
				return rowOfInputs;
			}
		}
		return null;
	}

	public void selectInput(JTextComponent input) {
		if (manager != null) {
			manager.clearOtherInputsManagersSelections();
		}
		if (currentlySelectedComponent != null) {
			inputSelectionHandler
					.markInputAsDeselected(currentlySelectedComponent);
		}
		inputSelectionHandler.markInputAsSelected(input);
		currentlySelectedComponent = input;

	}

	public void deselectInput(JTextComponent input) {
		if (currentlySelectedComponent != null) {
			inputSelectionHandler.markInputAsDeselected(input);
		}
		currentlySelectedComponent = null;
	}

	public void toggleSelection(JTextComponent input) {
		if (currentlySelectedComponent == input) {
			deselectInput(input);
		}
		else {
			selectInput(input);
		}
	}

	public JTextComponent getSelectedInput() {
		return currentlySelectedComponent;
	}

	public boolean hasSelectedInput() {
		return currentlySelectedComponent != null;
	}

	public int getSelectedInputIndex() {
		return inputToRowMap.get(currentlySelectedComponent).getInputsList()
				.indexOf(currentlySelectedComponent);
	}

	public void selectInputInColumn(int columnNumber) {
		List<JTextComponent> inputsList = inputToRowMap.entrySet().iterator()
				.next().getValue().getInputsList();
		if (inputsList.size() > columnNumber) {
			selectInput(inputsList.get(columnNumber));
		}

	}

	public void addManager(
			ListInputsSelectionManager listInputsSelectionManager) {
		this.manager = listInputsSelectionManager;
		manager.addInputSelectionManager(this);
	}

	public void deselectCurrentInput() {
		deselectInput(currentlySelectedComponent);
	}
}
