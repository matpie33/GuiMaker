package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RowsPreprocessor {

	private static final int SPACE_BETWEEN_ELEMENTS_HORIZONTALLY = 4;
	private static final int SPACE_BETWEEN_ELEMENTS_VERTICALLY = 3;
	private ProcessedPanelData lastPanelWithUIElements;
	private ProcessedPanelData lastPanelPlaceholder;

	public List<ProcessedPanelData> preprocess(PanelRow panelRow) {

		List<ProcessedPanelData> processedPanelsData = new ArrayList<>();
		boolean existsRowFilledVertically = false;
		ProcessedPanelData currentlyProcessedPanel = null;
		PanelRow currentRow = findFirstPanel(panelRow);

		int indexOfRowInsideSamePanel = 0;
		int rowIndex = lastPanelPlaceholder != null ?
				lastPanelPlaceholder.getRowIndex() :
				0;
		while (currentRow != null) {
			validate(currentRow);

			if (currentlyProcessedPanel == null || rowRequiresNewPanel(
					currentRow)) {
				indexOfRowInsideSamePanel = currentRow.shouldKeepColumnSizeWithRowAbove() ?
						getLastRowIndexFromLastPanelCreated() :
						0;
				currentlyProcessedPanel = new ProcessedPanelData();
				processedPanelsData.add(currentlyProcessedPanel);
			}
			else {
				indexOfRowInsideSamePanel++;
			}
			createUIElementsData(currentRow, currentlyProcessedPanel,
					indexOfRowInsideSamePanel, rowIndex);
			setPanelData(currentRow, rowIndex, currentlyProcessedPanel);
			existsRowFilledVertically =
					existsRowFilledVertically || containsVerticalFill(
							currentlyProcessedPanel.getFillType());
			currentRow = currentRow.getNextRow();
			if (currentRow == null || rowRequiresNewPanel(currentRow)) {
				rowIndex++;
			}

		}
		assert currentlyProcessedPanel != null;
		lastPanelWithUIElements = currentlyProcessedPanel;
		ProcessedPanelData placeHolderPanel = createEmptyPanelToTheBottomOfTheParentPanel(
				existsRowFilledVertically, rowIndex);
		processedPanelsData.add(placeHolderPanel);
		lastPanelPlaceholder = placeHolderPanel;
		return processedPanelsData;

	}

	private void validate(PanelRow currentRow) {
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
		if (currentRow.shouldKeepColumnSizeWithRowAbove()
				&& !currentRow.isFirstRow()) {
			throw new IllegalArgumentException("Wrong method called: should "
					+ "use method \"nextRowKeepingColumnSize\" for subsequent"
					+ " rows");
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
		processedUiElementData.setRowIndex(indexOfRowInsideSamePanel);
		processedUiElementData.setUiElement(uiElement);
		processedUiElementData.setColumnIndex(columnIndex);

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

	private int getLastRowIndexFromLastPanelCreated() {
		List<ProcessedUIElementData> processedUIElementsData = lastPanelWithUIElements.getProcessedUIElementsData();
		ProcessedUIElementData lastUIElementInPanel = processedUIElementsData.get(
				processedUIElementsData.size() - 1);
		return lastUIElementInPanel.getRowIndex() + 1;
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

	private void setPanelData(PanelRow currentRow, int rowIndex,
			ProcessedPanelData processedPanelData) {
		if (currentRow.shouldKeepColumnSizeWithRowAbove()) {
			processedPanelData.setShouldAddElementsToPreviousPanel();
		}
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
