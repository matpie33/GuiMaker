package com.guimaker.options;

import com.guimaker.colors.BasicColors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractComboboxOptions<Options extends AbstractComboboxOptions<Options>>
		extends AbstractComponentOptions<Options> {

	private List<String> comboboxValues = new ArrayList<>();

	public AbstractComboboxOptions() {
		backgroundColor(BasicColors.LIGHT_BLUE_2);
		border(BorderFactory.createLineBorder(Color.WHITE));
	}

	public List<String> getComboboxValues() {
		return comboboxValues;
	}

	public Options setComboboxValues(List<String> comboboxValues) {
		this.comboboxValues = comboboxValues;
		return getThis();
	}

	public abstract Options getThis();
}
