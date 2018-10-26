package com.guimaker.list.myList;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.WordInMyListExistence;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.strings.ExceptionsMessages;
import com.guimaker.utilities.StringUtilities;
import com.guimaker.utilities.ThreadUtilities;

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
		if (isTextFieldEmpty(input) && !somethingHasChanged) {
			return;
		}
		Property propertyNewValue = validateAndConvertToProperty(input);

		ThreadUtilities.callOnOtherThread(() -> {
			boolean inputValid = propertyNewValue != null;
			boolean addedWord = false;
			if (inputValid && !inputGoal.equals(InputGoal.SEARCH)) {
				addedWord = addWordToList(input, propertyNewValue);
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
			Property propertyNewValue) {
		listElementPropertyManager.setProperty(propertyHolder,
				propertyNewValue);
		WordInMyListExistence<PropertyHolder> wordInMyListExistence = list.doesWordWithPropertyExist(
				propertyNewValue, listElementPropertyManager, propertyHolder);
		if (wordInMyListExistence.exists()) {
			setTextInputToPreviousValue(input);
			setWordToPreviousValue(input);
			int duplicateRowNumber = wordInMyListExistence.getOneBasedRowNumber();
			String exceptionMessage = getExceptionForDuplicate(propertyNewValue,
					duplicateRowNumber);
			dialogWindow.showMessageDialog(exceptionMessage, false);
			list.highlightRow(duplicateRowNumber - 1, true);

			//TODO performance of displaying just 200 words is terrible low, which
			//forces me to show message dialog before highlighting row (which loads words
			// if necessary)
			//TODO also think of a way how to display modal dialog while painting the
			//loaded words

			return false;
		}
		else {
			previousValueOfTextInput = null;
			list.updateObservers(propertyHolder,
					ListElementModificationType.EDIT);
			list.save();
			return true;
		}
	}

	private void setWordToPreviousValue(JTextComponent input) {
		listElementPropertyManager.setProperty(propertyHolder,
				listElementPropertyManager.validateInputAndConvertToProperty(
						input));
	}

	private void setTextInputToPreviousValue(JTextComponent input) {
		input.setText(previousValueOfTextInput);
		input.requestFocusInWindow();
		input.selectAll();
	}

	private String getExceptionForDuplicate(Property propertyNewValue,
			int duplicateRowNumber) {
		String propertyDefinedMessage = listElementPropertyManager.getPropertyDefinedException(
				propertyNewValue);
		String duplicatedRowMessage = StringUtilities.putInNewLine(
				String.format(ExceptionsMessages.ROW_FOR_DUPLICATED_PROPERTY,
						duplicateRowNumber));
		return propertyDefinedMessage + duplicatedRowMessage;
	}

	private Property validateAndConvertToProperty(JTextComponent input) {
		Property propertyNewValue = listElementPropertyManager.validateInputAndConvertToProperty(
				input);
		if (propertyNewValue == null && !input.getText()
											  .isEmpty()) {
			input.setForeground(Color.RED);
			dialogWindow.showMessageDialog(
					listElementPropertyManager.getInvalidPropertyReason());
			setTextInputToPreviousValue(input);
			return null;
		}
		return propertyNewValue;

	}

}
