package from.scratch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwiftPanelTest extends AbstractPanelTest {

	private SwiftPanel swiftPanel = new SwiftPanel();

	@Test
	void shouldFillElementsHorizontalyAndVerticalWithNormalRows(
			TestInfo testInfo) {
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
		assertElementsFilledHorizontally(
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
	void shouldFillElementBothVerticalAndHorizontal(TestInfo testInfo) {
		//given
		JButton row2Element2 = new JButton("filled");
		JButton row3Element1 = new JButton("Full");
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test label");
		JButton row1Element3 = new JButton("Test button");
		JButton row2Element1 = new JButton("a");
		JButton row4Element1 = new JButton("test");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRow(row2Element1, row2Element2)
							 .fillElement(FillType.VERTICAL, row2Element2)
							 .fillThisRowVertically()
							 .nextRow(row3Element1)
							 .fillThisRowHorizontallyAndVertically()
							 .nextRow(row4Element1);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel, testInfo);

		//then

		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row1Element1, row1Element2, row1Element3);
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row2Element1, row2Element2);
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row3Element1);
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row4Element1);
		//noinspection unchecked
		assertDistancesBetweenRows(panel,
				Arrays.asList(row1Element1, row1Element3),
				Collections.singletonList(row2Element2),
				Collections.singletonList(row3Element1),
				Collections.singletonList(row4Element1));
		assertThatComponentIsCenteredBetweenElements(row2Element1, row1Element1,
				row3Element1);

		assertTrue(row1Element1.getHeight() < 1D / 10D * panel.getHeight());
		assertTrue(row2Element2.getHeight() > 1D / 3D * panel.getHeight());
		assertEquals(row3Element1.getHeight(), row2Element2.getHeight());
		assertEquals(getXCoordinate(row3Element1) - getXCoordinate(panel),
				getXCoordinate(panel) + panel.getWidth() - (
						getXCoordinate(row3Element1)
								+ row3Element1.getWidth()));
		assertEquals(getXCoordinate(row3Element1) - getXCoordinate(panel),
				getXCoordinate(row2Element1) - getXCoordinate(panel));
		assertTrue(getYCoordinate(panel) + panel.getHeight() - (
				row4Element1.getHeight() + getYCoordinate(row4Element1)) < 5);
	}

}