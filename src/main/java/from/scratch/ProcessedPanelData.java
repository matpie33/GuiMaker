package from.scratch;

import java.util.ArrayList;
import java.util.List;

public class ProcessedPanelData {

	private boolean isLast;
	private int gridY;
	private List<ProcessedUIElementData> processedUIElementsData = new ArrayList<>();
	private FillType fillType;

	public FillType getFillType() {
		return fillType;
	}

	public void setFillType(FillType fillType) {
		if (!FillType.BOTH.equals(this.fillType)) {
			this.fillType = fillType;
		}
	}

	public void addProcessedUIElementData(
			ProcessedUIElementData processedUIElementData) {
		this.processedUIElementsData.add(processedUIElementData);
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

	public List<ProcessedUIElementData> getProcessedUIElementsData() {
		return processedUIElementsData;
	}

}
