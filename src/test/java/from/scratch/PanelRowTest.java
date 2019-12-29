package from.scratch;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelRowTest {

	@Test
	void shouldSetFillTypeAndComponents() {
		//given

		//when
		JButton button = new JButton("Test button");
		PanelRow panelRow = new PanelRow(new JButton("Test"),
				new JLabel("Test label"), button).fillElement(
				FillType.HORIZONTAL, button);

		//then
		assertEquals(FillType.HORIZONTAL, panelRow.getElementsFillType());
	}

	@Test
	void shouldCreate2Rows() {
		//given
		JButton button = new JButton("B1");
		JComponent[] componentsRow1 = { button, new JLabel("L1") };
		List<JComponent> row1 = Arrays.asList(componentsRow1);
		JButton button1 = new JButton("B2");
		JComponent[] componentsRow2 = { button1, new JLabel("L2") };
		List<JComponent> row2 = Arrays.asList(componentsRow2);

		//when
		PanelRow panelRow = new PanelRow(componentsRow1).fillElement(
				FillType.HORIZONTAL, button)
														.nextRow(componentsRow2)
														.fillElement(
																FillType.VERTICAL,
																button1);

		//then
		assertTrue(panelRow.getPreviousRow() != null);
		assertEquals(FillType.HORIZONTAL, panelRow.getPreviousRow()
												  .getElementsFillType());
		assertEquals(row1, panelRow.getPreviousRow()
								   .getUIElements());
		assertEquals(FillType.VERTICAL, panelRow.getElementsFillType());
		assertEquals(row2, panelRow.getUIElements());
	}

}