package com.guimaker.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.row.SimpleRow;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ExpandablePanel extends MainPanel {
	//TODO hold the rows builded with "add row in column" too
	private List<SimpleRow> rows = new ArrayList<>();
	private State state;
	private JLabel titleLabel;

	private static final String EXPAND_HINT = "(rozwiń...)";
	private static final String SHRINK_HINT = "(zwiń...)";
	private static final int TITLE_ROW_NUMBER = 1;

	private enum State {
		EXPANDED, SHRINKED
	}

	public ExpandablePanel(Color color, String title) {
		super(color);
		state = State.SHRINKED;
		initialize(title);

	}

	private void initialize(String title) {
		titleLabel = GuiMaker
				.createLabel(new ComponentOptions().text(title +" "+ EXPAND_HINT));
		addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.WEST, titleLabel));
		getPanel().addMouseListener(new MouseAdapter() {
			@Override public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (state.equals(State.SHRINKED)) {
					expand();
				}
				else {
					shrink();
				}
			}

			@Override public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				getPanel().setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});

	}

	public void createRow(SimpleRow row) {
		rows.add(row);
	}

	public void shrink() {
		state = State.SHRINKED;
		String titleLabelText = titleLabel.getText();
		titleLabelText = titleLabelText.replace(SHRINK_HINT, EXPAND_HINT);
		titleLabel.setText(titleLabelText);
		for (int i = rows.size() - 1; i >= 0; i--) {
			removeRow((i + TITLE_ROW_NUMBER));
		}
		updateView();

	}

	public void expand() {
		String titleLabelText = titleLabel.getText();
		titleLabelText = titleLabelText.replace(EXPAND_HINT, SHRINK_HINT);
		titleLabel.setText(titleLabelText);
		state = State.EXPANDED;
		for (SimpleRow simpleRow : rows) {
			super.addRow(simpleRow);
		}
		updateView();
	}

}