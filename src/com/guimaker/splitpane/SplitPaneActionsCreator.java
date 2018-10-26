package com.guimaker.splitpane;

import com.guimaker.enums.MouseInBorderPosition;
import com.guimaker.panels.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SplitPaneActionsCreator {

	private MouseInBorderPosition mouseInBorderPosition = MouseInBorderPosition.NOT_IN_BORDER;
	private boolean mouseClicked;
	private int currentMouseXPosition;
	private int currentMouseYPosition;

	public AbstractAction createActionCollapsePanel() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		};
	}

	public void addShrinkExpandListeners(MainPanel wrappingPanel,
			JPanel subPanel) {
		createActionShrinkExpandOnDrag(wrappingPanel, subPanel);
		createActionExpandShrink(subPanel);
	}

	private void createActionShrinkExpandOnDrag(MainPanel wrappingPanel,
			JPanel subPanel) {
		subPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentMouseXPosition = e.getXOnScreen();
				currentMouseYPosition = e.getYOnScreen();
				if (mouseInBorderPosition.equals(
						MouseInBorderPosition.IN_VERTICAL)) {
					GridBagConstraints constraints = wrappingPanel.getConstraintsForComponent(
							subPanel);
					double oldWeightX = constraints.weightx;
					double newWeight = calculateNewWeightX(subPanel,
							oldWeightX);
					if (newWeight <= 0) {
						return;
					}
					for (Component otherSubPanel : wrappingPanel.getPanel()
																.getComponents()) {
						if (subPanel.getWidth() + subPanel.getLocation().x
								== otherSubPanel.getLocation().x) {
							GridBagConstraints thisConstraints = wrappingPanel.getConstraintsForComponent(
									otherSubPanel);
							thisConstraints.weightx =
									thisConstraints.weightx - (newWeight
											- oldWeightX);
							wrappingPanel.setConstraintsForComponent(
									otherSubPanel, thisConstraints);
						}
					}
					constraints.weightx = newWeight;
					wrappingPanel.setConstraintsForComponent(subPanel,
							constraints);
					wrappingPanel.updateView();
				}
				if (mouseInBorderPosition.equals(
						MouseInBorderPosition.IN_HORIZONTAL)) {
					GridBagConstraints constraints = wrappingPanel.getConstraintsForComponent(
							subPanel);
					double oldWeightY = constraints.weighty;
					double newWeight = calculateNewWeightY(subPanel,
							constraints.weighty);
					for (Component otherSubPanel : wrappingPanel.getRows()) {
						if (subPanel.getHeight() + subPanel.getLocation().y
								== otherSubPanel.getLocation().y) {
							GridBagConstraints thisConstraints = wrappingPanel.getConstraintsForComponent(
									otherSubPanel);
							thisConstraints.weighty =
									thisConstraints.weighty - (newWeight
											- oldWeightY);
							wrappingPanel.setConstraintsForComponent(
									otherSubPanel, thisConstraints);

						}
					}
					constraints.weighty = newWeight;
					wrappingPanel.setConstraintsForComponent(subPanel,
							constraints);
					wrappingPanel.updateView();
				}

			}
		});
	}

	private double calculateNewWeightY(JPanel panel, double oldWeightY) {
		double mouseMovementDeltaY =
				(double) currentMouseYPosition - panel.getLocationOnScreen()
													  .getY();
		double panelHeight = panel.getSize()
								  .getHeight();
		double panelHeightChange = mouseMovementDeltaY - panelHeight;
		return (panelHeight + panelHeightChange) * oldWeightY / panelHeight;
	}

	private double calculateNewWeightX(JPanel panel, double oldWeightX) {
		int panelWidth = panel.getSize().width;
		int mouseMovementDeltaX =
				currentMouseXPosition - (panel.getLocationOnScreen().x
						+ panelWidth);
		return (double) (panelWidth + mouseMovementDeltaX) * oldWeightX
				/ (double) panelWidth;
	}

	private void createActionExpandShrink(JPanel panel) {
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mouseClicked = true;
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseClicked = false;
			}
		});
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int width = panel.getWidth();
				int height = panel.getHeight();
				Point mousePoint = e.getPoint();
				int mouseX = mousePoint.x;
				int mouseY = mousePoint.y;

				Insets borderInsets = panel.getInsets();

				if (mouseY < borderInsets.top
						|| mouseY > height - borderInsets.bottom) {
					mouseInBorderPosition = MouseInBorderPosition.IN_HORIZONTAL;
				}
				else if (mouseX < borderInsets.left
						|| mouseX > width - borderInsets.right) {
					mouseInBorderPosition = MouseInBorderPosition.IN_VERTICAL;
				}
				else {
					mouseInBorderPosition = MouseInBorderPosition.NOT_IN_BORDER;
				}
				if (mouseInBorderPosition.equals(
						MouseInBorderPosition.IN_HORIZONTAL)) {
					panel.setCursor(
							Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				}
				else if (mouseInBorderPosition.equals(
						MouseInBorderPosition.IN_VERTICAL)) {
					panel.setCursor(
							Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				}
				else {
					if (!mouseClicked) {
						mouseInBorderPosition = MouseInBorderPosition.NOT_IN_BORDER;
						panel.setCursor(Cursor.getPredefinedCursor(
								Cursor.DEFAULT_CURSOR));
					}

				}
			}
		});
	}

}
