package com.guimaker.list.myList;

import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.ListPropertyInformation;
import com.guimaker.list.ListRowData;
import com.guimaker.panels.mainPanel.MainPanel;

import javax.swing.text.JTextComponent;

public class ListRowDataCreator<Word extends ListElement> {

	private ListRowData<Word> listRowData;

	public ListRowDataCreator(MainPanel rowPanel) {
		this.listRowData = new ListRowData<>(rowPanel);
	}

	public void addPropertyData(String propertyName,
			JTextComponent filteringInput,
			ListElementPropertyManager<?, Word> filteringHandler) {

		ListPropertyInformation<Word> listPropertyInformation = new ListPropertyInformation<>(
				filteringInput, filteringHandler);
		listRowData.addPropertyInformation(propertyName,
				listPropertyInformation);

	}

	public ListRowData<Word> getListRowData() {
		return listRowData;
	}

}
