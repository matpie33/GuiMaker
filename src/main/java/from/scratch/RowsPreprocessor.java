package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RowsPreprocessor {

	private static final int SPACE_BETWEEN_ELEMENTS_HORIZONTALLY = 4;
	private static final int SPACE_BETWEEN_ELEMENTS_VERTICALLY = 3;

	public List<ProcessedPanelData> preprocess(PanelRow panelRow) {

		List<ProcessedPanelData> processedPanelsData = new ArrayList<>();
		int rowIndex = 0;
		int indexOfRowInsideSamePanel = 0;
		boolean existsRowFilledVertically = false;
		ProcessedPanelData currentlyProcessedPanel = null;
		PanelRow currentRow = findFirstPanel(panelRow);
		while (currentRow != null) {
			checkCorrectElementsToFill(currentRow);

			if (currentlyProcessedPanel == null || rowRequiresNewPanel(currentRow)) {
				indexOfRowInsideSamePanel = 0;
				currentlyProcessedPanel = new ProcessedPanelData();
				processedPanelsData.add(currentlyProcessedPanel);
			}
			else {
				indexOfRowInsideSamePanel++;
			}
			createUIElementsData(currentRow, currentlyProcessedPanel,
					indexOfRowInsideSamePanel, rowIndex);
			setPanelData(currentRow, rowIndex, currentlyProcessedPanel,
					indexOfRowInsideSamePanel);
			existsRowFilledVertically =
					existsRowFilledVertically || containsVerticalFill(
							currentlyProcessedPanel.getFillType());
			currentRow = currentRow.getNextRow();
			if (currentRow == null || rowRequiresNewPanel(currentRow)) {
				rowIndex++;
			}

		}
		assert currentlyProcessedPanel != null;
		processedPanelsData.add(createEmptyPanelToTheBottomOfTheParentPanel(
				existsRowFilledVertically, rowIndex));
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
		return processedPanelData;
	}

	private boolean containsVerticalFill(FillType fillType) {
		return fillType.equals(FillType.VERTICAL) || fillType.equals(
				FillType.BOTH);
	}

	private boolean containsHorizontalFill(FillType fillType) {
		return fillType.equals(FillType.HORIZONTAL) || fillType.equals(
				FillType.BOTH);
	}

	private void createUIElementsData(PanelRow currentRow,
			ProcessedPanelData processedPanelData, int indexOfRowInsideSamePanel,
			int rowIndex) {
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
		processedUiElementData.setInsetBottom(SPACE_BETWEEN_ELEMENTS_VERTICALLY);

		processedUiElementData.setInsetTop(
				rowIndex == 0 && indexOfRowInsideSamePanel == 0 ?
						SPACE_BETWEEN_ELEMENTS_VERTICALLY :
						0);
		processedUiElementData.setInsetRight(SPACE_BETWEEN_ELEMENTS_HORIZONTALLY);
		processedUiElementData.setInsetLeft(
				columnIndex == 0 ? SPACE_BETWEEN_ELEMENTS_HORIZONTALLY : 0);
		setFillTypeForElement(uiElement, currentRow, processedUiElementData);
		return processedUiElementData;
	}

	private void setFillTypeForElement(JComponent uiElement,
			PanelRow currentRow, ProcessedUIElementData processedUiElementData) {
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
		processedUiElementData.setFillType(fillType, shouldFillElementAndRowOrColumn);

	}

	private void setPanelData(PanelRow currentRow, int rowIndex,
			ProcessedPanelData processedPanelData, int indexOfRowInsideSamePanel) {
		processedPanelData.setLast(currentRow.isLastRow());
		processedPanelData.setRowIndex(rowIndex);
		processedPanelData.setFillType(currentRow.getRowFillType());
		processedPanelData.setNumberOfRowsInPanel(indexOfRowInsideSamePanel + 1);
		processedPanelData.setFillType(getTotalFillTypeBasedOnElements(
				processedPanelData.getProcessedUIElementsData()));
	}

	private FillType getTotalFillTypeBasedOnElements(
			List<ProcessedUIElementData> processedUIElementsData) {
		boolean hasHorizontalFill = false;
		boolean hasVerticalFill = false;
		for (ProcessedUIElementData processedUIElementData : processedUIElementsData) {
			if (!processedUIElementData.shouldUseAllAvailableSpace()) {
				continue;
			}
			if (processedUIElementData.getFillType()
							   .equals(FillType.BOTH)) {
				return processedUIElementData.getFillType();
			}
			if (containsHorizontalFill(processedUIElementData.getFillType())) {
				hasHorizontalFill = true;
			}
			if (containsVerticalFill(processedUIElementData.getFillType())) {
				hasVerticalFill = true;
			}
		}
		if (hasHorizontalFill && !hasVerticalFill) {
			return FillType.HORIZONTAL;
		}
		else if (hasVerticalFill && !hasHorizontalFill) {
			return FillType.VERTICAL;
		}
		else //noinspection ConstantConditions
			if (hasVerticalFill && hasHorizontalFill) {
				return FillType.BOTH;
			}
			else {
				return FillType.NONE;
			}
	}

	private boolean rowRequiresNewPanel(PanelRow currentRow) {
		return currentRow.isFirstRow() || !currentRow.getPreviousRow()
													 .shouldKeepColumnSizeWithRowBelow();
	}

}
