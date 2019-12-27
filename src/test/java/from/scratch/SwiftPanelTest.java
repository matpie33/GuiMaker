package from.scratch;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwiftPanelTest extends AbstractPanelTest {

	private SwiftPanel swiftPanel = new SwiftPanel();

	//TODO create 2 or 3 tests only with different combinations:
	// 1 with normal rows, 1 with column rows, and in both tests add some filled
	// elements some anchored west/east etc
	@Test
	void shouldFillElementWithNormalRows(TestInfo testInfo) {
		//given
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test labelll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test row 2");
		JButton row2Element2 = new JButton("Test button");
		JButton row3Element1 = new JButton("Teeest");
		JButton row3Element2 = new JButton("Teeest2");
		JButton row4Element1 = new JButton("Tee last row test");
		JButton row4Element2 = new JButton("Test test test");

		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).fillElement(FillType.HORIZONTAL, row1Element1)
							 .nextRow(row2Element1, row2Element2)
							 .fillElement(FillType.BOTH, row2Element2)
							 .nextRow(row3Element1, row3Element2)
							 .nextRow(row4Element1, row4Element2)
							 .fillElement(FillType.HORIZONTAL, row4Element2);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then
		assertDistancesBetweenElementsHorizontally(
				(JPanel) panel.getComponents()[0], panel, true);
		assertDistancesBetweenElementsHorizontally(
				(JPanel) panel.getComponents()[1], panel, true);
		assertDistancesBetweenElementsHorizontally(
				(JPanel) panel.getComponents()[2], panel, false);
		assertDistancesBetweenElementsHorizontally(
				(JPanel) panel.getComponents()[3], panel, true);
		//noinspection unchecked
		assertDistancesBetweenRows(panel,
				Arrays.asList(row1Element1, row1Element3),
				Collections.singletonList(row2Element2),
				Arrays.asList(row3Element1, row3Element2),
				Arrays.asList(row4Element1, row4Element2));

		assertThatComponentIsCenteredBetweenElements(row1Element1, panel,
				row2Element2);
		assertThatComponentIsCenteredBetweenElements(row2Element1, row1Element1,
				row3Element1);

	}

	@Test
	void shouldFillElementWithRowsPutAsColumns(TestInfo testInfo) {
		//given
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test long labellllllllllllllll");
		JButton row1Element3 = new JButton("Test very long but");
		JButton row2Element1 = new JButton("Test");
		JTextField row2Element2 = new JTextField("Test");
		JComboBox<String> row2Element3 = new JComboBox<>();
		row2Element3.addItem("Some text in there but not here");
		JButton row3Element1 = new JButton("Test");
		JTextField row3Element2 = new JTextField("Test");
		JTextArea row3Element3 = new JTextArea("some kind", 1, 10);
		JButton row3Element4 = new JButton("Test qwerwerwerw");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRowKeepingColumnSize(row2Element1,
				row2Element2, row2Element3)
							 .nextRowKeepingColumnSize(row3Element1,
									 row3Element2, row3Element3, row3Element4)
							 .fillElement(FillType.BOTH, row3Element2,
									 row3Element3);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then
		assertSameXPositions(row1Element1, row2Element1, row3Element1);
		assertSameXPositions(row1Element2, row2Element2, row3Element2);
		assertSameXPositions(row1Element3, row2Element3, row3Element3);

		assertSameYPositions(row1Element1, row1Element3);
		assertSameYPositions(row2Element1, row2Element3);
		assertSameYPositions(row3Element1, row3Element2, row3Element3,
				row3Element4);

		assertThatComponentIsCenteredBetweenElements(row1Element2, panel,
				row2Element1);
		assertThatComponentIsCenteredBetweenElements(row2Element2, row1Element3,
				row3Element2);
		assertElementsFilled(
				Arrays.asList(row3Element1, row3Element2, row3Element3,
						row3Element4),
				Arrays.asList(row3Element2, row3Element3));
		assertDistanceBetweenElementsHorizontally(panel, panel, true,
				row3Element1, row3Element2, row3Element3, row3Element4);
		Set<Integer> differentHeights = Stream.of(row3Element1, row3Element2,
				row3Element3, row3Element4)
											  .map(JComponent::getHeight)
											  .collect(Collectors.toSet());
		assertTrue(differentHeights.size() == 1);
		//noinspection unchecked
	}

	@Test
	void shouldAddElementsToPanelInSingleRowWithSpacesBetween(
			TestInfo testInfo) {
		//given
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), new JButton("Test button"));
		int numberOfElements = panelRows.getUiComponents()
										.size();

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then

		JPanel row1 = (JPanel) panel.getComponents()[0];

		for (int i = 0; i < row1.getComponents().length - 1; i++) {
			assertTrue(row1.getComponents()[i + 1].getY()
					== row1.getComponents()[i].getY());
		}
	}

	@Disabled
	@Test
	void shouldAddElementsToPanelInTwoRowsWithSpacesBetween(TestInfo testInfo) {
		//given
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), new JButton("Test button")).nextRow(
				new JButton("Row2"), new JButton("Row2.22"));
		List<PanelRows> rows = panelRows.getRows();

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then

		JPanel row1 = (JPanel) panel.getComponents()[0];
		JPanel row2 = (JPanel) panel.getComponents()[1];

		assertCorrectYPositionsInRow(row1);
		assertCorrectYPositionsInRow(row2);

		int distanceBetweenFirstRowAndPanelTopEdge =
				getYCoordinate(row1.getComponent(0)) - getYCoordinate(panel);
		int distanceBetween1And2Row = getYCoordinate(row2.getComponent(0)) - (
				getYCoordinate(row1.getComponent(0)) + row1.getComponent(0)
														   .getHeight());
		int distanceBetween2RowAndPanelBottomEdge =
				getYCoordinate(panel) + panel.getHeight() - (
						getYCoordinate(row2.getComponent(0))
								+ row2.getComponent(0)
									  .getHeight());

		assertTrue(row1.getY() < row2.getY());
		assertTrue(distanceBetween1And2Row > 0);
		assertTrue(distanceBetween2RowAndPanelBottomEdge > 0);
		assertEquals(distanceBetweenFirstRowAndPanelTopEdge,
				distanceBetween1And2Row);
		assertEquals(distanceBetween2RowAndPanelBottomEdge,
				distanceBetween1And2Row);

	}

	@Disabled
	@Test
	void shouldAddElementsToPanelInTwoRowsWithKeepingColumnSize(
			TestInfo testInfo) {
		//given
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test label");
		JButton row1Element3 = new JButton("Test button");
		JButton row2Element1 = new JButton("Row2");
		JButton row2Element2 = new JButton("Row2.22");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRowKeepingColumnSize(row2Element1,
				row2Element2);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then

		assertEquals(getXCoordinate(row1Element1),
				getXCoordinate(row2Element1));
		assertEquals(getXCoordinate(row1Element2),
				getXCoordinate(row2Element2));

		assertEquals(getYCoordinate(row1Element1),
				getYCoordinate(row1Element2));
		assertEquals(getYCoordinate(row1Element2),
				getYCoordinate(row1Element3));
		assertEquals(getYCoordinate(row2Element1),
				getYCoordinate(row2Element2));

		int distanceBetweenRow1And2 =
				getYCoordinate(row2Element1) - (getYCoordinate(row1Element1)
						+ row1Element1.getHeight());
		assertEquals(distanceBetweenRow1And2,
				getYCoordinate(row1Element1) - getYCoordinate(panel));
		assertEquals(getYCoordinate(panel) + panel.getHeight() - (
						getYCoordinate(row2Element1) + row2Element1.getHeight()),
				distanceBetweenRow1And2);
		assertTrue(distanceBetweenRow1And2 > 0);

	}

}