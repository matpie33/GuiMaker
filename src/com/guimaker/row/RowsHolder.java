package com.guimaker.row;

import java.util.ArrayList;
import java.util.List;

public class RowsHolder {

	private List<AbstractSimpleRow> rows = new ArrayList<>();

	public void addRow (AbstractSimpleRow row){
		rows.add(row);
	}

	public List<AbstractSimpleRow> getAllRows() {
		return rows;
	}
}
