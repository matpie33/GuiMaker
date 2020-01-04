package from.scratch;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PanelUpdater {

	private List<ProcessedPanelData> processedPanelsData;
	private JPanel panel;

	public PanelUpdater(JPanel panel) {
		processedPanelsData = new ArrayList<>();
		this.panel = panel;
	}

	public void removeElement(JComponent uiElement) {
		for (ProcessedPanelData processedPanelData : processedPanelsData) {
			Optional<ProcessedUIElementData> elementToRemoveOptionalData = findDataForElement(
					uiElement, processedPanelData);
			if (elementToRemoveOptionalData.isPresent()) {
				JPanel panelContainingElement = getPanelContainingElement(
						processedPanelData);
				ProcessedUIElementData elementToRemoveData = elementToRemoveOptionalData.get();
				removeElement(uiElement, processedPanelData,
						elementToRemoveData, panelContainingElement);
				shiftElementsLeftFromPanel(processedPanelData,
						elementToRemoveData, panelContainingElement);
			}
		}
	}

	private Optional<ProcessedUIElementData> findDataForElement(
			JComponent uiElement, ProcessedPanelData processedPanelData) {
		return processedPanelData.getProcessedUIElementsData()
								 .stream()
								 .filter(uiData -> uiData.getUiElement()
										 == uiElement)
								 .findFirst();
	}

	private void shiftElementsLeftFromPanel(ProcessedPanelData panelData,
			ProcessedUIElementData uiElementData,
			JPanel panelContainingElement) {
		GridBagLayout layout = (GridBagLayout) panelContainingElement.getLayout();
		int columnIndex = uiElementData.getColumnIndex();
		panelData.getProcessedUIElementsData()
				 .stream()
				 .filter(uiElement -> uiElement.getColumnIndex() > columnIndex)
				 .filter(uiElement -> uiElement.getRowIndex()
						 == uiElementData.getRowIndex())
				 .forEach(uiElement -> {
					 shiftElement1PlaceLeft(layout, uiElement);
				 });
	}

	private void shiftElement1PlaceLeft(GridBagLayout layout,
			ProcessedUIElementData uiElement) {
		uiElement.setColumnIndex(uiElement.getColumnIndex() - 1);
		GridBagConstraints constraints = layout.getConstraints(
				uiElement.getUiElement());
		constraints.gridx = constraints.gridx - 1;
		layout.setConstraints(uiElement.getUiElement(), constraints);
	}

	private void removeElement(JComponent element, ProcessedPanelData panelData,
			ProcessedUIElementData uiElementData,
			JPanel panelContainingElement) {
		panelData.getProcessedUIElementsData()
				 .remove(uiElementData);
		panelContainingElement.remove(element);
	}

	private JPanel getPanelContainingElement(
			ProcessedPanelData processedPanelData) {
		int rowIndex = processedPanelData.getRowIndex();
		return (JPanel) this.panel.getComponent(rowIndex);
	}

	public void addProcessedPanelData(List<ProcessedPanelData> panelsToAdd) {
		ProcessedPanelData firstPanelToAdd = panelsToAdd.get(0);

		if (firstPanelToAdd.shouldAddElementsToPreviousPanel()) {
			ProcessedPanelData lastPanel = processedPanelsData.get(
					processedPanelsData.size() - 1);
			lastPanel.getProcessedUIElementsData()
					 .addAll(firstPanelToAdd.getProcessedUIElementsData());
			lastPanel.setFillType(firstPanelToAdd.getFillType());
			panelsToAdd.remove(firstPanelToAdd);

		}
		this.processedPanelsData.addAll(panelsToAdd);
	}

	public void removePlaceholderPanel() {
		if (panel.getComponentCount() > 0) {
			panel.remove(panel.getComponent(panel.getComponentCount() - 1));
			processedPanelsData.remove(processedPanelsData.get(
					processedPanelsData.size() - 1));
		}
	}
}
