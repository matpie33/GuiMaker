package com.guimaker.window;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.keyBinding.SimpleActionMaker;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;

public class MessagePanel {

	private SimpleWindow window;
	private MainPanel panel;

	public MessagePanel(SimpleWindow window) {
		panel = new MainPanel(BasicColors.OCEAN_BLUE);
		this.window = window;
	}

	public JPanel createPanel(String message) {
		JButton button = GuiMaker.createButton("Okej",
				SimpleActionMaker.createDisposingAction(window));
		JTextArea area = GuiMaker.createTextArea(false);
		area.setText(message);
		panel.addRow(RowMaker.createBothSidesFilledRow(area));
		panel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, button));
		return panel.getPanel();
	}

}
