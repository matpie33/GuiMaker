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
	private boolean keepColumnSizeWithRowAbove;

	public PanelRows(JComponent... uiComponents) {
		this(Arrays.asList(uiComponents), new ArrayList<>());
	}

	private PanelRows(List<JComponent> components, List<PanelRows> panelRows) {
		this.panelRows = panelRows;
		this.uiComponents = components;
		this.panelRows.add(this);
		keepColumnSizeWithRowAbove = false;
		fillType = FillType.NONE;
	}

	public PanelRows fillElement(FillType fillType, JComponent elementToFill) {
		if (!uiComponents.contains(elementToFill)){
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

	public boolean shouldFillElement (JComponent element){
		return element == elementToFill;
	}

	public List<JComponent> getUiComponents() {
		return uiComponents;
	}

	public PanelRows nextRow(JComponent... elements) {
		return new PanelRows(Arrays.asList(elements), panelRows);
	}

	public List<PanelRows> getRows() {
		return panelRows;
	}

	public PanelRows nextRowKeepingColumnSize(JComponent... elements) {
		return new PanelRows(Arrays.asList(elements),
				this.panelRows).keepColumnSizeWithRowAbove();
	}

	private PanelRows keepColumnSizeWithRowAbove() {
		this.keepColumnSizeWithRowAbove = true;
		return this;
	}

	public boolean shouldKeepColumnSizeWithRowAbove() {
		return keepColumnSizeWithRowAbove;
	}
}
