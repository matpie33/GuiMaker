package com.guimaker.row;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RowsHolder {

	private List<AbstractSimpleRow> rows = new ArrayList<>();

	public void addRow (AbstractSimpleRow row){
		rows.add(row);
	}

	public List<AbstractSimpleRow> getAllRows() {
		return rows;
	}

	public AbstractSimpleRow getRowContainingComponent (JComponent component){
		for (AbstractSimpleRow row : rows) {
			if (Arrays.asList(row.getComponents()).contains(component)){
				return row;
			}
		}
		return null;
	}

}
