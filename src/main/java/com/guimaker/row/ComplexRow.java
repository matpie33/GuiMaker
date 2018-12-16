package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import java.util.List;

public class ComplexRow extends AbstractSimpleRow<ComplexRow> {
	private RowsHolder rowsHolder;

	public ComplexRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		super(fillingType, anchor, components);
	}

	public void setRowsHolder(RowsHolder rowsHolder) {
		this.rowsHolder = rowsHolder;
	}

	@Override
	public ComplexRow nextRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		ComplexRow s = new ComplexRow(fillingType, anchor, components);
		s.setRowsHolder(rowsHolder);
		s.setColumnToPutRowInto(getColumnToPutRowInto());
		rowsHolder.addRow(s);
		return s;
	}

	@Override
	public ComplexRow nextRow(FillType fillingType, JComponent... components) {
		return nextRow(fillingType, getAnchor(), components);
	}

	@Override
	public ComplexRow nextRow(JComponent... components) {
		return nextRow(getFillType(), getAnchor(), components);
	}

	@Override
	public ComplexRow getThis() {
		return this;
	}

	public List<AbstractSimpleRow> getAllRows() {
		return rowsHolder.getAllRows();
	}

	public AbstractSimpleRow getRowContainingComponent(JComponent component) {
		return rowsHolder.getRowContainingComponent(component);
	}

}
