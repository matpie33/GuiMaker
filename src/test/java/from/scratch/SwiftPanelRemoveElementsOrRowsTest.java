package from.scratch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;

public class SwiftPanelRemoveElementsOrRowsTest extends AbstractPanelTest {

	private SwiftPanel swiftPanel = new SwiftPanel();

	@DisplayName("Remove element from normal row")
	@Test
	public void shouldRemoveElementFromNormalRow(TestInfo testInfo) {

		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test labelll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test row 2");
		JButton row2Element2 = new JButton("Test button");

		PanelRow panelRow = new PanelRow(row1Element1, row1Element2,
				row1Element3).nextRow(row2Element1, row2Element2);

		//when
		swiftPanel.addElements(panelRow);
		JPanel panel = swiftPanel.getPanel();
		swiftPanel.removeElement(row1Element2);
		swiftPanel.removeElement(row2Element2);
		showPanel(panel, testInfo);

		//then
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row1Element1, row1Element3);
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row2Element1);
		//noinspection unchecked
		assertDistancesBetweenRows(panel, false,
				Arrays.asList(row1Element1, row1Element3),
				Collections.singletonList(row2Element1));
		assertElementsNotFilledHorizontally(panel, row1Element1, row1Element3,
				row2Element1);
		Assertions.assertTrue(!row1Element2.isShowing());
		Assertions.assertTrue(!row2Element2.isShowing());
		Assertions.assertTrue(row1Element1.isShowing());

	}

	@DisplayName("Remove element from column layout")
	@Test
	public void shouldRemoveElementFromRowKeepingColumnSize(TestInfo testInfo) {

		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test labelll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test row 2");
		JButton row2Element2 = new JButton("Test button");

		PanelRow panelRow = new PanelRow(row1Element1, row1Element2,
				row1Element3).nextRowKeepingColumnSize(row2Element1,
				row2Element2);

		//when
		swiftPanel.addElements(panelRow);
		JPanel panel = swiftPanel.getPanel();
		swiftPanel.removeElement(row1Element2);
		showPanel(panel, testInfo);

		//then
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row2Element1, row2Element2);
		//noinspection unchecked
		assertDistancesBetweenRows(panel, false,
				Arrays.asList(row1Element1, row1Element3),
				Arrays.asList(row2Element1, row2Element2));
		assertElementsNotFilledHorizontally(panel, row1Element1, row1Element3,
				row2Element1, row2Element2);
		Assertions.assertTrue(!row1Element2.isShowing());
		Assertions.assertTrue(row1Element1.isShowing());
		assertSameXPositions(row1Element1, row2Element1);
		assertSameXPositions(row1Element3, row2Element2);
		assertSameYPositions(row1Element1, row1Element3);
		assertSameYPositions(row2Element1, row2Element2);

	}

	@DisplayName("Remove from col layout after add")
	@Test
	public void shouldRemoveElementFromRowKeepingColumnSizeAddedLater(
			TestInfo testInfo) {

		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test labelll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test row 2");
		JButton row2Element2 = new JButton("Test button");

		PanelRow panelRow = new PanelRow(row1Element1, row1Element2,
				row1Element3).nextRow(row2Element1, row2Element2);

		JButton row3Element1 = new JButton("Row 31");
		JButton row3Element2 = new JButton("Row 333333");
		JButton row3Element3 = new JButton("Rororo");
		JButton row3Element4 = new JButton("Roronoa zoro");
		PanelRow newRow = new PanelRow(row3Element1, row3Element2, row3Element3,
				row3Element4).keepColumnSizeWithRowAbove();

		//when
		swiftPanel.addElements(panelRow);
		swiftPanel.addElements(newRow);
		JPanel panel = swiftPanel.getPanel();
		swiftPanel.removeElement(row3Element2);
		showPanel(panel, testInfo);

		//then
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row1Element1, row1Element2, row1Element3);
		assertDistanceBetweenElementsHorizontally(panel, panel, false,
				row2Element1, row2Element2);
		//noinspection unchecked
		assertDistancesBetweenRows(panel, false,
				Arrays.asList(row1Element1, row1Element3),
				Arrays.asList(row2Element1, row2Element2),
				Arrays.asList(row3Element1,row3Element3, row3Element4));
		assertElementsNotFilledHorizontally(panel, row1Element1,
				row1Element2, row1Element3,
				row2Element1, row2Element2, row3Element1,row3Element3,row3Element4);
		Assertions.assertTrue(!row3Element2.isShowing());
		assertSameXPositions(row2Element1,row3Element1);
		assertSameXPositions(row2Element2, row3Element3);

	}

}
