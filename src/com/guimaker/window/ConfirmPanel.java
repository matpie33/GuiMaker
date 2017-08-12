package com.guimaker.window;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.guimaker.colors.BasicColors;
import com.guimaker.keyBinding.KeyBindingsMaker;
import com.guimaker.keyBinding.SimpleActionMaker;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;

public class ConfirmPanel {

	private SimpleWindow parentDialog;
	private MainPanel panel;
	private boolean isAccepted;

	public ConfirmPanel(SimpleWindow parent) {
		panel = new MainPanel(BasicColors.DARK_GREEN);
		parentDialog = parent;
	}

	public JPanel createPanel(String message) {
		JTextArea area = GuiMaker.createTextArea(false);
		area.setText(message);
		panel.addRow(RowMaker.createBothSidesFilledRow(area));

		AbstractAction confirmingAction = SimpleActionMaker.createConfirmingAction(this, true);
		AbstractAction refuseAction = SimpleActionMaker.createConfirmingAction(this, false);
		JButton yesButton = GuiMaker.createButton("Tak", confirmingAction);

		KeyBindingsMaker.makeBindings(yesButton, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				confirmingAction);

		JButton noButton = GuiMaker.createButton("Nie", refuseAction);
		noButton.addActionListener(refuseAction);

		panel.addRow(RowMaker.createUnfilledRow(Anchor.EAST, yesButton, noButton));
		return panel.getPanel();
	}

	public boolean accepted() {
		return isAccepted;
	}

	public void setAccepted(boolean chosen) {
		isAccepted = chosen;
		parentDialog.close();
	}

}
