package from.scratch;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RowsPreprocessor {

	private static final int SPACE_BETWEEN_ELEMENTS_HORIZONTALLY = 4;
	private static final int SPACE_BETWEEN_ELEMENTS_VERTICALLY = 3;

	public List<PanelBuilder> preprocess(PanelRow panelRow) {

		List<PanelBuilder> panelBuilders = new ArrayList<>();
		int rowIndex = 0;
		int indexOfRowInsideSamePanel = 0;
		boolean existsRowFilledVertically = false;
		PanelBuilder panelBuilder = null;
		PanelRow currentRow = findFirstPanel(panelRow);
		while (currentRow != null) {
			checkCorrectElementsToFill(currentRow);

			if (panelBuilder == null || rowRequiresNewPanel(currentRow)) {
				indexOfRowInsideSamePanel = 0;
				panelBuilder = new PanelBuilder();
				panelBuilders.add(panelBuilder);
			}
			else {
				indexOfRowInsideSamePanel++;
			}
			createUIElements(currentRow, panelBuilder,
					indexOfRowInsideSamePanel, rowIndex);
			setPanelProperties(currentRow, rowIndex, panelBuilder,
					indexOfRowInsideSamePanel);
			existsRowFilledVertically =
					existsRowFilledVertically || containsVerticalFill(
							panelBuilder.getFillType());
			currentRow = currentRow.getNextRow();
			if (panelBuilder.requiresNewRow()) {
				rowIndex++;
			}

		}
		rowIndex++;
		assert panelBuilder != null;
		panelBuilders.add(createEmptyPanelToTheBottomOfTheParentPanel(
				existsRowFilledVertically, rowIndex));
		return panelBuilders;

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

	private PanelBuilder createEmptyPanelToTheBottomOfTheParentPanel(
			boolean existsRowFilledVertically, int rowIndex) {
		PanelBuilder panelBuilder = new PanelBuilder();
		panelBuilder.setFillType(!existsRowFilledVertically ?
				FillType.BOTH :
				FillType.HORIZONTAL);
		panelBuilder.setRowIndex(rowIndex);
		return panelBuilder;
	}

	private boolean containsVerticalFill(FillType fillType) {
		return fillType.equals(FillType.VERTICAL) || fillType.equals(
				FillType.BOTH);
	}

	private boolean containsHorizontalFill(FillType fillType) {
		return fillType.equals(FillType.HORIZONTAL) || fillType.equals(
				FillType.BOTH);
	}

	private void createUIElements(PanelRow currentRow,
			PanelBuilder panelBuilder, int indexOfRowInsideSamePanel,
			int rowIndex) {
		List<JComponent> uiElements = currentRow.getUIElements();
		for (int indexOfElementInRow = 0;
			 indexOfElementInRow < uiElements.size(); indexOfElementInRow++) {
			JComponent uiElement = uiElements.get(indexOfElementInRow);
			panelBuilder.addElementBuilder(
					createUIElement(uiElement, currentRow, indexOfElementInRow,
							indexOfRowInsideSamePanel, rowIndex));
		}
	}

	private UIElementBuilder createUIElement(JComponent uiElement,
			PanelRow currentRow, int columnIndex, int indexOfRowInsideSamePanel,
			int rowIndex) {
		UIElementBuilder uiElementBuilder = new UIElementBuilder();
		uiElementBuilder.setUiElement(uiElement);
		uiElementBuilder.setColumnIndex(columnIndex);
		uiElementBuilder.setRowIndex(indexOfRowInsideSamePanel);
		uiElementBuilder.setInsetBottom(SPACE_BETWEEN_ELEMENTS_VERTICALLY);

		uiElementBuilder.setInsetTop(
				rowIndex == 0 ? SPACE_BETWEEN_ELEMENTS_VERTICALLY : 0);
		uiElementBuilder.setInsetRight(SPACE_BETWEEN_ELEMENTS_HORIZONTALLY);
		uiElementBuilder.setInsetLeft(
				columnIndex == 0 ? SPACE_BETWEEN_ELEMENTS_HORIZONTALLY : 0);
		setFillTypeForElement(uiElement, currentRow, uiElementBuilder);
		return uiElementBuilder;
	}

	private void setFillTypeForElement(JComponent uiElement,
			PanelRow currentRow, UIElementBuilder uiElementBuilder) {
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
		uiElementBuilder.setFillType(fillType, shouldFillElementAndRowOrColumn);

	}

	private void setPanelProperties(PanelRow currentRow, int rowIndex,
			PanelBuilder panelBuilder, int indexOfRowInsideSamePanel) {
		panelBuilder.setRequiresNewRow(rowRequiresNewPanel(currentRow));
		panelBuilder.setLast(currentRow.isLastRow());
		panelBuilder.setRowIndex(rowIndex);
		panelBuilder.setFillType(currentRow.getRowFillType());
		panelBuilder.setNumberOfRowsInPanel(indexOfRowInsideSamePanel + 1);
		panelBuilder.setFillType(getTotalFillTypeBasedOnElements(
				panelBuilder.getElementsBuilders()));
	}

	private FillType getTotalFillTypeBasedOnElements(
			List<UIElementBuilder> elementsBuilders) {
		boolean hasHorizontalFill = false;
		boolean hasVerticalFill = false;
		for (UIElementBuilder elementsBuilder : elementsBuilders) {
			if (!elementsBuilder.shouldUseAllAvailableSpace()) {
				continue;
			}
			if (elementsBuilder.getFillType()
							   .equals(FillType.BOTH)) {
				return elementsBuilder.getFillType();
			}
			if (containsHorizontalFill(elementsBuilder.getFillType())) {
				hasHorizontalFill = true;
			}
			if (containsVerticalFill(elementsBuilder.getFillType())) {
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
