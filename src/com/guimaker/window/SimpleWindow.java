package com.guimaker.window;

import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.guimaker.keyBinding.KeyBindingsMaker;
import com.guimaker.keyBinding.SimpleActionMaker;

public class SimpleWindow {
	private JDialog window;
	protected SimpleWindow newDialog;
	protected boolean isOpened;

	public SimpleWindow() {
		window = new JDialog();
		isOpened = false;
	}

	public void setProperties(JPanel contentPane) {
		window.setContentPane(contentPane);
		window.pack();
		window.setMinimumSize(window.getSize());
		window.setTitle("Błąd");
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);
		window.addWindowListener(SimpleActionMaker.createClosingListener(this));
		isOpened = true;
	}

	public void setMenuBar(JMenuBar menu) {
		window.setJMenuBar(menu);
	}

	protected boolean notOpenedYet() {
		return (newDialog == null || !newDialog.isOpened());
	}

	public void showMessageDialog(String message, boolean modal) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			MessagePanel dialog = new MessagePanel(newDialog);
			newDialog.setProperties(dialog.createPanel(message));
		}
	}

	public boolean showConfirmDialog(String prompt) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			newDialog.getWindow().setModalityType(ModalityType.APPLICATION_MODAL);
			newDialog.setEscapeOnClose();
			ConfirmPanel panel = new ConfirmPanel(newDialog);
			newDialog.setProperties(panel.createPanel(prompt));

			return panel.accepted();
		}
		return false;
	}

	public JDialog getWindow() {
		return window;
	}

	private void setRootToDisposeOnKey(KeyStroke key) {
		KeyBindingsMaker.makeBindings(window.getRootPane(), key,
				SimpleActionMaker.createDisposingAction(this));
	}

	public void setEscapeOnClose() {
		setRootToDisposeOnKey((KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)));
	}

	public boolean isOpened() {
		return isOpened;
	}

	public void close() {
		isOpened = false;
		window.dispose();
	}

	public void dispose() {
		window.dispose();
	}

	public SimpleWindow getNewDialog() {
		return newDialog;
	}

}
