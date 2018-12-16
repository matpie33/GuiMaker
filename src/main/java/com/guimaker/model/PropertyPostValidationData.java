package com.guimaker.model;

import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;

public class PropertyPostValidationData<WordProperty, Word extends ListElement> {
	private Word validatedWord;
	private boolean isValid;

	public PropertyPostValidationData(Word validatedWord, boolean isValid) {
		this.validatedWord = validatedWord;
		this.isValid = isValid;
	}

	public Word getValidatedWord() {
		return validatedWord;
	}


	public boolean isValid() {
		return isValid;
	}
}
