package com.guimaker.list;

import com.guimaker.panels.mainPanel.MainPanel;

import java.util.HashMap;
import java.util.Map;

public class ListRowData<Word extends ListElement> {

	private Map<String, ListPropertyInformation<Word>> rowPropertiesData = new HashMap<>();
	private MainPanel rowPanel;

	public ListRowData(MainPanel rowPanel) {
		this.rowPanel = rowPanel;
	}

	public Map<String, ListPropertyInformation<Word>> getRowPropertiesData() {
		return rowPropertiesData;
	}

	public void addPropertyInformation(String property,
			ListPropertyInformation<Word> propertyInformation) {
		rowPropertiesData.put(property, propertyInformation);
	}

	public MainPanel getRowPanel() {
		return rowPanel;
	}

	public boolean isEmpty() {
		return rowPropertiesData.isEmpty();
	}
}
