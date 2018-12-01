package com.guimaker.list.myList;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.WordDuplicationType;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.strings.ExceptionsMessages;
import com.guimaker.utilities.StringUtilities;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;

public class ListPropertyChangeHandler<Property, PropertyHolder extends ListElement>
		implements FocusListener {

	private MyList<PropertyHolder> list;
	private DialogWindow dialogWindow;
	private String previousValueOfTextInput;
	private ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager;
	private PropertyHolder propertyHolder;
	private String defaultValue = "";
	private InputGoal inputGoal;
	private Set<InputValidationListener<PropertyHolder>> validationListeners = new HashSet<>();

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			InputGoal inputGoal) {
		this.list = list;
		this.dialogWindow = dialogWindow;
		this.listElementPropertyManager = listElementPropertyManager;
		this.propertyHolder = propertyHolder;
		this.inputGoal = inputGoal;
		if (!inputGoal.equals(InputGoal.NO_INPUT)) {
			addInsertWordPanelAsValidationListener(list);
		}
	}

	private void addInsertWordPanelAsValidationListener(
			MyList<PropertyHolder> list) {
		InsertWordPanel insertWordPanel;
		MyList rootList = list.getRootList();
		if (rootList != null) {
			insertWordPanel = rootList.getInsertWordPanel();
		}
		else {
			insertWordPanel = list.getInsertWordPanel();
		}
		addValidationListener(insertWordPanel.getController());
	}

	public ListPropertyChangeHandler(PropertyHolder propertyHolder,
			MyList<PropertyHolder> list, DialogWindow dialogWindow,
			ListElementPropertyManager<Property, PropertyHolder> listElementPropertyManager,
			String defaultValue, boolean isRequiredField, InputGoal inputGoal) {
		this(propertyHolder, list, dialogWindow, listElementPropertyManager,
				inputGoal);
		this.defaultValue = defaultValue;
	}

	public void addValidationListener(
			InputValidationListener<PropertyHolder> validationListener) {
		if (validationListener != null) {
			validationListeners.add(validationListener);
		}

	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextComponent textInput = (JTextComponent) e.getSource();
		textInput.setForeground(Color.WHITE);
		previousValueOfTextInput = textInput.getText()
											.isEmpty() ?
				defaultValue :
				textInput.getText();

	}

	private boolean isTextFieldEmpty(JTextComponent textComponent) {
		return textComponent.getText()
							.isEmpty() || textComponent.getText()
													   .equals(defaultValue);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTextComponent input = (JTextComponent) e.getSource();
		boolean somethingHasChanged = !input.getText()
											.equals(previousValueOfTextInput);
		if (isTextFieldEmpty(input) || (!inputGoal.equals(InputGoal.ADD)
				&& !somethingHasChanged)) {
			return;
		}
		Property propertyNewValue = validateAndConvertToProperty(input);
		Property previousValue = listElementPropertyManager.convertToProperty(
				new JTextField(previousValueOfTextInput.equals(defaultValue) ?
						"" :
						previousValueOfTextInput));

		ThreadUtilities.callOnOtherThread(() -> {
			boolean inputValid = propertyNewValue != null;
			boolean addedWord = false;
			if (inputValid && !inputGoal.equals(InputGoal.SEARCH)) {
				addedWord = addWordToList(input, propertyNewValue,
						previousValue);
			}
			notifyValidationListeners(
					inputValid && (addedWord || inputGoal.equals(
							InputGoal.SEARCH)), propertyNewValue);
		});
	}

	private void notifyValidationListeners(boolean inputValid,
			Property propertyNewValue) {
		PropertyPostValidationData<Property, PropertyHolder> postValidationData = new PropertyPostValidationData<>(
				propertyHolder, propertyNewValue, listElementPropertyManager,
				inputValid);
		validationListeners.forEach(
				listener -> listener.inputValidated(postValidationData));
	}

	private boolean addWordToList(JTextComponent input,
			Property propertyNewValue, Property previousValue) {
		listElementPropertyManager.setProperty(propertyHolder, propertyNewValue,
				previousValue);
		WordInMyListExistence<PropertyHolder> childWordExistence = list.doesWordWithPropertyExist(
				propertyNewValue, listElementPropertyManager, propertyHolder);
		WordInMyListExistence rootWordExistence = null;
		if (list.getRootList() != null) {
			rootWordExistence = list.getRootList()
									.containsWord(list.getRootWord());
			//TODO make it generic safe
		}

		if (childWordExistence.exists()
				|| rootWordExistence != null && rootWordExistence.exists()) {
			WordInMyListExistence duplication = childWordExistence.exists() ?
					childWordExistence :
					rootWordExistence;
			setTextInputToPreviousValue(input);
			setWordToPreviousValue(input, propertyNewValue);
			int duplicateRowNumber = duplication.getOneBasedRowNumber();
			String exceptionMessage = getExceptionForDuplicate(propertyNewValue,
					duplicateRowNumber, duplication);
			dialogWindow.showMessageDialog(exceptionMessage, false);
			return false;
		}
		else {
			previousValueOfTextInput = null;
			list.updateObservers(propertyHolder,
					ListElementModificationType.EDIT);
			if (list.getRootList() != null) {
				list.getRootList()
					.updateObservers(list.getRootWord(),
							ListElementModificationType.EDIT);
			}
			list.save();
			return true;
		}
	}

	private void setWordToPreviousValue(JTextComponent input,
			Property previousValue) {
		listElementPropertyManager.setProperty(propertyHolder,
				listElementPropertyManager.convertToProperty(input),
				previousValue);
	}

	private void setTextInputToPreviousValue(JTextComponent input) {
		input.setText(previousValueOfTextInput);
		input.requestFocusInWindow();
		input.selectAll();
	}

	private String getExceptionForDuplicate(Property propertyNewValue,
			int duplicateRowNumber,
			WordInMyListExistence wordDuplicateInformation) {
		String propertyDefinedMessage;
		if (wordDuplicateInformation.getDuplicationType()
									.equals(WordDuplicationType.PROPERTY)) {
			propertyDefinedMessage = listElementPropertyManager.getPropertyDefinedException(
					propertyNewValue);
			propertyDefinedMessage += StringUtilities.putInNewLine(
					String.format(
							ExceptionsMessages.ROW_FOR_DUPLICATED_PROPERTY,
							duplicateRowNumber));
		}
		else {
			propertyDefinedMessage = String.format(
					ExceptionsMessages.WORD_ALREADY_EXISTS,
					wordDuplicateInformation.getOneBasedRowNumber());
		}

		return propertyDefinedMessage;
	}

	private Property validateAndConvertToProperty(JTextComponent input) {
		boolean isValid = listElementPropertyManager.validateInput(input,
				propertyHolder);
		if (!isValid && !input.getText()
							  .isEmpty()) {
			input.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			setTextInputToPreviousValue(input);
			return null;
		}
		return listElementPropertyManager.convertToProperty(input);

	}

}
