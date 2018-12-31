package com.guimaker.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.utilities.ColorChanger;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class CustomTabbedPane extends BasicTabbedPaneUI {

	private Polygon shape;
	private final int spaceBetweenTabs = 5;

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
			int x, int y, int w, int h, boolean isSelected) {
		return;
	}

	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement,
			Rectangle[] rects, int tabIndex, Rectangle iconRect,
			Rectangle textRect, boolean isSelected) {
		if (tabPane.hasFocus() && isSelected) {
			g.setColor(UIManager.getColor("ScrollBar.thumbShadow"));
			g.drawPolygon(shape);
		}
	}

	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement,
			int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		Graphics2D g2D = (Graphics2D) g;
		createRectangle(x, y, w, h);
		if (isSelected) {
			GradientPaint gradientShadow = new GradientPaint(0, 0,
					BasicColors.GREEN_NORMAL_1, 0, y + h / 2,
					ColorChanger.makeLighter(BasicColors.GREEN_BRIGHT_1));
			g2D.setPaint(gradientShadow);
		}
		else {
			if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
				g2D.setColor(Color.BLACK);
				GradientPaint gradientShadowTmp = new GradientPaint(0, 0,
						BasicColors.BLUE_BRIGHT_1, 0, y + h / 2,
						BasicColors.BLUE_NORMAL_2);
				g2D.setPaint(gradientShadowTmp);
			}
			else {
				GradientPaint gradientShadowTmp = new GradientPaint(0, 0,
						new Color(240, 255, 210), 0, y + 15 + h / 2,
						new Color(204, 204, 204));
				g2D.setPaint(gradientShadowTmp);
			}
		}
		g2D.fill(shape);
		if (runCount > 1) {
			g2D.setColor(
					hasAlfa(getRunForTab(tabPane.getTabCount(), tabIndex) - 1));
			g2D.fill(shape);
		}
		g2D.fill(shape);
	}

	private void createRectangle(int x, int y, int w, int h) {
		shape = new Polygon();
		shape.addPoint(x, y);
		shape.addPoint(x, y + h);
		shape.addPoint(x + w, y + h);
		shape.addPoint(x + w, y);
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BasicTabbedPaneUI.TabbedPaneLayout() {
			@Override
			protected void calculateTabRects(int tabPlacement, int tabCount) {
				final int indent = 0;

				super.calculateTabRects(tabPlacement, tabCount);

				for (int i = 0; i < rects.length; i++) {
					rects[i].x += i * spaceBetweenTabs + indent;
				}
			}
		};
	}

	private Color hasAlfa(int value) {
		int alfa = 0;
		if (value >= 0) {
			alfa = 50 + (value > 7 ? 70 : 10 * value);
		}
		return new Color(0, 0, 0, alfa);
	}

}
