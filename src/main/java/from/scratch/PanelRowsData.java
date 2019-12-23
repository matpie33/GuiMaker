package from.scratch;

import java.util.ArrayList;
import java.util.List;

public class PanelRowsData {

	private int highestNumberOfColumns;
	private List<PanelRows> panelRows;

	public PanelRowsData() {
		panelRows = new ArrayList<>();
	}

	public int getHighestNumberOfColumns() {
		return highestNumberOfColumns;
	}

	public void addRow(PanelRows panelRows) {
		this.panelRows.add(panelRows);
	}

	public List<PanelRows> getRows() {
		return panelRows;
	}

	public void checkHighestNumberOfColumns(int size) {
		highestNumberOfColumns = Math.max(highestNumberOfColumns, size);
	}

	public boolean isLast(PanelRows panelRows) {
		return getRows().get(getRows().size() - 1) == panelRows;
	}
}
