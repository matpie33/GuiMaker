package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelBuilder {

	private FillType rowFillType;
	private boolean requiresNewRow = true;
	private int highestNumberOfColumnsInPanel;
	private boolean isLast;
	private int gridY;
	private List<UIElementBuilder> elementsBuilders = new ArrayList<>();
	private boolean shouldTakeAllSpaceToBottom;
	private boolean hasElementFilledHorizontallyToTheRightEdgeOfPanel;

	public boolean shouldTakeAllSpaceToBottom() {
		return shouldTakeAllSpaceToBottom;
	}

	public void setShouldTakeAllSpaceToBottom(
			boolean shouldTakeAllSpaceToBottom) {
		this.shouldTakeAllSpaceToBottom = shouldTakeAllSpaceToBottom;
	}

	public void addElementBuilder(UIElementBuilder uiElementsBuilder) {
		elementsBuilders.add(uiElementsBuilder);
	}

	public FillType getRowFillType() {
		return rowFillType;
	}

	public void setRowFillType(FillType rowFillType) {
		this.rowFillType = rowFillType;
	}

	public boolean requiresNewRow() {
		return requiresNewRow;
	}

	public void setRequiresNewRow(boolean requiresNewRow) {
		this.requiresNewRow = requiresNewRow;
	}

	public int getHighestNumberOfColumnsInPanel() {
		return highestNumberOfColumnsInPanel;
	}

	public void setHighestNumberOfColumnsInPanel(
			int numberOfColumnsInCurrentRow) {
		if (numberOfColumnsInCurrentRow > this.highestNumberOfColumnsInPanel){
			this.highestNumberOfColumnsInPanel = numberOfColumnsInCurrentRow;
		}
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean last) {
		isLast = last;
	}

	public int getRowIndex() {
		return gridY;
	}

	public void setRowIndex(int rowNumber) {
		this.gridY = rowNumber;
	}

	public List<UIElementBuilder> getElementsBuilders() {
		return elementsBuilders;
	}

	public boolean hasElementFilledHorizontallyToTheRightEdgeOfPanel() {
		return hasElementFilledHorizontallyToTheRightEdgeOfPanel;
	}

	public void setHasElementFilledHorizontallyToTheRightEdgeOfPanel(
			boolean hasElementFilledHorizontallyToTheRightEdgeOfPanel) {
		this.hasElementFilledHorizontallyToTheRightEdgeOfPanel = hasElementFilledHorizontallyToTheRightEdgeOfPanel;
	}
}
