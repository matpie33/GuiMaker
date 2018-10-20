package com.guimaker.list;


public interface ObservableList<Word extends ListElement> {

	public void addListObserver(ListObserver<Word> observer);
}
