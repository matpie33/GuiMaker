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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.guimaker.row.Rows;
import com.guimaker.row.SimpleRow;

public class MainPanel {

	private List<JPanel> rows;
	private JPanel panel;
	private int gapBetweenRows = 2;
	private int gapInsideRow = 3;
	private int gapRightSide;
	private final boolean shouldPutRowsHighestAsPossible;
	private Border borderToUse;
	private Color rowColor;

	public void setGapsBetweenRowsTo0() {
		gapBetweenRows = 0;
	}

	public void setGapsBetweenRowsTo(int gap) {
		gapBetweenRows = gap;
	}

	public void setGapsRightSideBetweenColumnsTo(int gap) {
		gapRightSide = gap;
	}

	public void setRightBorder() {
		borderToUse = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
	}

	public void setBorder(Border border) {
		borderToUse = border;
	}

	public void setRowColor(Color color) {
		rowColor = color;
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

	public List<JPanel> addRows(Rows rows) {
		List<JPanel> panels = new ArrayList<>();
		for (SimpleRow row : rows.getRows()) {
			panels.add(addRow(row));
		}
		return panels;
	}

	public JPanel addRow(SimpleRow row) {
		JPanel panel = addComponentsToSinglePanel(row.getComponents(), mapComponentToFilling(row));
		if (rowColor != null) {
			panel.setBackground(rowColor);
			panel.setOpaque(true);
		}
		int fill = row.getFillTypeAsGridBagConstraint();
		createConstraintsAndAdd(panel, row.getAnchor().getAnchor(), fill);
		checkForFillingNeed();
		updateView();
		return panel;
	}

	private void checkForFillingNeed() {
		if (shouldPutRowsHighestAsPossible) {
			return;
		}
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		Component[] components = panel.getComponents();
		boolean anyFillerPresent = false;
		for (Component c : components) {
			GridBagConstraints constr = g.getConstraints(c);
			if (constr.fill != 0) {
				anyFillerPresent = true;
				break;
			}
		}
		if (!anyFillerPresent) {
			Component c = components[0];
			GridBagConstraints con = g.getConstraints(c);
			con.weighty = 1;
			panel.remove(c);
			panel.add(c, con);
		}
	}

	private Map<JComponent, Integer> mapComponentToFilling(SimpleRow row) {
		Map<JComponent, Integer> componentsFilling = new HashMap<JComponent, Integer>();
		JComponent[] horizontal = row.getHorizontallyFilledElements();
		List<JComponent> vertical = new ArrayList<JComponent>(
				Arrays.asList(row.getVerticallyFilledElements()));

		if (row.getComponents().length == 1) {
			componentsFilling.put(row.getComponents()[0], row.getFillTypeAsGridBagConstraint());
		}
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
		if (borderToUse != null) {
			p.setBorder(borderToUse);
		}
		p.setLayout(new GridBagLayout());
		p.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTHWEST;

		int a = gapInsideRow;
		int b = gapRightSide;
		int rightGap = a != b && b > 0 ? b : a;
		gbc.insets = new Insets(a, a, a, rightGap);
		int i = 0;
		for (JComponent compo : components) {
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
			if (i == components.length - 1) {
				gbc.weightx = 1;
			}
			i++;

			p.add(compo, gbc);
		}

		return p;
	}

	private void createConstraintsAndAdd(JPanel p, int anchor, int fill) {
		createConstraintsAndAdd(p, anchor, fill, rows.size());
	}

	private void createConstraintsAndAdd(JPanel p, int anchor, int fill, int rowNumber) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = rowNumber;
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
			if (c.fill == GridBagConstraints.BOTH) {
				c.fill = GridBagConstraints.HORIZONTAL;
			}
		}

		c.anchor = anchor;
		c.fill = fill;
		int a = gapBetweenRows;
		c.insets = new Insets(a, a, a, a);
		panel.add(p, c);
		rows.add(rowNumber, p);
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
			return this;
		}

		GridBagLayout g = (GridBagLayout) panel.getLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel row = rows.get(number);
		c = g.getConstraints(row);

		panel.remove(row);
		rows.remove(row);

		JPanel asRow = new JPanel();
		panel.add(asRow, c);
		rows.add(number, asRow);
		updateView();
		return this;
	}

	public void removeLastRow() {
		if (!rows.isEmpty()) {
			removeRow(rows.get(rows.size() - 1));
		}

	}

	public void removeRow(int number) {
		JPanel row = rows.get(number);

		removeAndUpdateRows(row, number);
	}

	public void addElementsToLastRow(JComponent... elements) {
		addElementsToRow(rows.size() - 1, elements);
	}

	public void addElementsToRow(int rowNumber, JComponent... elements) {
		addElementsToRow(rows.get(rowNumber), elements);
	}

	public void addElementsToRow(JPanel row, JComponent... elements) {

		GridBagConstraints c = ((GridBagLayout) row.getLayout())
				.getConstraints(row.getComponent(0));
		for (JComponent element : elements) {
			row.add(element, c); // TODO why it works?
		}
		updateView();
	}

	private void removeRow(JPanel row) {
		removeAndUpdateRows(row, rows.indexOf(row));
	}

	private void removeAndUpdateRows(JPanel row, int lastRowToUpdate) {
		movePanels(Direction.BACKWARD, lastRowToUpdate);
		panel.remove(row);
		rows.remove(row);
		if (shouldPutRowsHighestAsPossible) {
			giveLastRowTheRestOfSpace();
		}
		updateView();
	}

	private void giveLastRowTheRestOfSpace() {
		if (rows.isEmpty()) {
			return;
		}
		int lastRow = rows.size() - 1;
		GridBagConstraints cd = getConstraintsForRow(lastRow);
		cd.weighty = 1;
		panel.remove(rows.get(lastRow));
		panel.add(rows.get(lastRow), cd);
	}

	private enum Direction {
		FORWARD, BACKWARD;
	}

	private void movePanels(Direction direction, int startIndex) {

		for (int i = rows.size() - 1; i >= startIndex; i--) {
			JPanel row = rows.get(i);
			GridBagConstraints c = getConstraintsForRow(i);
			if (direction.equals(Direction.FORWARD)) {
				c.gridy++;
			}
			else if (direction.equals(Direction.BACKWARD)) {
				c.gridy--;
			}
			c.weighty = 0;

			panel.remove(row);
			panel.add(row, c);
		}

	}

	private GridBagConstraints getConstraintsForRow(int rowNumber) {
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		JPanel row = rows.get(rowNumber);
		return g.getConstraints(row);
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

	public void insertRow(int number, SimpleRow row) {
		movePanels(Direction.FORWARD, number);
		JPanel panel = addComponentsToSinglePanel(row.getComponents(), mapComponentToFilling(row));
		int fill = row.getFillTypeAsGridBagConstraint();
		createConstraintsAndAdd(panel, row.getAnchor().getAnchor(), fill, number);
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

	public void removeRowWithElements(Component... elements) {
		JPanel panel = findRow(elements);
		if (panel != null) {
			this.panel.remove(panel);
			rows.remove(panel);
		}
	}

	public void removeElementsFromRow(int rowNumber, Component... elements) {
		JPanel row = rows.get(rowNumber);
		for (Component c : elements) {
			row.remove(c);
		}

	}

	public void removeLastElementFromLastRow() {
		removeLastElementFromRow(rows.size() - 1);
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

	public void changeVisibilityOfLastElementInRow(int rowNumber, boolean visible) {
		JPanel panel = rows.get(rowNumber);
		Component c = panel.getComponent(panel.getComponentCount() - 1);
		c.setVisible(visible);
	}

	public void setPanelColor(int rowNumber, Color color) {
		JPanel panel = rows.get(rowNumber);
		panel.setOpaque(true);
		panel.setBackground(color);
	}

	public void clearPanelColor(int rowNumber) {
		JPanel panel = rows.get(rowNumber);
		panel.setOpaque(false);
	}

	public int getIndexOfPanel(JPanel panel) {
		return rows.indexOf(panel);
	}

}
