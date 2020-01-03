package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RowsPreprocessor {

	private static final int SPACE_BETWEEN_ELEMENTS_HORIZONTALLY = 4;
	private static final int SPACE_BETWEEN_ELEMENTS_VERTICALLY = 3;
	private ProcessedPanelData lastPanelPlaceholder;

	public List<ProcessedPanelData> preprocess(PanelRow panelRow) {

		List<ProcessedPanelData> processedPanelsData = new ArrayList<>();
		int rowIndex = lastPanelPlaceholder != null ?
				lastPanelPlaceholder.getRowIndex() :
				0;
		int indexOfRowInsideSamePanel = 0;
		boolean existsRowFilledVertically = false;
		ProcessedPanelData currentlyProcessedPanel = null;
		PanelRow currentRow = findFirstPanel(panelRow);
		while (currentRow != null) {
			checkCorrectElementsToFill(currentRow);

			if (currentlyProcessedPanel == null || rowRequiresNewPanel(
					currentRow)) {
				indexOfRowInsideSamePanel = 0;
				currentlyProcessedPanel = new ProcessedPanelData();
				processedPanelsData.add(currentlyProcessedPanel);
			}
			else {
				indexOfRowInsideSamePanel++;
			}
			createUIElementsData(currentRow, currentlyProcessedPanel,
					indexOfRowInsideSamePanel, rowIndex);
			setPanelData(rowIndex, currentlyProcessedPanel);
			existsRowFilledVertically =
					existsRowFilledVertically || containsVerticalFill(
							currentlyProcessedPanel.getFillType());
			currentRow = currentRow.getNextRow();
			if (currentRow == null || rowRequiresNewPanel(currentRow)) {
				rowIndex++;
			}

		}
		assert currentlyProcessedPanel != null;
		ProcessedPanelData placeHolderPanel = createEmptyPanelToTheBottomOfTheParentPanel(
				existsRowFilledVertically, rowIndex);
		processedPanelsData.add(placeHolderPanel);
		lastPanelPlaceholder = placeHolderPanel;
		return processedPanelsData;

	}

	private void checkCorrectElementsToFill(PanelRow currentRow) {
		if (!currentRow.getUIElements()
					   .containsAll(currentRow.getElementsToFill())) {
			throw new IllegalArgumentException(
					"Some of the elements to fill are not on the"
							+ " list of elements to add to row.");
		}
		if (!currentRow.getElementsToFillWithinColumnOrRowSize()
					   .isEmpty() && (currentRow.isFirstRow()
				|| !currentRow.getPreviousRow()
							  .shouldKeepColumnSizeWithRowBelow())) {
			throw new IllegalArgumentException("Wrong method used: fill "
					+ "elements based on column or row size from previous "
					+ "row, but previous row doesnt exist or does not keep "
					+ "column size.");
		}
	}

	private PanelRow findFirstPanel(PanelRow panelRow) {
		PanelRow current = panelRow;
		while (current.getPreviousRow() != null) {
			current = current.getPreviousRow();
		}
		return current;
	}

	private ProcessedPanelData createEmptyPanelToTheBottomOfTheParentPanel(
			boolean existsRowFilledVertically, int rowIndex) {
		ProcessedPanelData processedPanelData = new ProcessedPanelData();
		processedPanelData.setFillType(!existsRowFilledVertically ?
				FillType.BOTH :
				FillType.HORIZONTAL);
		processedPanelData.setRowIndex(rowIndex);
		processedPanelData.setLast(true);
		return processedPanelData;
	}

	private boolean containsVerticalFill(FillType fillType) {
		return fillType.equals(FillType.VERTICAL) || fillType.equals(
				FillType.BOTH);
	}

	private void createUIElementsData(PanelRow currentRow,
			ProcessedPanelData processedPanelData,
			int indexOfRowInsideSamePanel, int rowIndex) {
		List<JComponent> uiElements = currentRow.getUIElements();
		for (int indexOfElementInRow = 0;
			 indexOfElementInRow < uiElements.size(); indexOfElementInRow++) {
			JComponent uiElement = uiElements.get(indexOfElementInRow);
			processedPanelData.addProcessedUIElementData(
					createUIElement(uiElement, currentRow, indexOfElementInRow,
							indexOfRowInsideSamePanel, rowIndex));
		}
	}

	private ProcessedUIElementData createUIElement(JComponent uiElement,
			PanelRow currentRow, int columnIndex, int indexOfRowInsideSamePanel,
			int rowIndex) {
		ProcessedUIElementData processedUiElementData = new ProcessedUIElementData();
		processedUiElementData.setUiElement(uiElement);
		processedUiElementData.setColumnIndex(columnIndex);
		processedUiElementData.setRowIndex(indexOfRowInsideSamePanel);
		processedUiElementData.setInsetBottom(
				SPACE_BETWEEN_ELEMENTS_VERTICALLY);
		processedUiElementData.setAnchor(currentRow.getAnchor());
		processedUiElementData.setInsetTop(
				rowIndex == 0 && indexOfRowInsideSamePanel == 0 ?
						SPACE_BETWEEN_ELEMENTS_VERTICALLY :
						0);
		processedUiElementData.setInsetRight(
				SPACE_BETWEEN_ELEMENTS_HORIZONTALLY);
		processedUiElementData.setInsetLeft(
				columnIndex == 0 ? SPACE_BETWEEN_ELEMENTS_HORIZONTALLY : 0);
		setFillTypeForElement(uiElement, currentRow, processedUiElementData);
		return processedUiElementData;
	}

	private void setFillTypeForElement(JComponent uiElement,
			PanelRow currentRow,
			ProcessedUIElementData processedUiElementData) {
		FillType fillType = FillType.NONE;
		boolean shouldFillElementAndRowOrColumn = currentRow.getElementsToFill()
															.contains(
																	uiElement);

		boolean shouldFillElementButNotRowNorColumn = currentRow.getElementsToFillWithinColumnOrRowSize()
																.contains(
																		uiElement);
		if (shouldFillElementAndRowOrColumn) {
			fillType = currentRow.getElementsFillType();
		}
		if (shouldFillElementButNotRowNorColumn) {
			fillType = currentRow.getFillTypeWithinColumnOrRowSize();
		}
		processedUiElementData.setFillType(fillType,
				shouldFillElementAndRowOrColumn);

	}

	private void setPanelData(int rowIndex,
			ProcessedPanelData processedPanelData) {
		processedPanelData.setRowIndex(rowIndex);
		processedPanelData.setFillType(getTotalFillTypeBasedOnElements(
				processedPanelData.getProcessedUIElementsData()));
	}

	private FillType getTotalFillTypeBasedOnElements(
			List<ProcessedUIElementData> processedUIElementsData) {
		double maxWeightx = 0;
		double maxWeighty = 0;
		for (ProcessedUIElementData processedUIElementData : processedUIElementsData) {
			if (!processedUIElementData.shouldUseAllAvailableSpace()) {
				continue;
			}
			maxWeightx = Math.max(processedUIElementData.getFillType()
														.getWeightX(),
					maxWeightx);
			maxWeighty = Math.max(processedUIElementData.getFillType()
														.getWeightY(),
					maxWeighty);
		}
		return FillType.basedOnWeights(maxWeightx, maxWeighty);
	}

	private boolean rowRequiresNewPanel(PanelRow currentRow) {
		return currentRow.isFirstRow() || !currentRow.getPreviousRow()
													 .shouldKeepColumnSizeWithRowBelow();
	}

}
