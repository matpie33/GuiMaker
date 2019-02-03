package com.guimaker.panels;

import com.guimaker.enums.*;
import com.guimaker.inputSelection.InputSelectionManager;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class MainPanel {

	private final int paddingDefaultValue = 4;

	private List<JComponent> rows;
	private TreeMap<Integer, RowType> rowNumberToTypeMap = new TreeMap<>();
	private JPanel panel;
	private int gapBetweenRows = 4;

	private int paddingRight = paddingDefaultValue;
	private int paddingLeft = paddingDefaultValue;
	private int paddingTop = paddingDefaultValue;
	private int paddingBottom = paddingDefaultValue;

	private boolean shouldPutRowsHighestAsPossible;
	private Border borderToUse;
	private Color rowColor;
	private int numberOfRows;
	private Color originalBackgroundColor;
	private boolean skipInsetsForExtremeEdges = false;
	private InputSelectionManager inputSelectionManager;
	private PanelDisplayMode displayMode;
	private ColumnPanelCreator columnPanelCreator;
	private boolean rowsHaveNonZeroWeightsY;
	private static Color defaultColor;
	private boolean opaque = true;
	private boolean opaqueRows = true;
	private ElementsShifter elementsShifter;

	public static void setDefaultColor(Color defaultColor) {
		MainPanel.defaultColor = defaultColor;
	}

	public void setPaddingHorizontal(int padding) {
		paddingLeft = padding;
		paddingRight = padding;
	}

	public void setGapsBetweenRowsTo0() {
		gapBetweenRows = 0;
		columnPanelCreator.setGapBetweenRows(0);
	}

	public void setGapsBetweenRowsTo(int gap) {
		gapBetweenRows = gap;
	}

	public void setGapsRightSideBetweenColumnsTo(int gap) {
		paddingRight = gap;
	}

	public void setRightBorder() {
		borderToUse = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
	}

	public void setRowsBorder(Border border) {
		borderToUse = border;
	}

	public void setWrappingPanelBorder(Border border) {
		panel.setBorder(border);
	}

	public void setRowColor(Color color) {
		rowColor = color;
	}

	public MainPanel() {
		this(new PanelConfiguration());
	}

	//TODO "do not create many top level containers (JPanel?), instead reuse existing
	// by calling removAll" - try to optimize drawing

	public MainPanel(PanelConfiguration panelConfiguration) {
		opaque = panelConfiguration.isOpaque();
		columnPanelCreator = new ColumnPanelCreator(
				panelConfiguration.getPanelDisplayMode(), gapBetweenRows);
		columnPanelCreator.setPadding(paddingTop, paddingRight, paddingBottom,
				paddingLeft);

		numberOfRows = 0;
		originalBackgroundColor = panelConfiguration.getColorToUse();
		shouldPutRowsHighestAsPossible = panelConfiguration.shouldPutRowsAsHighestAsPossible();
		if (panelConfiguration.isScrollHorizontally()) {
			panel = new JPanel();
		}
		else {
			panel = new HorizontallyNonscrollablePanel();
		}
		elementsShifter = new ElementsShifter(panel,
				shouldPutRowsHighestAsPossible);

		if (panelConfiguration.getColorToUse() == null
				&& panelConfiguration.isOpaque()) {
			panel.setBackground(defaultColor);
		}
		else if (panelConfiguration.getColorToUse() != null) {
			panel.setBackground(panelConfiguration.getColorToUse());
		}
		else if (!panelConfiguration.isOpaque()) {
			panel.setOpaque(false);
		}

		panel.setLayout(new GridBagLayout());
		rows = new LinkedList<>();
		displayMode = panelConfiguration.getPanelDisplayMode();
		inputSelectionManager = new InputSelectionManager(displayMode);

	}

	public void setGapsBetweenColumns(int gapBetweenColumns) {
		columnPanelCreator.setGapsBetweenColumns(gapBetweenColumns);
	}

	public void addSwitchBetweenInputsFailedListener(
			SwitchBetweenInputsFailListener listener) {
		inputSelectionManager.addSwitchBetweenInputsFailListener(listener);
	}

	public void setSkipInsetsForExtremeEdges(
			boolean skipInsetsForExtremeEdges) {
		this.skipInsetsForExtremeEdges = skipInsetsForExtremeEdges;
	}

	public void setOriginalBackgroundColor() {
		setBackgroundColor(originalBackgroundColor);
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

	private void addElement(int row, int column, Container container,
			JComponent element) {
		GridBagConstraints constraints = initializeGridBagConstraints();
		constraints.gridx = column;
		constraints.gridy = row;
		container.add(element, constraints);

	}

	public JComponent addRows(ComplexRow complexRow) {
		JComponent panel = null;
		for (AbstractSimpleRow row : complexRow.getAllRows()) {
			panel = addRow(row, rows.size());
		}
		return panel;
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
		if (panel instanceof JPanel && row.isBorderEnabled() && (
				borderToUse != null || row.getBorder() != null)) {
			panel.setBorder(
					borderToUse != null ? borderToUse : row.getBorder());
		}
		if (panel instanceof JPanel && (rowColor != null
				|| row.getColor() != null)) {
			panel.setBackground(
					row.getColor() != null ? row.getColor() : rowColor);
			panel.setOpaque(opaque && row.isOpaque());
		}
		if (!opaqueRows) {
			panel.setOpaque(false);
		}
		createConstraintsAndAdd(panel, row, rowNumber);
		return panel;
	}

	private Map<JComponent, Integer> mapComponentToFilling(
			AbstractSimpleRow row) {
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
			Map<JComponent, Integer> componentsFilling, AbstractSimpleRow row) {
		if (components.length == 1
				&& !(components[0] instanceof JTextComponent)) {
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
			if (compo == null) {
				continue;
			}
			boolean isTextInput = manageTextInput(compo,
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
				if (i == components.length - 1) {
					gbc.insets.right = 0;
				}
				else if (i == 0) {
					gbc.insets.left = 0;
				}
			}

			p.add(compo, gbc);
			gbc.gridx = gbc.gridx + 1;
			i++;
		}
		if (!row.isWrapWithPanel() && components.length == 1) {
			return components[0];
		}

		return p;
	}

	private boolean manageTextInput(JComponent compo,
			JTextComponent firstTextComponentInRow) {

		if (compo instanceof JTextComponent) {
			JTextComponent input = (JTextComponent) compo;
			inputSelectionManager.addInput(input, firstTextComponentInRow);
			if (displayMode.equals(PanelDisplayMode.EDIT)) {
				compo.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						inputSelectionManager.selectInput(input);
					}

					@Override
					public void focusLost(FocusEvent e) {
						inputSelectionManager.deselectInput(input);
					}
				});
			}
			else {
				compo.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						inputSelectionManager.toggleSelection(input);
					}
				});
			}

			return true;
		}
		return false;

	}

	private GridBagConstraints initializeGridBagConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(paddingTop, paddingLeft, paddingBottom,
				paddingRight);
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
		if (!skipInsetsForExtremeEdges) {
			c.insets = new Insets(a, a, a, a);
		}
		else {
			c.insets = new Insets(a, 0, a, 0);
		}

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
		removeAndUpdateRows(row, number, true);
	}

	public void addElementsToLastRow(JComponent... elements) {
		addElementsToRow(rows.size() - 1, elements);
	}

	public void addElementsToRow(int rowNumber, JComponent... elements) {
		addElementsToRow(rows.get(rowNumber), elements);
	}

	public void addElementsToRow(JComponent row, JComponent... elements) {

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
			mainPanel.addRow(
					SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST,
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
		removeAndUpdateRows(row, rows.indexOf(row), true);
	}

	private void removeAndUpdateRows(JComponent row, int lastRowToUpdate,
			boolean updateView) {
		movePanels(Direction.BACKWARD, lastRowToUpdate, 1);
		panel.remove(row);
		rows.remove(row);
		if (shouldPutRowsHighestAsPossible) {
			giveLastRowTheRestOfSpace();
		}
		if (updateView) {
			updateView();
		}

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
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		GridBagConstraints cd = elementsShifter.getConstraintsForRow(
				rows.get(lastRow));
		cd.weighty = 1;
		g.setConstraints(rows.get(lastRow), cd);
	}

	public JTextComponent getSelectedInput() {
		return inputSelectionManager.getSelectedInput();
	}

	public boolean hasSelectedInput() {
		return inputSelectionManager.hasSelectedInput();
	}

	public int getSelectedInputIndex() {
		return inputSelectionManager.getSelectedInputIndex();
	}

	public void selectInputInColumn(int columnNumber) {
		inputSelectionManager.selectInputInColumn(columnNumber);
	}

	public void addManager(
			ListInputsSelectionManager listInputsSelectionManager) {
		inputSelectionManager.addManager(listInputsSelectionManager);
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

	public void clearSelectedInput() {
		inputSelectionManager.deselectCurrentInput();
	}

	public void setPadding(int padding) {
		this.paddingLeft = padding;
		this.paddingBottom = padding;
		this.paddingTop = padding;
		this.paddingRight = padding;
		columnPanelCreator.setPadding(paddingTop, paddingRight, paddingBottom,
				paddingLeft);
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

	public void setOpaqueRows(boolean opaque) {
		opaqueRows = opaque;
	}

	private RowType movePanels(Direction direction, int startIndex,
			int absoluteIncrementDecrementValue) {
		if (absoluteIncrementDecrementValue < 0) {
			throw new IllegalArgumentException(
					"Increment/decrement value should be positive");
		}
		RowType rowType = rowNumberToTypeMap.get(startIndex);
		if (rowType == null) {
			return rowType;
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

	public void insertElementInPlaceOfElement(JComponent elementToAdd,
			JComponent elementToReplace) {

		boolean first = true;
		for (JComponent row : rows) {
			if (row instanceof JPanel == false) {
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
					manageTextInput(elementToAdd, (JTextComponent) c);
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

	public int getNumberOfRows() {
		return Math.max(rows.size(), numberOfRows);
	}

	public List<JComponent> getRows() {
		return rows;
	}

	public void updateView() {
		getPanel().revalidate();
		getPanel().repaint();

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
		int rowNumber = getIndexOfRowContainingElements(elements);
		removeAndUpdateRows(rows.get(rowNumber), rowNumber, true);
	}

	public void removeElementsFromRow(int rowNumber, Component... elements) {
		JComponent row = rows.get(rowNumber);
		for (Component c : elements) {
			row.remove(c);
		}
		addOrRemoveFillingFromLastElementInRow(true, row);
		updateView();

	}

	public void removeLastElementFromRow(int rowNumber) {
		JComponent row = rows.get(rowNumber);
		removeElementsFromRow(rowNumber,
				row.getComponent(row.getComponentCount() - 1));
	}

	public int getIndexOfRowContainingElements(
			List<? extends Component> elements) {
		return getIndexOfRowContainingElements(elements.toArray(new Component
				[] {}));
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
		if (panel instanceof JPanel){
			for (Component component : panel.getComponents()) {
				if (doesPanelContainElements((JComponent)component, elements)){
					return true;
				}
			}
		}
		if (elements.length == 1 && panel.equals(elements[0])) {
			return true;
		}
		if (e.containsAll(Arrays.asList(elements))) {
			return true;
		}
		return false;
	}

	public void clear() {
		panel.removeAll();
		if (columnPanelCreator.isInitialized()) {
			columnPanelCreator.clear();
		}
		rows.clear();

	}

	public void setBackgroundColor(Color c) {
		//TODO check why column panel creator is opaque false
		getPanel().setOpaque(c != null);
		getPanel().setBackground(c);
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

	public void changeEnabledStateOfLastElementInRow(int rowNumber,
			boolean enabled) {
		JComponent panel = rows.get(rowNumber);
		Component c = panel.getComponent(panel.getComponentCount() - 1);
		c.setEnabled(enabled);
	}

	public void setPanelColor(int rowNumber, Color color) {
		JComponent panel = rows.get(rowNumber);
		panel.setOpaque(true);
		panel.setBackground(color);
	}

	public void clearPanelColor(JComponent panel) {
		panel.setOpaque(false);
	}

	public int getIndexOfPanel(JComponent panel) {
		return rows.indexOf(panel);
	}

	public void selectNextInputInSameRow() {
		inputSelectionManager.selectNextInputInSameRow();
	}

	public void selectPreviousInputInSameRow() {
		inputSelectionManager.selectPreviousInputInSameRow();
	}

	public GridBagConstraints getConstraintsForComponent(Component component) {
		GridBagLayout layout = (GridBagLayout) getPanel().getLayout();
		return layout.getConstraints(component);
	}

	public void setConstraintsForComponent(Component component,
			GridBagConstraints constraints) {
		GridBagLayout layout = (GridBagLayout) getPanel().getLayout();
		layout.setConstraints(component, constraints);
	}

}
