package com.guimaker.list.myList;

import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListRowData;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.CommonListElements;

public interface ListRowCreator<Word extends ListElement> {

	public ListRowData<Word> createListRow(Word word,
			CommonListElements commonListElements, InputGoal inputGoal);

	public void addValidationListener(
			InputValidationListener<Word> inputValidationListener);
}
