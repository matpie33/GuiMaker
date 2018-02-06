package com.guimaker.panels;

import com.guimaker.colors.BasicColors;

import javax.swing.*;
import java.awt.*;

public class SubPanel {

	private MainPanel leftPanel;
	private MainPanel rightPanel;
	private JPanel panel;

	public SubPanel() {
		super();
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		leftPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		rightPanel = new MainPanel(BasicColors.LIGHT_BLUE);
		panel.add(leftPanel.getPanel());
		panel.add(rightPanel.getPanel());
	}

	public MainPanel getLeft() {
		return leftPanel;
	}

	public MainPanel getRight() {
		return rightPanel;
	}

	public JPanel getPanel() {
		return panel;
	}

}
