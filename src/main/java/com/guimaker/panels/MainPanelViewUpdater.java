package com.guimaker.panels;

import com.guimaker.enums.*;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.ComplexRow;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainPanelViewUpdater {

	private boolean rowsHaveNonZeroWeightsY;
	private TreeMap<Integer, RowType> rowNumberToTypeMap = new TreeMap<>();
	private Border rowBorder;
	private Color rowColor;
	private int numberOfRows;
	private boolean skipInsetsForExtremeEdges;
	private boolean opaque;
	private boolean opaqueRows;
	private ElementsShifter elementsShifter;
	private List<JComponent> rows;
	private PanelDisplayMode displayMode;
	private JPanel panel;
	private int paddingRight;
	private int paddingLeft;
	private int paddingTop;
	private int paddingBottom;
	private boolean shouldPutRowsHighestAsPossible;
	private int gapBetweenRows;

	private ColumnPanelCreator columnPanelCreator;
	private PanelTextInputsManager panelTextInputsManager;

	public MainPanelViewUpdater(PanelConfiguration panelConfiguration,
			JPanel panel) {
		this.skipInsetsForExtremeEdges = panelConfiguration.isSkipInsetsForExtremeEdges();
		this.opaque = panelConfiguration.isOpaque();
		this.displayMode = panelConfiguration.getPanelDisplayMode();
		this.paddingRight = panelConfiguration.getPaddingRight();
		this.paddingLeft = panelConfiguration.getPaddingLeft();
		this.paddingTop = panelConfiguration.getPaddingTop();
		this.paddingBottom = panelConfiguration.getPaddingBottom();
		this.shouldPutRowsHighestAsPossible = panelConfiguration.shouldPutRowsAsHighestAsPossible();
		this.panel = panel;
		this.gapBetweenRows = panelConfiguration.getGapBetweenRows();

		elementsShifter = new ElementsShifter(panel,
				shouldPutRowsHighestAsPossible);
		rows = new ArrayList<>();
		columnPanelCreator = new ColumnPanelCreator(displayMode,
				gapBetweenRows);
		columnPanelCreator.setPadding(paddingTop, paddingRight, paddingBottom,
				paddingLeft);
		panelTextInputsManager = new PanelTextInputsManager(displayMode);
	}

	public void addRows(ComplexRow complexRow) {
		for (AbstractSimpleRow row : complexRow.getAllRows()) {
			addRow(row, rows.size());
		}
	}

	//TODO there is problem when we first use addElementsInCOlumn, then clear
	// the panel, then use the method add row - > they use different panel,
	// and the changes would not be visible
	public JComponent addRow(AbstractSimpleRow abstractSimpleRows) {
		if (abstractSimpleRows instanceof ComplexRow) {
			throw new IllegalArgumentException("Incorrect method used: trying"
					+ " to add multiple rows using 'add single row' method");
		}
		return addRow(abstractSimpleRows, rows.size());
	}

	private JComponent addRow(AbstractSimpleRow row, int rowNumber) {
		if (!row.shouldAddRow()) {
			return null;
		}
		JComponent panel = addComponentsToSinglePanel(row.getComponents(),
				mapComponentToFilling(row), row);
		//TODO its better to always create a panel even if there is just 1
		// component, because it's easier to add elements to this row later
		if (panel == null) {
			return null;
		}
		if (panel instanceof JPanel) {
			if (row.isBorderEnabled() && (rowBorder != null
					|| row.getBorder() != null)) {
				panel.setBorder(
						rowBorder != null ? rowBorder : row.getBorder());
			}
			if ((rowColor != null || row.getColor() != null)) {
				panel.setBackground(
						row.getColor() != null ? row.getColor() : rowColor);
				panel.setOpaque(
						row.isOpaque() != null ? row.isOpaque() : opaque);
			}
		}
		if (!opaque) {
			panel.setOpaque(false);
		}
		createConstraintsAndAdd(panel, row, rowNumber);
		return panel;
	}

	private Map<JComponent, Integer> mapComponentToFilling(
			AbstractSimpleRow row) {
		Map<JComponent, Integer> componentsFilling = new HashMap<>();
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
			Map<JComponent, Integer> componentsFilling, AbstractSimpleRow row) {
		if (components.length == 1 && components[0] instanceof JPanel) {
			return components[0];
		}
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.setOpaque(false);

		GridBagConstraints gbc = initializeGridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;

		int i = 0;
		JTextComponent firstTextComponentInRow = null;
		for (JComponent compo : components) {
			if (compo == null || row.getComponentsThatDidntMatchCondition()
									.contains(compo)) {
				continue;
			}
			boolean isTextInput = panelTextInputsManager.manageTextInput(compo,
					firstTextComponentInRow);
			if (isTextInput && firstTextComponentInRow == null) {
				firstTextComponentInRow = (JTextComponent) compo;
			}
			if (displayMode.equals(PanelDisplayMode.VIEW)
					&& !(compo instanceof AbstractButton)) {
				compo.setEnabled(false);
			}
			if (componentsFilling.containsKey(compo)) {
				gbc.fill = componentsFilling.get(compo);
				gbc.weighty = row.isUseAllExtraVerticalSpace() ? 1 : 0;
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
			if (i == components.length - 1
					&& gbc.fill != GridBagConstraints.HORIZONTAL) {
				gbc.weightx = 1;
			}
			if (!row.isBorderEnabled() && i == 0) {
				gbc.insets.left = 0;
			}
			if (skipInsetsForExtremeEdges) {
				//TODO this variable should probably be removed
			}

			if (i != components.length - 1) {
				gbc.insets.right = paddingRight;
				//TODO change it: padding right -> distance between elements
				// inside row, padding left -> distance between row and panel
				// edges
			}
			p.add(compo, gbc);
			gbc.gridx = gbc.gridx + 1;
			i++;
		}
		if (!row.isWrapWithPanel() && components.length == 1) {
		}

		return p;
	}

	private GridBagConstraints initializeGridBagConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 0, 0, 0);
		return gbc;
	}

	private void createConstraintsAndAdd(JComponent p, AbstractSimpleRow row,
			int rowNumber) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = rowNumber;
		c.weightx = 1;
		int fill = row.getFillTypeAsGridBagConstraint();
		int anchor = row.getAnchor()
						.getAnchor();
		if (shouldPutRowsHighestAsPossible || row.isUseAllExtraVerticalSpace()
				|| fill == GridBagConstraints.BOTH
				|| fill == GridBagConstraints.VERTICAL) {
			c.weighty = 1;
			if (!rowsHaveNonZeroWeightsY) {
				removeFillingFromOtherPanels(rowNumber);
			}

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

		if (row.getWeightY() != 0) {
			c.weighty = row.getWeightY();
			rowsHaveNonZeroWeightsY = true;
		}

		c.anchor = anchor;
		c.fill = fill;
		int a = gapBetweenRows;
		c.insets = new Insets(a, paddingLeft, a, paddingLeft);

		panel.add(p, c);
		rows.add(rowNumber, p);
		rowNumberToTypeMap.put(rowNumber, RowType.ROW_BELOW_ROW);
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

	private void setAsRow(int number) {
		if (rows.size() < number + 1) {
			return;
		}

		GridBagLayout g = (GridBagLayout) panel.getLayout();

		JComponent row = rows.get(number);
		GridBagConstraints c = g.getConstraints(row);

		panel.remove(row);
		rows.remove(row);

		JPanel asRow = new JPanel();
		panel.add(asRow, c);
		rows.add(number, asRow);
		updateView();
	}

	public void removeLastRow() {
		if (!rows.isEmpty()) {
			removeRow(rows.get(rows.size() - 1));
		}
		else if (numberOfRows > 0) {
			removeRowInAColumnWay(numberOfRows - 1);
		}

	}

	private void removeRowInAColumnWay(int rowNumber) {
		for (Component component : panel.getComponents()) {
			GridBagLayout gridBagLayout = (GridBagLayout) panel.getLayout();
			if (gridBagLayout.getConstraints(component).gridy == rowNumber) {
				panel.remove(component);
			}
		}
		updateView();

	}

	public void removeRow(int number) {
		if (rows.size() <= number) {
			return;
		}
		JComponent row = rows.get(number);
		removeAndUpdateRows(row, number);
	}

	private void addElementsToRow(int rowNumber, JComponent... elements) {
		addElementsToRow(rows.get(rowNumber), elements);
	}

	private void addElementsToRow(JComponent row, JComponent... elements) {

		GridBagConstraints c;
		if (row instanceof JPanel) {
			addOrRemoveFillingFromLastElementInRow(false, row);
			c = ((GridBagLayout) row.getLayout()).getConstraints(
					row.getComponent(row.getComponentCount() - 1));
			c.weightx = 1;
			for (JComponent element : elements) {
				row.add(element, c);
			}
		}
		else {
			MainPanel mainPanel = new MainPanel(
					new PanelConfiguration().setNotOpaque());
			mainPanel.setSkipInsetsForExtremeEdges(true);
			JPanel panel = mainPanel.getPanel();
			replacePanel(row, panel);
			panel.add(row);
			List<JComponent> components = new ArrayList<>();
			components.add(row);
			components.addAll(Arrays.asList(elements));
			addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST,
					components.toArray(new JComponent[] {})));
		}
		updateView();
	}

	private void addOrRemoveFillingFromLastElementInRow(boolean addFilling,
			JComponent row) {
		GridBagConstraints c = ((GridBagLayout) row.getLayout()).getConstraints(
				row.getComponent(row.getComponentCount() - 1));
		Component comp = row.getComponent(row.getComponentCount() - 1);
		row.remove(comp);
		c.weightx = addFilling ? 1 : 0;
		row.add(comp, c);
	}

	public void removeRowsInclusive(int start, int end) {
		for (int i = start; i <= end; i++) {
			JComponent row = rows.get(start);
			panel.remove(row);
			rows.remove(row);
		}
		movePanels(Direction.BACKWARD, start, end - start + 1);
		if (shouldPutRowsHighestAsPossible) {
			giveLastRowTheRestOfSpace();
		}
		updateView();

	}

	private void removeRow(JComponent row) {
		removeAndUpdateRows(row, rows.indexOf(row));
	}

	private void removeAndUpdateRows(JComponent row, int lastRowToUpdate) {
		movePanels(Direction.BACKWARD, lastRowToUpdate, 1);
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
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		GridBagConstraints cd = elementsShifter.getConstraintsForRow(
				rows.get(lastRow));
		cd.weighty = 1;
		g.setConstraints(rows.get(lastRow), cd);
	}

	public void replacePanel(JComponent oldPanel, JComponent newPanel) {
		Map<JComponent, GridBagConstraints> constraintsOfPanels = new LinkedHashMap<>();
		rows.forEach(row -> {
			if (row == oldPanel) {
				constraintsOfPanels.put(newPanel,
						getConstraintsForComponent(oldPanel));
			}
			else {
				constraintsOfPanels.put(row, getConstraintsForComponent(row));
			}

		});
		int i = rows.indexOf(oldPanel);
		rows.set(i, newPanel);
		panel.removeAll();
		panel.getLayout();
		constraintsOfPanels.entrySet()
						   .forEach(entry -> panel.add(entry.getKey(),
								   entry.getValue()));

	}

	public void insertElementInPlaceOfElement(JComponent elementToAdd,
			JComponent elementToReplace) {

		boolean first = true;
		for (JComponent row : rows) {
			if (!(row instanceof JPanel)) {
				continue;
			}
			GridBagLayout layout = (GridBagLayout) row.getLayout();
			GridBagConstraints firstElementToMoveConstraints = layout.getConstraints(
					elementToReplace);
			JPanel rowPanel = (JPanel) row;
			for (Component c : rowPanel.getComponents()) {
				GridBagConstraints currentConstraints = layout.getConstraints(
						c);
				if (elementToAdd instanceof JTextComponent
						&& currentConstraints.gridy
						== firstElementToMoveConstraints.gridy
						&& c instanceof JTextComponent && first) {
					panelTextInputsManager.manageTextInput(elementToAdd,
							(JTextComponent) c);
					first = false;
				}
				if (currentConstraints.gridx
						== firstElementToMoveConstraints.gridx
						&& currentConstraints.gridy
						== firstElementToMoveConstraints.gridy) {
					row.remove(c);
					row.add(elementToAdd, currentConstraints);
					currentConstraints.gridx = currentConstraints.gridx + 1;
					row.add(c, currentConstraints);
				}
				else if (currentConstraints.gridy
						== firstElementToMoveConstraints.gridy
						&& currentConstraints.gridx
						> firstElementToMoveConstraints.gridx) {
					currentConstraints.gridx = currentConstraints.gridx + 1;
					row.remove(c);
					row.add(c, currentConstraints);
				}
			}
		}

		updateView();
	}

	public void insertRow(int number, AbstractSimpleRow row) {

		RowType rowType = movePanels(Direction.FORWARD, number, 1);
		if (rowType == null || rowType == RowType.ROW_BELOW_ROW) {
			addRow(row, number);
		}
		else {
			addRowAsColumn(row, number);
		}

	}

	public void removeRowWithElements(Component... elements) {
		int rowNumber = getIndexOfRowContainingElements(elements);
		removeAndUpdateRows(rows.get(rowNumber), rowNumber);
	}

	private void removeElementsFromRow(int rowNumber, Component... elements) {
		JComponent row = rows.get(rowNumber);
		for (Component c : elements) {
			row.remove(c);
		}
		addOrRemoveFillingFromLastElementInRow(true, row);
		updateView();

	}

	public int getIndexOfRowContainingElements(Component... elements) {

		if (columnPanelCreator.isInitialized()) {
			return columnPanelCreator.getIndexOfRowContainingElements(elements);
		}
		for (int i = 0; i < rows.size(); i++) {
			if (doesPanelContainElements(rows.get(i), elements))
				return i;
		}
		return -1;
	}

	private boolean doesPanelContainElements(JComponent panel,
			Component[] elements) {
		List<Component> e = Arrays.asList(panel.getComponents());
		if (panel instanceof JPanel) {
			for (Component component : panel.getComponents()) {
				if (doesPanelContainElements((JComponent) component,
						elements)) {
					return true;
				}
			}
		}
		return elements.length == 1 && panel.equals(elements[0])
				|| e.containsAll(Arrays.asList(elements));
	}

	public void clear() {
		panel.removeAll();
		if (columnPanelCreator.isInitialized()) {
			columnPanelCreator.clear();
		}
		rows.clear();

	}

	public void changeEnabledStateOfLastElementInRow(int rowNumber,
			boolean enabled) {
		JComponent panel = rows.get(rowNumber);
		Component c = panel.getComponent(panel.getComponentCount() - 1);
		c.setEnabled(enabled);
	}

	public int getIndexOfPanel(JComponent panel) {
		return rows.indexOf(panel);
	}

	private GridBagConstraints getConstraintsForComponent(Component component) {
		GridBagLayout layout = (GridBagLayout) panel.getLayout();
		return layout.getConstraints(component);
	}

	public void addRowsOfElementsInColumn(ComplexRow complexRow) {
		for (AbstractSimpleRow abstractSimpleRow1 : complexRow.getAllRows()) {
			addElementsInColumn(abstractSimpleRow1);
		}
	}

	public void addElementsInColumn(AbstractSimpleRow abstractSimpleRow) {
		if (!columnPanelCreator.isInitialized()) {
			columnPanelCreator.initializePanel();
			addRow(SimpleRowBuilder.createRow(FillType.NONE,
					abstractSimpleRow.getAnchor(),
					columnPanelCreator.getPanel()));
		}
		addRowAsColumn(abstractSimpleRow);
	}

	private void addRowAsColumn(AbstractSimpleRow abstractSimpleRow) {
		columnPanelCreator.addElementsInColumn(abstractSimpleRow);
		rowNumberToTypeMap.put(rowNumberToTypeMap.lastKey() + 1,
				RowType.COLUMN_BELOW_COLUMN);
	}

	private void addRowAsColumn(AbstractSimpleRow abstractSimpleRow,
			int number) {
		columnPanelCreator.addElementsInColumn(abstractSimpleRow, number);
		rowNumberToTypeMap.put(rowNumberToTypeMap.lastKey() + 1,
				RowType.COLUMN_BELOW_COLUMN);
	}

	private RowType movePanels(Direction direction, int startIndex,
			int absoluteIncrementDecrementValue) {
		if (absoluteIncrementDecrementValue < 0) {
			throw new IllegalArgumentException(
					"Increment/decrement value should be positive");
		}
		RowType rowType = rowNumberToTypeMap.get(startIndex);
		if (rowType == null) {
			return null;
		}
		if (rowType.equals(RowType.COLUMN_BELOW_COLUMN)) {
			columnPanelCreator.shiftElements(direction, startIndex,
					absoluteIncrementDecrementValue);
		}
		else if (rowType.equals(RowType.ROW_BELOW_ROW)) {
			elementsShifter.shiftElements(direction, startIndex,
					absoluteIncrementDecrementValue);
		}
		return rowType;

	}

	public void toggleEnabledState() {
		boolean enableAllInputs = displayMode.equals(PanelDisplayMode.VIEW);
		toggleDisplayMode(enableAllInputs);
		for (JComponent row : rows) {
			if (row instanceof JPanel) {
				enableOrDisableAllElementsInPanel((JPanel) row,
						enableAllInputs);
			}
		}
	}

	public void setPanelColor(int rowNumber, Color color) {
		JComponent panel = rows.get(rowNumber);
		panel.setOpaque(true);
		panel.setBackground(color);
	}

	public int getNumberOfRows() {
		return Math.max(rows.size(), numberOfRows);
	}

	public List<JComponent> getRows() {
		return rows;
	}

	private void toggleDisplayMode(boolean enable) {
		if (enable) {
			displayMode = PanelDisplayMode.EDIT;
		}
		else {
			displayMode = PanelDisplayMode.VIEW;
		}
	}

	private void enableOrDisableAllElementsInPanel(JPanel panel,
			boolean enableInputs) {
		for (Component element : panel.getComponents()) {
			if (element instanceof JPanel) {
				enableOrDisableAllElementsInPanel((JPanel) element,
						enableInputs);
			}
			else {
				element.setEnabled(enableInputs);
			}
		}
	}

	public void updateView() {
		panel.revalidate();
		panel.repaint();

	}

	public void addSwitchBetweenInputsFailedListener(
			SwitchBetweenInputsFailListener listener) {
		panelTextInputsManager.addSwitchBetweenInputsFailedListener(listener);
	}

	public JTextComponent getSelectedInput() {
		return panelTextInputsManager.getSelectedInput();
	}

	public boolean hasSelectedInput() {
		return panelTextInputsManager.hasSelectedInput();
	}

	public int getSelectedInputIndex() {
		return panelTextInputsManager.getSelectedInputIndex();
	}

	public void selectInputInColumn(int columnNumber) {
		panelTextInputsManager.selectInputInColumn(columnNumber);
	}

	public void addManager(
			ListInputsSelectionManager listInputsSelectionManager) {
		panelTextInputsManager.addManager(listInputsSelectionManager);
	}

	public void clearSelectedInput() {
		panelTextInputsManager.clearSelectedInput();
	}

	public void selectNextInputInSameRow() {
		panelTextInputsManager.selectNextInputInSameRow();
	}

	public void selectPreviousInputInSameRow() {
		panelTextInputsManager.selectPreviousInputInSameRow();
	}

	public void setRowColor(Color color) {
		rowColor = color;
	}

	public void setGapsBetweenRowsTo0() {
		gapBetweenRows = 0;
		columnPanelCreator.setGapBetweenRows(0);
	}

	public void setRowsBorder(Border border) {
		rowBorder = border;
	}

	public void setWrappingPanelBorder(Border border) {
		panel.setBorder(border);
	}

	public void setSkipInsetsForExtremeEdges(
			boolean skipInsetsForExtremeEdges) {
		this.skipInsetsForExtremeEdges = skipInsetsForExtremeEdges;
	}

	public void setPadding(int padding) {
		this.paddingLeft = padding;
		this.paddingBottom = padding;
		this.paddingTop = padding;
		this.paddingRight = padding;
		columnPanelCreator.setPadding(paddingTop, paddingRight, paddingBottom,
				paddingLeft);
	}

	public void setOpaqueRows(boolean opaque) {
		opaqueRows = opaque;
	}

	public JPanel getPanel() {
		return panel;
	}

	public void setBackgroundColor(Color c) {
		//TODO check why column panel creator is opaque false
		getPanel().setOpaque(c != null);
		getPanel().setBackground(c);
	}

	public void clearPanelColor(JComponent panel) {
		panel.setOpaque(false);
	}

}
