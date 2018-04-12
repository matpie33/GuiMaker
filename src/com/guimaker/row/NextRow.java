package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import java.util.List;

public class NextRow extends AbstractSimpleRow<NextRow> {
	private RowsHolder rowsHolder;

	public NextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		super(fillingType, anchor, components);
	}

	public void setRowsHolder(RowsHolder rowsHolder) {
		this.rowsHolder = rowsHolder;
	}

	@Override
	public NextRow nextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		NextRow s = new NextRow(fillingType, anchor, components);
		s.setRowsHolder(rowsHolder);
		s.setColumnToPutRowInto(getColumnToPutRowInto());
		rowsHolder.addRow(s);
		return s;
	}

	@Override
	public NextRow nextRow(FillType fillingType, JComponent... components) {
		return nextRow(fillingType, getAnchor(), components);
	}

	@Override
	public NextRow nextRow(JComponent... components) {
		return nextRow(getFillType(), Anchor.NORTHWEST, components);
	}

	@Override
	public NextRow getThis() {
		return this;
	}

	public List<AbstractSimpleRow> getAllRows() {
		return rowsHolder.getAllRows();
	}

}
