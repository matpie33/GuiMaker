package from.scratch;

import org.junit.jupiter.api.TestInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractPanelTest {

	protected void assertDistancesBetweenRows(JPanel panel,
			List<JComponent>... rows) {
		for (List<JComponent> row : rows) {
			for (int i = 0; i < row.size() - 1; i++) {
				JComponent element1 = row.get(i);
				JComponent element2 = row.get(i + 1);
				assertEquals(getYCoordinate(element1),
						getYCoordinate(element2));
			}
		}
		Set<Integer> verticalDistancesBetweenRows = new HashSet<>();
		JComponent element1Row1 = rows[0].get(0);
		verticalDistancesBetweenRows.add(
				getYCoordinate(element1Row1) - getYCoordinate(panel));
		for (int i = 0; i < rows.length - 1; i++) {
			List<JComponent> row1 = rows[i];
			List<JComponent> row2 = rows[i + 1];
			verticalDistancesBetweenRows.add(
					getYCoordinate(row2.get(0)) - (getYCoordinate(row1.get(0))
							+ row1.get(0)
								  .getHeight()));

		}
		assertTrue(verticalDistancesBetweenRows.size() == 1);

	}

	protected void assertElementsFilled(List<JComponent> allElementsInRow,
			List<JComponent> elementsFilled) {
		Map<JComponent, Double> weightsOfElements = new HashMap<>();
		double sumOfWidths = 0;
		for (JComponent element : allElementsInRow) {
			sumOfWidths += element.getWidth();
		}
		for (JComponent element : allElementsInRow) {
			weightsOfElements.put(element,
					(double) element.getWidth() / sumOfWidths);
		}
		List<JComponent> notFilledElements = allElementsInRow.stream()
															 .filter(element -> !elementsFilled.contains(
																	 element))
															 .collect(
																	 Collectors.toList());
		for (JComponent filledElement : elementsFilled) {
			Double filledElementWeight = weightsOfElements.get(filledElement);
			for (JComponent notFilledElement : notFilledElements) {
				assertTrue(filledElementWeight > weightsOfElements.get(
						notFilledElement));
			}
		}
	}

	protected void assertThatComponentIsCenteredBetweenElements(
			JComponent elementBetween, JComponent elementAbove,
			JComponent elementBelow) {
		int yCoordinateElementBetween = getYCoordinate(elementBetween);
		int yCoordinateElementAbove = getYCoordinate(elementAbove);
		int yCoordinateElementBelow = getYCoordinate(elementBelow);

		int distanceBetweenElementAndAbove =
				yCoordinateElementBetween - (yCoordinateElementAbove + (
						elementAbove instanceof JPanel ?
								0 :
								elementAbove.getHeight()));
		int distanceBetweenElementAndBelow =
				yCoordinateElementBelow - (yCoordinateElementBetween
						+ elementBetween.getHeight());
		assertTrue(Math.abs(
				distanceBetweenElementAndAbove - distanceBetweenElementAndBelow)
				< 3);

	}

	protected int getYCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getY();
	}

	protected int getXCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getX();
	}

	protected void assertCorrectYPositionsInRow(JPanel row) {
		int numberOfElementsInRow1 = row.getComponentCount();

		for (int i = 0; i < numberOfElementsInRow1 - 1; i++) {
			Component component1 = row.getComponents()[i];
			Component component2 = row.getComponents()[i + 1];
			assertEquals(component1.getLocationOnScreen()
								   .getY(), component2.getLocationOnScreen()
													  .getY());
		}
	}

	protected void assertSameXPositions(JComponent... elements) {
		Set<Integer> xPositions = Arrays.stream(elements)
										.map(this::getXCoordinate)
										.collect(Collectors.toSet());
		assertTrue(xPositions.size() == 1);
	}

	protected void assertSameYPositions(JComponent... elements) {
		Set<Integer> yPositions = Arrays.stream(elements)
										.map(this::getYCoordinate)
										.collect(Collectors.toSet());
		assertTrue(yPositions.size() == 1);
	}

	protected void assertDistancesBetweenElementsHorizontally(JPanel panel,
			JPanel rootPanel, boolean checkRightPanelEdgeToo) {
		Component[] elementsInRow = panel.getComponents();
		assertDistanceBetweenElementsHorizontally(panel, rootPanel,
				checkRightPanelEdgeToo, elementsInRow);
	}

	protected void assertDistanceBetweenElementsHorizontally(JComponent firstElement,
			JPanel rootPanel, boolean checkRightPanelEdgeToo,
			Component... elementsInRow) {
		List<Integer> spacesBetween = new ArrayList<>();
		int distanceFromFirstElementToLeftEdgeOfPanel =
				getXCoordinate(elementsInRow[0]) - getXCoordinate(firstElement);
		spacesBetween.add(distanceFromFirstElementToLeftEdgeOfPanel);
		for (int i = 0; i < elementsInRow.length - 1; i++) {
			Component right = elementsInRow[i + 1];
			Component left = elementsInRow[i];
			spacesBetween.add(getXCoordinate(right) - (getXCoordinate(left)
					+ left.getWidth()));
		}
		if (checkRightPanelEdgeToo) {
			Component lastElement = elementsInRow[elementsInRow.length - 1];
			spacesBetween.add(
					getXCoordinate(rootPanel) + rootPanel.getWidth() - (
							getXCoordinate(lastElement)
									+ lastElement.getWidth()));
		}
		for (int i = 0; i < spacesBetween.size() - 1; i++) {
			Integer space1 = spacesBetween.get(i);
			Integer space2 = spacesBetween.get(i + 1);
			assertEquals(space1, space2);
			assertTrue(space1 > 0);
		}
	}

	protected void showPanel(JPanel panel, TestInfo testInfo) {
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.setSize(new Dimension(800, 600));
		frame.setVisible(true);
		saveToFile(frame, testInfo);
	}

	protected void saveToFile(JFrame frame, TestInfo testInfo) {
		try {
			new File("target", "screens").mkdirs();
			BufferedImage image = new BufferedImage(frame.getWidth(),
					frame.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = image.createGraphics();
			frame.paint(graphics2D);
			ImageIO.write(image, "jpeg", new File(
					"target/screens/" + testInfo.getDisplayName() + ".jpeg"));
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
