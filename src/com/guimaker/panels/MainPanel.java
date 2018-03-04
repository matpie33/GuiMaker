package com.guimaker.panels;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainPanel {

	private List<JComponent> rows;
	private JPanel panel;
	private int gapBetweenRows = 4;
	private int gapInsideRow = 3;
	private int gapRightSide;
	private final boolean shouldPutRowsHighestAsPossible;
	private Border borderToUse;
	private Color rowColor;
	private int numberOfColumns;
	private int numberOfRows;
	private Color originalBackgroundColor;
	private boolean skipInsetsForExtremeEdges = false;

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

	public MainPanel(Color color, boolean putRowsHighestAsPossible,
			boolean scrollHorizontally) {

		numberOfColumns = 0;
		numberOfRows = 0;
		originalBackgroundColor = color;
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
		rows = new LinkedList<>();

	}

	public void setSkipInsetsForExtremeEdges(
			boolean skipInsetsForExtremeEdges) {
		this.skipInsetsForExtremeEdges = skipInsetsForExtremeEdges;
	}

	public void setOriginalBackgroundColor() {
		setBackground(originalBackgroundColor);
	}

	public void addElementsInColumnStartingFromColumn(
			JComponent componentToFill, int startingColumn,
			JComponent... elements) {
		addElementsInColumnStartingFromColumn(
				Arrays.asList(new JComponent[] { componentToFill }),
				FillType.BOTH, startingColumn, elements);
	}

	public void addElementsInColumnStartingFromColumn(
			List<JComponent> componentsToFill, FillType fillType,
			int startingColumn, JComponent... elements) {
		if (numberOfColumns < elements.length) {
			numberOfColumns = elements.length;
		}
		for (JComponent element : elements) {
			setWeightyToZeroForPreviousRow();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = startingColumn++;
			c.gridy = numberOfRows + rows.size();
			c.anchor = GridBagConstraints.NORTHWEST;
			int xGap = gapInsideRow;
			int yGap = gapBetweenRows;
			c.insets = new Insets(yGap, 0, yGap, xGap);
			c.weighty = 1;

			if (componentsToFill.contains(element)) {
				c.fill = fillType.getGridBagConstraintsFilling();
				c.weightx = 1;
				if (c.fill == GridBagConstraints.BOTH
						|| c.fill == GridBagConstraints.VERTICAL) {
					c.weighty = 1;
				}
			}
			else {
				c.weightx = 0;
			}

			panel.add(element, c);
		}
		numberOfRows++;
	}

	private void setWeightyToZeroForPreviousRow() {
		for (Component component : panel.getComponents()) {
			GridBagLayout gridBagLayout = (GridBagLayout) panel.getLayout();
			GridBagConstraints constraints = gridBagLayout
					.getConstraints(component);
			if (constraints.gridy == numberOfRows - 1 + rows.size()){
				constraints.weighty = 0;
				panel.remove(component);
				panel.add(component, constraints);
			}
		}
	}

	public void insertElementBeforeOtherElement(
			JComponent elementBeforeWhichWeInsert, JComponent elementToInsert) {
		insertElementBeforeOtherElement(panel, elementBeforeWhichWeInsert,
				elementToInsert);

	}

	private void insertElementBeforeOtherElement(Container containerToCheck,
			JComponent elementBeforeWhichWeInsert, JComponent elementToInsert) {
		int elementBeforeWeInsertGridX = Integer.MAX_VALUE;
		boolean found = false;
		for (Component component : containerToCheck.getComponents()) {
			LayoutManager layout = containerToCheck.getLayout();
			GridBagLayout gridBagLayout = (GridBagLayout) layout;
			GridBagConstraints constraints = gridBagLayout
					.getConstraints(elementBeforeWhichWeInsert);
			if (component instanceof JPanel) {
				insertElementBeforeOtherElement((JPanel) component,
						elementBeforeWhichWeInsert, elementToInsert);
			}
			else if (component == elementBeforeWhichWeInsert
					|| constraints.gridx > elementBeforeWeInsertGridX) {
				if (layout instanceof GridBagLayout) {
					containerToCheck.remove(component);
					if (!found) {
						addElement(constraints.gridy, constraints.gridx,
								containerToCheck, elementToInsert);
						found = true;
					}
					constraints.gridx = constraints.gridx + 1;
					containerToCheck.add(component, constraints);
					if (elementBeforeWeInsertGridX == Integer.MAX_VALUE)
						elementBeforeWeInsertGridX = constraints.gridx - 1;
				}

			}

		}
		if (!found) {
			return;
		}
		updateView();
	}

	private void addElement(int row, int column, Container container,
			JComponent element) {
		GridBagConstraints constraints = initializeGridBagConstraints();
		constraints.gridx = column;
		constraints.gridy = row;
		container.add(element, constraints);

	}

	public void addElementsInColumnStartingFromColumn(
			JComponent componentToFill, int startingColumn, FillType fillType,
			JComponent... elements) {
		addElementsInColumnStartingFromColumn(
				Arrays.asList(new JComponent[] { componentToFill }), fillType,
				startingColumn, elements);
	}

	public void addElementsInColumnStartingFromColumn(
			List<JComponent> componentsToFill, int startingColumn,
			JComponent... elements) {
		addElementsInColumnStartingFromColumn(componentsToFill, FillType.BOTH,
				startingColumn, elements);
	}

	public void addElementsInColumnStartingFromColumn(int columnNumber,
			JComponent... elements) {
		addElementsInColumnStartingFromColumn(
				Arrays.asList(new JComponent[] {}), columnNumber, elements);
	}

	public JComponent addRows(SimpleRow simpleRows) {
		JComponent panel = null;
		for (SimpleRow row : simpleRows.getBuilder().getRows()) {
			panel = addRow(row, rows.size());
		}
		return panel;
	}

	public JComponent addRow(SimpleRow simpleRows) {
		return addRow(simpleRows, rows.size());
	}

	private JComponent addRow(SimpleRow row, int rowNumber) {
		JComponent panel = addComponentsToSinglePanel(row.getComponents(),
				mapComponentToFilling(row), row.isUseAllExtraVerticalSpace());
		if (panel == null) {
			return null;
		}
		if (row.isBorderEnabled() && (borderToUse != null
				|| row.getBorder() != null)) {
			panel.setBorder(
					borderToUse != null ? borderToUse : row.getBorder());
		}
		if (row.isOpaque() && (rowColor != null || row.getColor() != null)) {
			panel.setBackground(rowColor != null ? rowColor : row.getColor());
			panel.setOpaque(true);
		}
		createConstraintsAndAdd(panel, row, rowNumber);
		return panel;
	}

	private Map<JComponent, Integer> mapComponentToFilling(SimpleRow row) {
		Map<JComponent, Integer> componentsFilling = new HashMap<JComponent, Integer>();
		JComponent[] horizontal = row.getHorizontallyFilledElements();
		List<JComponent> vertical = new ArrayList<JComponent>(
				Arrays.asList(row.getVerticallyFilledElements()));

		if (row.getComponents().length == 1) {
			componentsFilling.put(row.getComponents()[0],
					row.getFillTypeAsGridBagConstraint());
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

	private JComponent addComponentsToSinglePanel(JComponent[] components,
			Map<JComponent, Integer> componentsFilling,
			boolean useExtraSpaceVertically) {
		if (components.length == 1) {
			return components[0];
		}
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.setOpaque(false);

		GridBagConstraints gbc = initializeGridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;

		int i = 0;
		for (JComponent compo : components) {
			if (compo == null) {
				continue;
			}
			if (componentsFilling.containsKey(compo)) {
				gbc.fill = componentsFilling.get(compo);
				gbc.weighty = useExtraSpaceVertically ? 1 : 0;
				if (gbc.fill == GridBagConstraints.HORIZONTAL) {
					gbc.weightx = 1;
				}
				else if (gbc.fill == GridBagConstraints.VERTICAL) {
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
			if (skipInsetsForExtremeEdges) {
				if (i == components.length - 1) {
					gbc.insets.right = 0;
				}
				else if (i == 1) {
					gbc.insets = initializeGridBagConstraints().insets;
				}
			}

			p.add(compo, gbc);
			gbc.gridx = gbc.gridx + 1;
			i++;
		}

		return p;
	}

	private GridBagConstraints initializeGridBagConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;

		int a = gapInsideRow;
		int b = gapRightSide;
		int rightGap = a != b && b > 0 ? b : a;
		gbc.insets = new Insets(0, 0, 0, rightGap);
		return gbc;
	}

	private void createConstraintsAndAdd(JComponent p, SimpleRow row,
			int rowNumber) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = rowNumber;
		c.weightx = 1;
		int fill = row.getFillTypeAsGridBagConstraint();
		int anchor = row.getAnchor().getAnchor();
		if (shouldPutRowsHighestAsPossible || row.isUseAllExtraVerticalSpace()
				|| fill == GridBagConstraints.BOTH
				|| fill == GridBagConstraints.VERTICAL) {
			c.weighty = 1;
			removeFillingFromOtherPanels(rowNumber);
		}
		else {
			c.weighty = 0;
		}

		if (shouldPutRowsHighestAsPossible) {
			if (rowNumber >= rows.size()) {
				updateRowsAboveMe();
				c.weighty = 1;
			}
			else {
				c.weighty = 0;
			}

			if (c.fill == GridBagConstraints.BOTH) {
				c.fill = GridBagConstraints.HORIZONTAL;
			}
		}

		c.anchor = anchor;
		c.fill = fill;
		int a = gapBetweenRows;
		if (!skipInsetsForExtremeEdges) {
			c.insets = new Insets(a, a, a, a);
		}
		else {
			c.insets = new Insets(a, 0, a, 0);
		}

		panel.add(p, c);
		rows.add(rowNumber, p);
	}

	private void removeFillingFromOtherPanels(int rowNumber) {
		if (shouldPutRowsHighestAsPossible) {
			return;
		}
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		for (int i = 0; i < rowNumber; i++) {
			Component c = panel.getComponent(i);
			GridBagConstraints constr = g.getConstraints(c);
			if (constr.weighty != 0) {
				constr.weighty = 0;
				panel.remove(c);
				panel.add(c, constr);
			}
		}

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

		JComponent row = rows.get(number);
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
		else if (numberOfRows > 0) {
			removeRowInAColumnWay(numberOfRows - 1);
		}

	}

	public void removeRow(int number) {
		JComponent row = rows.get(number);
		removeAndUpdateRows(row, number);
	}

	public void addElementsToLastRow(JComponent... elements) {
		addElementsToRow(rows.size() - 1, elements);
	}

	public void addElementsToRow(int rowNumber, JComponent... elements) {
		addElementsToRow(rows.get(rowNumber), elements);
	}

	public void addElementsToRow(JComponent row, JComponent... elements) {

		addOrRemoveFillingFromLastElementInRow(false, row);
		GridBagConstraints c = ((GridBagLayout) row.getLayout())
				.getConstraints(row.getComponent(row.getComponentCount() - 1));
		c.weightx = 1;
		for (JComponent element : elements) {
			row.add(element, c); // TODO why it works?
		}
		updateView();
	}

	private void addOrRemoveFillingFromLastElementInRow(boolean addFilling,
			JComponent row) {
		GridBagConstraints c = ((GridBagLayout) row.getLayout())
				.getConstraints(row.getComponent(row.getComponentCount() - 1));
		Component comp = row.getComponent(row.getComponentCount() - 1);
		row.remove(comp);
		c.weightx = addFilling ? 1 : 0;
		row.add(comp, c);
	}

	private void removeRow(JComponent row) {
		removeAndUpdateRows(row, rows.indexOf(row));
	}

	private void removeAndUpdateRows(JComponent row, int lastRowToUpdate) {
		movePanels(Direction.BACKWARD, lastRowToUpdate);
		panel.remove(row);
		rows.remove(row);
		if (shouldPutRowsHighestAsPossible) {
			giveLastRowTheRestOfSpace();
		}
		updateView();
	}

	public void insertRowStartingFromColumn(int columnNumber, int rowNumber,
			JComponent... components) {
		//TODO theres a chaos already with the inserting methods, and the add row/add elements in column
		for (Component c : panel.getComponents()) {
			GridBagLayout layout = (GridBagLayout) panel.getLayout();
			GridBagConstraints constraints = layout.getConstraints(c);
			if (constraints.gridy >= rowNumber) {
				constraints.gridy = constraints.gridy + 1;
				panel.remove(c);
				panel.add(c, constraints);
			}
		}
		for (JComponent component : components) {
			addElement(rowNumber, columnNumber, panel, component);
			columnNumber++;
		}
		numberOfRows++;
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
			JComponent row = rows.get(i);
			GridBagConstraints c = getConstraintsForRow(i);
			if (direction.equals(Direction.FORWARD)) {
				c.gridy++;
			}
			else if (direction.equals(Direction.BACKWARD)) {
				c.gridy--;
			}
			if (!shouldPutRowsHighestAsPossible) {
				c.weighty = 0;
			}

			panel.remove(row);
			panel.add(row, c);
		}

	}

	private GridBagConstraints getConstraintsForRow(int rowNumber) {
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		JComponent row = rows.get(rowNumber);
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
		addRow(row, number);
	}

	public int getNumberOfRows() {
		return Math.max(rows.size(), numberOfRows);
	}

	public List<JComponent> getRows() {
		return rows;
	}

	public void updateView() {
		panel.revalidate();
		panel.repaint();

	}

	public JPanel getPanel() {
		return panel;
	}

	public void removeRowInAColumnWay(int rowNumber) {
		for (Component component : panel.getComponents()) {
			GridBagLayout gridBagLayout = (GridBagLayout) panel.getLayout();
			if (gridBagLayout.getConstraints(component).gridy == rowNumber) {
				panel.remove(component);
			}
		}
		updateView();

	}

	public void removeRowWithElements(Component... elements) {
		JComponent panel = findRow(elements);
		if (panel != null) {
			this.panel.remove(panel);
			rows.remove(panel);
		}
	}

	public void removeElementsFromRow(int rowNumber, Component... elements) {
		JComponent row = rows.get(rowNumber);
		for (Component c : elements) {
			row.remove(c);
		}
		addOrRemoveFillingFromLastElementInRow(true, row);
		updateView();

	}

	public void removeLastElementFromLastRow() {
		removeLastElementFromRow(rows.size() - 1);
	}

	public void removeLastElementFromRow(int rowNumber) {
		JComponent row = rows.get(rowNumber);
		removeElementsFromRow(rowNumber,
				row.getComponent(row.getComponentCount() - 1));
	}

	private JComponent findRow(Component... elements) {
		for (JComponent panel : rows) {
			Set<Component> e = new HashSet<Component>(
					Arrays.asList(panel.getComponents()));
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
		JComponent row = rows.get(rowNumber);
		Component[] components = row.getComponents();
		for (Component componentInRow : components) {
			if (componentInRow.equals(c)) {
				return true;
			}
		}
		return false;
	}

	public void changeVisibilityOfLastElementInRow(int rowNumber,
			boolean visible) {
		JComponent panel = rows.get(rowNumber);
		Component c = panel.getComponent(panel.getComponentCount() - 1);
		c.setVisible(visible);
	}

	public void setPanelColor(int rowNumber, Color color) {
		JComponent panel = rows.get(rowNumber);
		panel.setOpaque(true);
		panel.setBackground(color);
	}

	public void clearPanelColor(int rowNumber) {
		JComponent panel = rows.get(rowNumber);
		panel.setOpaque(false);
	}

	public int getIndexOfPanel(JComponent panel) {
		return rows.indexOf(panel);
	}

}
