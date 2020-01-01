package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelRow {

	private List<JComponent> uiElements;
	private FillType elementsFillType;
	private FillType fillTypeWithinColumnOrRowSize;
	private List<JComponent> elementsToFill;
	private List<JComponent> elementsToFillWithinColumnOrRowSize;
	private boolean keepColumnSizeWithRowBelow;
	private PanelRow previousRow;
	private PanelRow nextRow;
	private FillType rowFillType;

	public PanelRow(JComponent... uiElements) {
		this.uiElements = Arrays.asList(uiElements);
		keepColumnSizeWithRowBelow = false;
		elementsFillType = FillType.NONE;
		rowFillType = FillType.NONE;
		elementsToFill = new ArrayList<>();
		elementsToFillWithinColumnOrRowSize = new ArrayList<>();
	}

	public FillType getFillTypeWithinColumnOrRowSize() {
		return fillTypeWithinColumnOrRowSize;
	}

	public PanelRow fillElementWithinColumnOrRowSize(FillType fillType,
			JComponent... elements) {
		elementsToFillWithinColumnOrRowSize = Arrays.asList(elements);
		fillTypeWithinColumnOrRowSize = fillType;
		return this;
	}

	public List<JComponent> getElementsToFillWithinColumnOrRowSize() {
		return elementsToFillWithinColumnOrRowSize;
	}

	public PanelRow fillElement(FillType fillType,
			JComponent... elementsToFill) {
		this.elementsFillType = fillType;
		this.elementsToFill = Arrays.asList(elementsToFill);
		return this;
	}

	public PanelRow fillThisRow(FillType fillType) {
		rowFillType = fillType;
		return this;
	}

	public FillType getElementsFillType() {
		return elementsFillType;
	}

	public List<JComponent> getElementsToFill() {
		return elementsToFill;
	}

	public List<JComponent> getUIElements() {
		return uiElements;
	}

	public PanelRow getPreviousRow() {
		return previousRow;
	}

	private void setPreviousRow(PanelRow previousRow) {
		this.previousRow = previousRow;
	}

	public PanelRow nextRow(JComponent... elements) {
		nextRow = new PanelRow(elements);
		nextRow.setPreviousRow(this);
		return nextRow;
	}

	public PanelRow nextRowKeepingColumnSize(JComponent... elements) {
		keepColumnSizeWithRowBelow = true;
		nextRow = new PanelRow(elements);
		nextRow.setPreviousRow(this);
		return nextRow;
	}

	public boolean shouldKeepColumnSizeWithRowBelow() {
		return keepColumnSizeWithRowBelow;
	}

	public boolean isLastRow() {
		return nextRow == null;
	}

	public PanelRow getNextRow() {
		return nextRow;
	}

	public FillType getRowFillType() {
		return rowFillType;
	}

	public boolean isFirstRow() {
		return getPreviousRow() == null;
	}
}