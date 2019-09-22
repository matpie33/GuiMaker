package com.guimaker.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.panels.mainPanel.MainPanel;

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
		leftPanel = new MainPanel(new PanelConfiguration().setColorToUse(
				BasicColors.BLUE_NORMAL_2));
		rightPanel = new MainPanel(new PanelConfiguration().setColorToUse(
				BasicColors.BLUE_NORMAL_1));
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
