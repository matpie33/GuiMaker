package com.guimaker.row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

public class Rows extends SimpleRow {

	private List<SimpleRow> rows;

	protected Rows(SimpleRow... rows) {
		super(rows[rows.length - 1].getFillTypeAsEnum(), rows[rows.length - 1].getAnchor(),
				rows[rows.length - 1].getComponents());
		this.rows = new ArrayList<>();
		this.rows.addAll(Arrays.asList(rows));
	}

	public List<SimpleRow> getRows() {
		return rows;
	}

	public Rows fillHorizontallySomeElements(JComponent... components) {
		rows.get(rows.size() - 1).fillHorizontallySomeElements(components);
		return this;
	}

	@Override
	public Rows nextRow(FillType fillType, Anchor anchor, JComponent... components) {
		rows.add(createSimpleRow(fillType, anchor, components));
		return new Rows(rows.toArray(new SimpleRow[] {}));
	}

}
