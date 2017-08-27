package com.guimaker.keyBinding;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;

import com.guimaker.window.ConfirmPanel;
import com.guimaker.window.SimpleWindow;

public class SimpleActionMaker {

	public static AbstractAction createConfirmingAction(final ConfirmPanel panel,
			final boolean chosen) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.setAccepted(chosen);
			}
		};
	}

	public static WindowListener createClosingListener(final SimpleWindow window) {
		return new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				window.close();
			}
		};
	}

	public static AbstractAction createDisposingAction(final SimpleWindow dialog) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
	}
}
