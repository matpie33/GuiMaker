package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelRows {

	private List<JComponent> uiComponents;
	private FillType fillType;
	private JComponent elementToFill;
	private List<PanelRows> panelRows;
	private boolean keepColumnSizeWithRowAboveOrBelow;
	private ColumnRowsData columnRowsData;
	private boolean isLast;

	public PanelRows(JComponent... uiComponents) {
		this(Arrays.asList(uiComponents), new ArrayList<>());
	}

	private PanelRows(List<JComponent> components, List<PanelRows> panelRows) {
		this.panelRows = panelRows;
		this.uiComponents = components;
		this.panelRows.add(this);
		keepColumnSizeWithRowAboveOrBelow = false;
		fillType = FillType.NONE;
		isLast = true;
	}

	public PanelRows fillElement(FillType fillType, JComponent elementToFill) {
		if (!uiComponents.contains(elementToFill)) {
			throw new IllegalArgumentException("Element to fill is not on the"
					+ " list of elements to add to row.");
		}
		this.fillType = fillType;
		this.elementToFill = elementToFill;
		return this;
	}

	public FillType getFillType() {
		return fillType;
	}

	public boolean shouldFillElement(JComponent element) {
		return element == elementToFill;
	}

	public List<JComponent> getUiComponents() {
		return uiComponents;
	}

	public PanelRows nextRow(JComponent... elements) {
		isLast= false;
		return new PanelRows(Arrays.asList(elements), panelRows);
	}

	public boolean isLast() {
		return isLast;
	}

	public List<PanelRows> getRows() {
		return panelRows;
	}

	public PanelRows nextRowKeepingColumnSize(JComponent... elements) {
		int higherNumberOfColumns = Math.max(getUiComponents().size(),
				elements.length);
		if (columnRowsData == null) {
			columnRowsData = new ColumnRowsData(higherNumberOfColumns);
		}
		else {
			columnRowsData.setHighestNumberOfColumns(
					Math.max(higherNumberOfColumns,
							columnRowsData.getHighestNumberOfColumns()));
		}
		keepColumnSizeWithRowAboveOrBelow = true;

		return new PanelRows(Arrays.asList(elements),
				this.panelRows).keepColumnSizeWithRowAboveOrBelow(
				columnRowsData);
	}

	public int getHighestNumberOfColumns() {
		return columnRowsData != null ?
				columnRowsData.getHighestNumberOfColumns() :
				0;
	}

	private PanelRows keepColumnSizeWithRowAboveOrBelow(
			ColumnRowsData columnRowsData) {
		this.keepColumnSizeWithRowAboveOrBelow = true;
		this.columnRowsData = columnRowsData;
		return this;
	}

	public boolean shouldKeepColumnSizeWithRowAbove() {
		return keepColumnSizeWithRowAboveOrBelow;
	}
}
