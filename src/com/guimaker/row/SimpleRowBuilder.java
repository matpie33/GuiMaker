package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleRowBuilder {

	private List<SimpleRow> rows;

	private SimpleRowBuilder() {
		rows = new ArrayList<>();
	}

	public List<SimpleRow> getRows() {
		return rows;
	}

	public void addRow(SimpleRow row) {
		rows.add(row);
	}

	public static SimpleRow createRow(FillType fillingType, Anchor anchor,
			JComponent... components) {
		SimpleRowBuilder builder = new SimpleRowBuilder();
		SimpleRow s = new SimpleRow(builder, fillingType, anchor, components);
		builder.addRow(s);
		return s;
	}

	public static SimpleRow createRow(FillType fillingType, JComponent... components) {
		return createRow(fillingType, Anchor.NORTHWEST, components);
	}

}
