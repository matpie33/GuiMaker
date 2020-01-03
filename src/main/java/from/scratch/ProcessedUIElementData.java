package from.scratch;

import com.guimaker.enums.Anchor;

import javax.swing.*;

public class ProcessedUIElementData {

	private FillType fillType;
	private int insetLeft;
	private int insetRight;
	private int insetTop;
	private int insetBottom;
	private int columnIndex;
	private int rowIndex;
	private JComponent uiElement;
	private boolean useAllAvailableSpace;
	private Anchor anchor;

	public Anchor getAnchor() {
		return anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
	}

	public JComponent getUiElement() {
		return uiElement;
	}

	public void setUiElement(JComponent uiElement) {
		this.uiElement = uiElement;
	}

	public FillType getFillType() {
		return fillType;
	}

	public void setFillType(FillType fillType, boolean useAllAvailableSpace) {
		this.fillType = fillType;
		this.useAllAvailableSpace = useAllAvailableSpace;
	}

	public boolean shouldUseAllAvailableSpace() {
		return useAllAvailableSpace;
	}

	public int getInsetLeft() {
		return insetLeft;
	}

	public void setInsetLeft(int insetLeft) {
		this.insetLeft = insetLeft;
	}

	public int getInsetRight() {
		return insetRight;
	}

	public void setInsetRight(int insetRight) {
		this.insetRight = insetRight;
	}

	public int getInsetTop() {
		return insetTop;
	}

	public void setInsetTop(int insetTop) {
		this.insetTop = insetTop;
	}

	public int getInsetBottom() {
		return insetBottom;
	}

	public void setInsetBottom(int insetBottom) {
		this.insetBottom = insetBottom;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
}
