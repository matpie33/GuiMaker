package from.scratch;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwiftPanelTest {

	private SwiftPanel swiftPanel = new SwiftPanel();

	@Test
	void shouldAddElementsToPanel() {
		//given
		JButton button1 = new JButton("Test");
		JLabel label = new JLabel("Test label");
		JButton button2 = new JButton("Test button");
		PanelRows panelRows = new PanelRows(button1, label, button2);

		//when
		swiftPanel.addElements(panelRows);

		//then
		JPanel row1 = (JPanel) swiftPanel.getPanel()
										 .getComponents()[0];
		List<Component> components = Arrays.asList(row1.getComponents());
		assertTrue(components.contains(button1));
		assertTrue(components.contains(label));
		assertTrue(components.contains(button2));
		assertTrue(components.size() == 3);
	}

	@Test
	void shouldFillElementWithNormalRows() {
		//given
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test labelll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test");
		JButton row2Element2 = new JButton("Test button");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRow(row2Element1, row2Element2)
							 .fillElement(FillType.HORIZONTAL, row2Element2);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel);

		//then
		assertEquals(getXCoordinate(panel) + panel.getWidth() - (
						getXCoordinate(row2Element2) + row2Element2.getWidth()),
				getXCoordinate(row2Element1) - getXCoordinate(panel));
	}

	@Test
	void shouldFillElementWithRowsPutAsColumns() {
		//given
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test long labellllllllllllllll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test");
		JButton row2Element2 = new JButton("Test");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRowKeepingColumnSize(row2Element1,
				row2Element2)
							 .fillElement(FillType.HORIZONTAL, row2Element2);

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel);

		//then
		assertEquals(
				getXCoordinate(row1Element3) - (getXCoordinate(row2Element2)
						+ row2Element2.getWidth()),
				getXCoordinate(row2Element1) - getXCoordinate(panel));
	}

	@Test
	void shouldAddElementsToPanelInSingleRowWithSpacesBetween() {
		//given
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), new JButton("Test button"));
		int numberOfElements = panelRows.getUiComponents()
										.size();

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel);

		//then

		JPanel row1 = (JPanel) panel.getComponents()[0];
		List<Integer> spacesBetween = getDistanceBetweenElementsHorizontally(
				row1);

		for (int i = 0; i < row1.getComponents().length - 1; i++) {
			assertTrue(row1.getComponents()[i + 1].getY()
					== row1.getComponents()[i].getY());
		}
		assertEquals(numberOfElements, spacesBetween.size());
		assertEquals(spacesBetween.get(1), spacesBetween.get(0));
		assertEquals(spacesBetween.get(2), spacesBetween.get(1));
		assertTrue(spacesBetween.get(0) > 0);
	}

	@Test
	void shouldAddElementsToPanelInTwoRowsWithSpacesBetween() {
		//given
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), new JButton("Test button")).nextRow(
				new JButton("Row2"), new JButton("Row2.22"));
		List<PanelRows> rows = panelRows.getRows();

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel);

		//then

		JPanel row1 = (JPanel) panel.getComponents()[0];
		JPanel row2 = (JPanel) panel.getComponents()[1];
		List<Integer> horizontalSpacesBetweenElementsInRow1 = getDistanceBetweenElementsHorizontally(
				row1);
		List<Integer> horizontalSpacesBetweenElementsInRow2 = getDistanceBetweenElementsHorizontally(
				row2);

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

		assertEquals(row1.getComponentCount(),
				horizontalSpacesBetweenElementsInRow1.size());
		assertEquals(row2.getComponentCount(),
				horizontalSpacesBetweenElementsInRow2.size());

		assertCorrectHorizontalSpaces(row1);
		assertCorrectHorizontalSpaces(row2);

		assertTrue(horizontalSpacesBetweenElementsInRow1.get(0) > 0);
	}

	@Test
	void shouldAddElementsToPanelInTwoRowsWithKeepingColumnSize() {
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
		showPanel(panel);

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

	private int getYCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getY();
	}

	private int getXCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getX();
	}

	private void assertCorrectHorizontalSpaces(JPanel row) {
		List<Integer> horizontalSpacesBetweenElementsInRow1 = getDistanceBetweenElementsHorizontally(
				row);

		for (int i = 0;
			 i < horizontalSpacesBetweenElementsInRow1.size() - 1; i++) {
			assertEquals(horizontalSpacesBetweenElementsInRow1.get(i + 1),
					horizontalSpacesBetweenElementsInRow1.get(i));
		}
	}

	private void assertCorrectYPositionsInRow(JPanel row) {
		int numberOfElementsInRow1 = row.getComponentCount();

		for (int i = 0; i < numberOfElementsInRow1 - 1; i++) {
			Component component1 = row.getComponents()[i];
			Component component2 = row.getComponents()[i + 1];
			assertEquals(component1.getLocationOnScreen()
								   .getY(), component2.getLocationOnScreen()
													  .getY());
		}
	}

	private List<Integer> getDistanceBetweenElementsHorizontally(JPanel panel) {
		Component[] elementsInRow = panel.getComponents();
		List<Integer> spacesBetween = new ArrayList<>();
		int distanceFromFirstElementToLeftEdgeOfPanel =
				getXCoordinate(elementsInRow[0]) - getXCoordinate(panel);
		spacesBetween.add(distanceFromFirstElementToLeftEdgeOfPanel);
		for (int i = 0; i < elementsInRow.length - 1; i++) {
			Component right = elementsInRow[i + 1];
			Component left = elementsInRow[i];
			spacesBetween.add(getXCoordinate(right) - (getXCoordinate(left)
					+ left.getWidth()));
		}
		return spacesBetween;
	}

	private void showPanel(JPanel panel) {
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);

		if (System.getProperty("Wait")!=null){
			try {
				Thread.sleep(1000000L);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}