package com.guimaker.row;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;

import javax.swing.*;

public class SimpleRow extends AbstractSimpleRow<SimpleRow> {

	public SimpleRow(FillType fillingType,
			Anchor anchor, JComponent... components) {
		super( fillingType, anchor, components);
	}

	@Override
	public SimpleRow getThis() {
		return this;
	}
}
