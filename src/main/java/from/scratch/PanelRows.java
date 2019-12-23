package from.scratch;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class PanelRows {

	private List<JComponent> uiComponents;
	private FillType fillType;
	private JComponent elementToFill;
	private boolean keepColumnSizeWithRowAboveOrBelow;
	private PanelRowsData panelRowsData;

	public PanelRows(JComponent... uiComponents) {
		this(Arrays.asList(uiComponents), new PanelRowsData());
	}

	private PanelRows(List<JComponent> components,
			PanelRowsData panelRowsData) {
		this.panelRowsData = panelRowsData;
		panelRowsData.addRow(this);
		panelRowsData.checkHighestNumberOfColumns(components.size());
		this.uiComponents = components;
		keepColumnSizeWithRowAboveOrBelow = false;
		fillType = FillType.NONE;
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
		return new PanelRows(Arrays.asList(elements), panelRowsData);
	}

	public List<PanelRows> getRows() {
		return panelRowsData.getRows();
	}

	public PanelRows nextRowKeepingColumnSize(JComponent... elements) {
		keepColumnSizeWithRowAboveOrBelow();

		return new PanelRows(Arrays.asList(elements),
				this.panelRowsData).keepColumnSizeWithRowAboveOrBelow();
	}

	public int getHighestNumberOfColumns() {
		return panelRowsData.getHighestNumberOfColumns();
	}

	private PanelRows keepColumnSizeWithRowAboveOrBelow() {
		this.keepColumnSizeWithRowAboveOrBelow = true;
		return this;
	}

	public boolean shouldKeepColumnSizeWithRowAbove() {
		return keepColumnSizeWithRowAboveOrBelow;
	}

	public boolean isLast() {
		return panelRowsData.isLast(this);
	}
}
