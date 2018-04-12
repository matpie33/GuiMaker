package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;

public class SimpleRowBuilder {

	public static SimpleRow createRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		SimpleRow s = new SimpleRow(fillingType, anchor, components);
		return s;
	}

	public static AbstractSimpleRow createRow(FillType fillingType,
			JComponent... components) {
		return createRow(fillingType, Anchor.NORTHWEST, components);
	}

	public static AbstractSimpleRow createRowStartingFromColumn(
			int columnNumber, FillType fillingType, Anchor anchor,
			JComponent... components) {
		AbstractSimpleRow abstractSimpleRow = createRow(fillingType, anchor,
				components);
		abstractSimpleRow.setColumnToPutRowInto(columnNumber);
		return abstractSimpleRow;
	}

	public static AbstractSimpleRow createRowStartingFromColumn(
			int columnNumber, FillType fillingType, JComponent... components) {
		AbstractSimpleRow abstractSimpleRow = createRow(fillingType,
				components);
		abstractSimpleRow.setColumnToPutRowInto(columnNumber);
		return abstractSimpleRow;
	}

}
