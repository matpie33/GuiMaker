package com.guimaker.list;

import javax.swing.text.JTextComponent;

public class ListPropertyInformation<Word extends ListElement> {

	private JTextComponent filteringTextComponent;
	private ListElementPropertyManager<?, Word> filteringHandler;

	public ListPropertyInformation(JTextComponent filteringTextComponent,
			ListElementPropertyManager<?, Word> filteringHandler) {
		this.filteringTextComponent = filteringTextComponent;
		this.filteringHandler = filteringHandler;
	}

	public JTextComponent getFilteringTextComponent() {
		return filteringTextComponent;
	}

	public ListElementPropertyManager<?, Word> getFilteringHandler() {
		return filteringHandler;
	}
}
