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
	void shouldAddElementsToPanelInSingleRowWithSpacesBetween() {
		//given
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), new JButton("Test button"));

		//when
		swiftPanel.addElements(panelRows);
		JPanel panel = swiftPanel.getPanel();
		showPanel(panel);

		//then

		JPanel row1 = (JPanel) panel.getComponents()[0];
		List<Integer> spacesBetween = getDistanceBetweenElementsHorizontally(
				row1, 0);

		for (int i = 0; i < row1.getComponents().length - 1; i++) {
			assertTrue(row1.getComponents()[i + 1].getY()
					== row1.getComponents()[i].getY());
		}
		assertEquals(4, spacesBetween.size());
		assertEquals(spacesBetween.get(1), spacesBetween.get(0));
		assertEquals(spacesBetween.get(2), spacesBetween.get(1));
		assertEquals(spacesBetween.get(3), spacesBetween.get(2));
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
				row1, 0);
		List<Integer> horizontalSpacesBetweenElementsInRow2 = getDistanceBetweenElementsHorizontally(
				row2, 1);

		assertCorrectYPositionsInRow(row1);
		assertCorrectYPositionsInRow(row2);

		int firstRowPositionY = row1.getY();
		int distanceBetween1And2Row =
				row2.getY() - (firstRowPositionY + row1.getHeight());
		int distanceBetween2RowAndBottomEdge =
				panel.getHeight() - (row2.getY() + row2.getHeight());
		assertTrue(row1.getY() < row2.getY());
		assertTrue(firstRowPositionY > 0);
		assertTrue(distanceBetween1And2Row > 0);
		assertTrue(distanceBetween2RowAndBottomEdge > 0);
		assertEquals(firstRowPositionY, distanceBetween1And2Row);
		assertEquals(distanceBetween2RowAndBottomEdge, distanceBetween1And2Row);

		assertEquals(row1.getComponentCount() + 1,
				horizontalSpacesBetweenElementsInRow1.size());
		assertEquals(row2.getComponentCount() + 1,
				horizontalSpacesBetweenElementsInRow2.size());

		assertCorrectHorizontalSpaces(row1, 0);
		assertCorrectHorizontalSpaces(row2, 1);

		assertTrue(horizontalSpacesBetweenElementsInRow1.get(0) > 0);
	}

	private void assertCorrectHorizontalSpaces(JPanel row, int rowNumber) {
		List<Integer> horizontalSpacesBetweenElementsInRow1 = getDistanceBetweenElementsHorizontally(
				row, rowNumber);

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
			assertEquals(component1.getY(), component2.getY());
		}
	}

	private List<Integer> getDistanceBetweenElementsHorizontally(JPanel panel,
			int rowNumber) {
		Component[] elementsInRow = panel.getComponents();
		List<Integer> spacesBetween = new ArrayList<>();
		int distanceFromFirstElementToLeftEdgeOfPanel = elementsInRow[0].getX();
		spacesBetween.add(distanceFromFirstElementToLeftEdgeOfPanel);
		for (int i = 0; i < elementsInRow.length - 1; i++) {
			Component right = elementsInRow[i + 1];
			Component left = elementsInRow[i];
			spacesBetween.add(right.getX() - (left.getX() + left.getWidth()));
		}
		Component lastElement = elementsInRow[elementsInRow.length - 1];
		int distanceOfLastElementToRightEdgeOfPanel =
				panel.getWidth() - (lastElement.getX()
						+ lastElement.getWidth());
		spacesBetween.add(distanceOfLastElementToRightEdgeOfPanel);
		return spacesBetween;
	}

	private void showPanel(JPanel panel) {
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

}