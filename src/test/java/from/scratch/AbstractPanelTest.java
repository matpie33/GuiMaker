package from.scratch;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractPanelTest {

	protected int getYCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getY();
	}

	protected int getXCoordinate(Component c) {
		return (int) c.getLocationOnScreen()
					  .getX();
	}

	protected void assertCorrectHorizontalSpaces(JPanel row) {
		java.util.List<Integer> horizontalSpacesBetweenElementsInRow1 = getDistanceBetweenElementsHorizontally(
				row);

		for (int i = 0;
			 i < horizontalSpacesBetweenElementsInRow1.size() - 1; i++) {
			assertEquals(horizontalSpacesBetweenElementsInRow1.get(i + 1),
					horizontalSpacesBetweenElementsInRow1.get(i));
		}
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

	protected List<Integer> getDistanceBetweenElementsHorizontally(
			JPanel panel) {
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

	protected void showPanel(JPanel panel) {
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.setSize(new Dimension(800, 600));
		frame.setVisible(true);

		if (System.getProperty("Wait") != null
				|| System.getProperty("wait") != null) {
			try {
				Thread.sleep(1000000L);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
