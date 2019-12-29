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
			existsRowFilledVertically =
					existsRowFilledVertically || containsVerticalFill(
							currentRow.getRowFillType());
			if (panelBuilder == null || rowRequiresNewPanel(currentRow)) {
				indexOfRowInsideSamePanel = 0;
				panelBuilder = createRowBuilder(currentRow, rowIndex,
						existsRowFilledVertically);
				panelBuilders.add(panelBuilder);
			}
			else {
				indexOfRowInsideSamePanel++;
			}
			createUIElements(currentRow, panelBuilder,
					indexOfRowInsideSamePanel, rowIndex);
			adjustAdditionalProperties(panelBuilder);
			currentRow = currentRow.getNextRow();
			if (panelBuilder.requiresNewRow()) {
				rowIndex++;
			}

		}
		rowIndex++;
		assert panelBuilder != null;
		if (!existsRowFilledVertically) {
			panelBuilders.add(
					createEmptyPanelToTheBottomOfTheParentPanel(rowIndex));
		}
		return panelBuilders;

	}

	private void adjustAdditionalProperties(PanelBuilder panelBuilder) {
		getFillTypeEqualToRowIfOnly1Element(panelBuilder);
		checkIfLastElementInRowShouldGainAllPossibleHorizontalSpace(
				panelBuilder, panelBuilder.getElementsBuilders());
	}

	private void getFillTypeEqualToRowIfOnly1Element(
			PanelBuilder panelBuilder) {
		if (panelBuilder.getElementsBuilders()
						.size() == 1) {
			panelBuilder.getElementsBuilders()
						.get(0)
						.setFillType(panelBuilder.getRowFillType());
		}
	}

	private void checkCorrectElementsToFill(PanelRow currentRow) {
		if (!currentRow.getUIElements()
					   .containsAll(currentRow.getElementsToFill())) {
			throw new IllegalArgumentException(
					"Some of the elements to fill are not on the"
							+ " list of elements to add to row.");
		}
	}

	private void checkIfLastElementInRowShouldGainAllPossibleHorizontalSpace(
			PanelBuilder panelBuilder,
			List<UIElementBuilder> elementsBuilders) {
		if (panelBuilder.requiresNewRow()) {
			panelBuilder.setHasElementFilledHorizontallyToTheRightEdgeOfPanel(
					elementsBuilders.stream()
									.anyMatch(uiElement -> containsHorizontalFill(
											uiElement.getFillType())));
		}
		else {
			UIElementBuilder lastElement = elementsBuilders.size() > 1 ?
					elementsBuilders.get(elementsBuilders.size() - 1) :
					elementsBuilders.get(0);
			panelBuilder.setHasElementFilledHorizontallyToTheRightEdgeOfPanel(
					containsHorizontalFill(lastElement.getFillType()));
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
			int rowIndex) {
		PanelBuilder panelBuilder = new PanelBuilder();
		panelBuilder.setShouldTakeAllSpaceToBottom(true);
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
		uiElementBuilder.setFillType(currentRow.getElementsToFill()
											   .contains(uiElement) ?
				currentRow.getElementsFillType() :
				FillType.NONE);
		return uiElementBuilder;
	}

	private PanelBuilder createRowBuilder(PanelRow currentRow, int rowIndex,
			boolean existsRowFilledVertically) {
		PanelBuilder panelBuilder = new PanelBuilder();
		panelBuilder.setRowFillType(currentRow.getRowFillType());
		panelBuilder.setRequiresNewRow(rowRequiresNewPanel(currentRow));
		panelBuilder.setHighestNumberOfColumnsInPanel(currentRow.getUIElements()
																.size());
		panelBuilder.setLast(currentRow.isLastRow());
		panelBuilder.setRowIndex(rowIndex);
		panelBuilder.setShouldTakeAllSpaceToBottom(
				containsVerticalFill(currentRow.getRowFillType()) || (
						currentRow.isLastRow() && !existsRowFilledVertically));
		return panelBuilder;
	}

	private boolean rowRequiresNewPanel(PanelRow currentRow) {
		return !currentRow.hasPreviousRow() || !currentRow.getPreviousRow()
														  .shouldKeepColumnSizeWithRowBelow();
	}

}
