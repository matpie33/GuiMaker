package com.guimaker.splitpane;

import com.guimaker.enums.MouseInBorderPosition;

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

	public void addShrinkExpandListeners(JPanel panel) {
		createActionChangeCursorOnBorders(panel);
		createActionShrinkExpandOnDrag(panel);
		createActionDetectMouseClickAndTrackMousePosition(panel);
	}

	private void createActionChangeCursorOnBorders(JPanel panel) {
		panel.addMouseMotionListener(new MouseAdapter() {
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
				boolean inBorder = !mouseInBorderPosition
						.equals(MouseInBorderPosition.NOT_IN_BORDER);
				if (inBorder) {
					panel.setCursor(
							Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else {
					if (!mouseClicked) {
						mouseInBorderPosition = MouseInBorderPosition.NOT_IN_BORDER;
					}

					panel.setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});

	}

	private void createActionShrinkExpandOnDrag(JPanel panel) {
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int modifierX = e.getX() - currentMouseXPosition;
				int modifierY = e.getY() - currentMouseYPosition;
				currentMouseYPosition = e.getY();
				currentMouseXPosition = e.getX();
				if (mouseInBorderPosition
						.equals(MouseInBorderPosition.IN_VERTICAL)) {
					panel.setSize(new Dimension(panel.getWidth() + modifierX,
							panel.getHeight()));
				}
				if (mouseInBorderPosition
						.equals(MouseInBorderPosition.IN_HORIZONTAL)) {
					panel.setSize(new Dimension(panel.getWidth(),
							panel.getHeight() + modifierY));
				}
			}
		});
	}

	private void createActionDetectMouseClickAndTrackMousePosition(JPanel panel) {
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
				currentMouseXPosition = e.getPoint().x;
				currentMouseYPosition = e.getPoint().y;
			}
		});
	}

}
