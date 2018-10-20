package com.guimaker.list;

import com.guimaker.enums.ListElementModificationType;

public interface ListObserver<Word extends ListElement> {

	public void update(Word changedListElement,
			ListElementModificationType modificationType);
}
