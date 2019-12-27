package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelRows {

	private List<JComponent> uiComponents;
	private FillType fillType;
	private List<JComponent> elementsToFill;
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
		elementsToFill = new ArrayList<>();
	}

	public PanelRows fillElement(FillType fillType,
			JComponent... elementsToFill) {
		if (!uiComponents.containsAll(Arrays.asList(elementsToFill))) {
			throw new IllegalArgumentException(
					"Some of the elements to fill are not on the"
							+ " list of elements to add to row.");
		}
		this.fillType = fillType;
		this.elementsToFill = Arrays.asList(elementsToFill);
		if (shouldFillAnyElementVertically()) {
			panelRowsData.setHasAnyRowWithElementFilledVertically();

		}
		return this;
	}

	public boolean shouldFillAnyElementVertically() {
		return fillType.equals(FillType.VERTICAL) || fillType.equals(
				FillType.BOTH);
	}

	public boolean shouldFillAnyElementHorizontally() {
		return fillType.equals(FillType.HORIZONTAL) || fillType.equals(
				FillType.BOTH);
	}

	public FillType getFillType() {
		return fillType;
	}

	public boolean shouldFillElement(JComponent element) {
		return elementsToFill.contains(element);
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

	public boolean shouldKeepColumnSizeWithRowAboveOrBelow() {
		return keepColumnSizeWithRowAboveOrBelow;
	}

	public boolean isLast() {
		return panelRowsData.isLast(this);
	}

	public boolean existsOtherRowWithElementFilledVertically() {
		return panelRowsData.hasAnyRowWithElementFilledVertically();
	}

}
