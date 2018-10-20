package com.guimaker.listeners;

import com.guimaker.list.ListElement;
import com.guimaker.model.PropertyPostValidationData;

public interface InputValidationListener<Word extends ListElement> {

	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData);

}
