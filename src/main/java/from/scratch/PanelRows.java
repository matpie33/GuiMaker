package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelRows {

	private List<JComponent> uiComponents;
	private FillType fillType;
	private List<PanelRows> panelRows;

	public PanelRows(JComponent... uiComponents) {
		this(Arrays.asList(uiComponents), new ArrayList<>());
	}

	private PanelRows(List<JComponent> components, List<PanelRows> panelRows) {
		this.panelRows = panelRows;
		this.uiComponents = components;
		this.panelRows.add(this);
	}

	public PanelRows withFillType(FillType fillType) {
		this.fillType = fillType;
		return this;
	}

	public FillType getFillType() {
		return fillType;
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
}
