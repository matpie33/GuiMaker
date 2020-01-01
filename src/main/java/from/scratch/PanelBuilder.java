package from.scratch;

import java.util.ArrayList;
import java.util.List;

public class PanelBuilder {

	private boolean requiresNewRow = true;
	private boolean isLast;
	private int gridY;
	private List<UIElementBuilder> elementsBuilders = new ArrayList<>();
	private FillType fillType;
	private int numberOfRowsInPanel;

	public int getNumberOfRowsInPanel() {
		return numberOfRowsInPanel;
	}

	public void setNumberOfRowsInPanel(int numberOfRowsInPanel) {
		this.numberOfRowsInPanel = numberOfRowsInPanel;
	}

	public FillType getFillType() {
		return fillType;
	}

	public void setFillType(FillType fillType) {
		if (!FillType.BOTH.equals(this.fillType)) {
			this.fillType = fillType;
		}
	}

	public void addElementBuilder(UIElementBuilder uiElementsBuilder) {
		elementsBuilders.add(uiElementsBuilder);
	}

	public boolean requiresNewRow() {
		return requiresNewRow;
	}

	public void setRequiresNewRow(boolean requiresNewRow) {
		this.requiresNewRow = requiresNewRow;
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

}
