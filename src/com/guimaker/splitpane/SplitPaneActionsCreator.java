package com.guimaker.splitpane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SplitPaneActionsCreator {

	public AbstractAction createActionCollapsePanel() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		};
	}

	public void createActionExpandCollapseOnDrag(JPanel panel) {
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int width = panel.getWidth();
				int height = panel.getHeight();
				Point mousePoint = e.getPoint();
				int mouseX = mousePoint.x;
				int mouseY = mousePoint.y;

				Insets borderInsets = panel.getInsets();
				boolean inBorder = (mouseX < borderInsets.left
						|| mouseX > width - borderInsets.right
						|| mouseY < borderInsets.top
						|| mouseY > height - borderInsets.bottom);
				if (inBorder){
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else{
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});
	}

}
