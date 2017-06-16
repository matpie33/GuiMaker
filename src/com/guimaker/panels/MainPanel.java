package com.guimaker.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.guimaker.row.SimpleRow;

public class MainPanel {

	private List<JPanel> rows;
	private JPanel panel;
	private int gapBetweenRows = 5;
	private int gapInsideRow = 3;
	private final boolean shouldPutRowsHighestAsPossible;

	public void setGapsBetweenRowsTo0() {
		gapBetweenRows = 0;
	}

	public void setGapsBetweenRowsTo(int gap) {
		gapBetweenRows = gap;
	}

	public MainPanel(Color color) {
		this(color, false);
	}

	public MainPanel(Color color, boolean putRowsHighestAsPossible) {
		this(color, putRowsHighestAsPossible, true);
	}

	public MainPanel(Color color, boolean putRowsHighestAsPossible, boolean scrollHorizontally) {

		shouldPutRowsHighestAsPossible = putRowsHighestAsPossible;
		if (scrollHorizontally) {
			panel = new JPanel();
		}
		else {
			panel = new HorizontallyNonscrollablePanel();
		}

		if (color == null) {
			panel.setOpaque(false);
		}
		else {
			panel.setBackground(color);
		}

		panel.setLayout(new GridBagLayout());
		rows = new LinkedList<JPanel>();
	}

	public JPanel addRow(SimpleRow row) {
		JPanel panel = addComponentsToSinglePanel(row.getComponents(), mapComponentToFilling(row));
		int fill = row.getFillingType();
		createConstraintsAndAdd(panel, row.getAnchor(), fill);
		updateView();
		return panel;
	}

	private Map<JComponent, Integer> mapComponentToFilling(SimpleRow row) {
		Map<JComponent, Integer> componentsFilling = new HashMap<JComponent, Integer>();
		JComponent[] horizontal = row.getHorizontallyFilledElements();
		List<JComponent> vertical = new ArrayList<JComponent>(
				Arrays.asList(row.getVerticallyFilledElements()));

		if (row.getComponents().length == 1) {
			componentsFilling.put(row.getComponents()[0], row.getFillingType());
		}
		System.out.println("to fill horizontal: " + horizontal.length);
		System.out.println("to fill vertical: " + vertical.size());
		for (JComponent hor : horizontal) {
			boolean bothSides = false;
			for (JComponent ver : vertical) {
				if (hor == ver) {
					componentsFilling.put(hor, GridBagConstraints.BOTH);
					vertical.remove(ver);
					bothSides = true;
					break;
				}
			}
			if (!bothSides) {
				componentsFilling.put(hor, GridBagConstraints.HORIZONTAL);
			}

		}
		for (JComponent v : vertical) {
			componentsFilling.put(v, GridBagConstraints.VERTICAL);
		}
		return componentsFilling;
	}

	private JPanel addComponentsToSinglePanel(JComponent[] components,
			Map<JComponent, Integer> componentsFilling) {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;

		int a = gapInsideRow;
		gbc.insets = new Insets(a, a, a, a);
		for (JComponent compo : components) {
			System.out.println("components size: " + components.length);
			if (compo == null) {
				continue;
			}
			if (componentsFilling.containsKey(compo)) {
				gbc.fill = componentsFilling.get(compo);
				if (gbc.fill == GridBagConstraints.VERTICAL) {
					gbc.weighty = 1;
				}
				else if (gbc.fill == GridBagConstraints.HORIZONTAL) {
					gbc.weightx = 1;
				}
				else if (gbc.fill == GridBagConstraints.BOTH) {
					gbc.weightx = 1;
					gbc.weighty = 1;
				}

			}
			else {
				gbc.weightx = 0;
				gbc.weighty = 0;
			}

			p.add(compo, gbc);
		}

		return p;
	}

	private void createConstraintsAndAdd(JPanel p, int anchor, int fill) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = rows.size();
		c.weightx = 1;
		if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL) {
			c.weighty = 1;
		}
		else {
			c.weighty = 0;
		}
		if (shouldPutRowsHighestAsPossible) {
			updateRowsAboveMe();
			c.weighty = 1;
			// c.anchor = GridBagConstraints.NORTHWEST;
			if (c.fill == GridBagConstraints.BOTH) {
				c.fill = GridBagConstraints.HORIZONTAL;
			}
		}

		c.anchor = anchor;
		c.fill = fill;
		int a = gapBetweenRows;
		c.insets = new Insets(a, a, a, a);
		System.out.printf("constraint: fill %d, weightx %f, weighty %f, anchor %d", c.fill,
				c.weightx, c.weighty, c.anchor);
		panel.add(p, c);
		rows.add(p);
	}

	private void updateRowsAboveMe() {
		if (rows.size() > 0) {
			GridBagLayout g = (GridBagLayout) panel.getLayout();
			GridBagConstraints c = g.getConstraints(rows.get(rows.size() - 1));
			c.weighty = 0;
			panel.remove(rows.get(rows.size() - 1));
			panel.add(rows.get(rows.size() - 1), c);
		}
	}

	public void setAsLastRow(JComponent... components) {
		setAsRow(rows.size(), components);
	}

	public MainPanel setAsRow(int number, JComponent... components) {
		if (rows.size() < number + 1) {
			// createRow(GridBagConstraints.WEST, 1, components);
			return this;
		}

		GridBagLayout g = (GridBagLayout) panel.getLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel row = rows.get(number);
		c = g.getConstraints(row);

		panel.remove(row);
		rows.remove(row);

		JPanel asRow = new JPanel();
		// addComponentsToSinglePanel(GridBagConstraints.NONE, components);
		panel.add(asRow, c);
		rows.add(number, asRow);
		updateView();
		return this;
	}

	public void removeRow(int number) {
		JPanel row = rows.get(number);
		removeAndUpdateRows(row, number);
	}

	public void addElementsToRow(int rowNumber, JComponent... elements) {
		addElementsToRow(rows.get(rowNumber), elements);
	}

	public void addElementsToRow(JPanel row, JComponent... elements) {

		GridBagConstraints c = ((GridBagLayout) row.getLayout())
				.getConstraints(row.getComponent(0));
		for (JComponent element : elements) {
			// c.gridx++;

			row.add(element, c); // TODO why it works?
		}
		updateView();
	}

	public void removeRow(JPanel row) {
		removeAndUpdateRows(row, rows.indexOf(row));
	}

	private void removeAndUpdateRows(JPanel row, int lastRowToUpdate) {
		movePanels(Direction.BACKWARD, lastRowToUpdate);
		panel.remove(row);
		rows.remove(row);
		updateView();
	}

	private enum Direction {
		FORWARD, BACKWARD;
	}

	private void movePanels(Direction direction, int startIndex) {
		for (int i = rows.size() - 1; i >= startIndex; i--) {
			GridBagLayout g = (GridBagLayout) panel.getLayout();
			JPanel row = rows.get(i);
			GridBagConstraints c = g.getConstraints(row);
			System.out.printf("constraint: fill %d, weightx %f, weighty %f, anchor %d", c.fill,
					c.weightx, c.weighty, c.anchor);
			if (direction.equals(Direction.FORWARD)) {
				c.gridy++;
			}
			else if (direction.equals(Direction.BACKWARD)) {
				c.gridy--;
			}
			c.weighty = 0;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.BOTH;
			panel.remove(row);
			panel.add(row, c);
		}
	}

	public SubPanel divideRow(int number) {
		SubPanel p = new SubPanel();
		rows.add(p.getPanel());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = number;
		c.weightx = 1;
		c.weighty = 1;
		panel.add(p.getPanel(), c);
		return p;
	}

	public void insertRow(int number, JComponent... components) {
		movePanels(Direction.FORWARD, number);
		JPanel newRow = new JPanel();
		// addComponentsToSinglePanel(GridBagConstraints.NONE, components);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = number;
		c.weightx = 1;
		c.weighty = 1;
		panel.add(newRow, c);
		rows.add(number, newRow);
		updateView();
	}

	public int getNumberOfRows() {
		return rows.size();
	}

	public List<JPanel> getRows() {
		return rows;
	}

	private void updateView() {
		panel.revalidate();
		panel.repaint();

	}

	public JPanel getPanel() {
		return panel;
	}

	public Component getElementFromRow(int rowNumber0Based, int elementNumber0Based) {
		JPanel j = rows.get(rowNumber0Based);
		return j.getComponent(elementNumber0Based);
	}

	public void removeRowWithElements(Component... elements) {
		JPanel panel = findRow(elements);
		if (panel != null) {
			this.panel.remove(panel);
			rows.remove(panel);
		}
	}

	public void removeElementsFromRow(int rowNumber, Component... elements) {
		JPanel row = rows.get(rowNumber);
		for (int i = 0; i < row.getComponentCount(); i++) {
			System.out.println("in panel");
			System.out.println(row.getComponent(i));
		}
		for (Component c : elements) {
			System.out.println("to remove");
			System.out.println(c);
			row.remove(c);
		}

	}

	public void removeLastElementFromRow(int rowNumber) {
		JPanel row = rows.get(rowNumber);
		row.remove(row.getComponentCount() - 1);
		updateView();
	}

	private JPanel findRow(Component... elements) {
		for (JPanel panel : rows) {
			Set<Component> e = new HashSet<Component>(Arrays.asList(panel.getComponents()));
			if (e.equals(new HashSet<Component>(Arrays.asList(elements)))) {
				return panel;
			}
		}
		return null;
	}

	public void clear() {
		panel.removeAll();
		rows.clear();
	}

	public void setBackground(Color c) {
		panel.setBackground(c);
	}

	public boolean rowContainsComponent(int rowNumber, Component c) {
		JPanel row = rows.get(rowNumber);
		Component[] components = row.getComponents();
		for (Component componentInRow : components) {
			if (componentInRow.equals(c)) {
				return true;
			}
		}
		return false;
	}

}
