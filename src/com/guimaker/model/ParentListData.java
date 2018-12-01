package com.guimaker.model;

import com.guimaker.enums.ListElementModificationType;
import com.guimaker.list.ListElement;
import com.guimaker.list.myList.MyList;

import java.util.Collection;

public class ParentListData<ParentWordType extends ListElement, ChildWordType extends ListElement> {

	private MyList<ParentWordType> parentList;
	private ParentWordType parentWord;
	private Collection<ChildWordType> childListRepresentationAsCollection;

	public ParentListData(MyList<ParentWordType> parentList,
			ParentWordType parentWord,
			Collection<ChildWordType> childListRepresentationAsCollection) {
		this.parentList = parentList;
		this.parentWord = parentWord;
		this.childListRepresentationAsCollection = childListRepresentationAsCollection;
	}

	public void updateObservers(ListElementModificationType modificationType) {
		parentList.updateObservers(parentWord, modificationType);
	}

	public void addElement(ChildWordType word) {
		if (!childListRepresentationAsCollection.contains(word)){
			childListRepresentationAsCollection.add(word);
		}
	}

	public void removeElement(ChildWordType word) {
		childListRepresentationAsCollection.remove(word);
	}

	public MyList<ParentWordType> getList() {
		return parentList;
	}

	public ParentWordType getParentWord() {
		return parentWord;
	}
}
