package from.scratch;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PanelRowsTest {

	@Test
	void shouldSetFillTypeAndComponents() {
		//given

		//when
		JButton button = new JButton("Test button");
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"), button).fillElement(
				FillType.HORIZONTAL, button);

		//then
		assertEquals(FillType.HORIZONTAL, panelRows.getFillType());
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
		PanelRows panelRows = new PanelRows(componentsRow1).fillElement(
				FillType.HORIZONTAL, button)
														   .nextRow(
																   componentsRow2)
														   .fillElement(
																   FillType.VERTICAL,
																   button1);

		//then
		assertEquals(2, panelRows.getRows()
								 .size());
		assertEquals(FillType.HORIZONTAL, panelRows.getRows()
												   .get(0)
												   .getFillType());
		assertEquals(row1, panelRows.getRows()
									.get(0)
									.getUiComponents());
		assertEquals(FillType.VERTICAL, panelRows.getRows()
												 .get(1)
												 .getFillType());
		assertEquals(row2, panelRows.getRows()
									.get(1)
									.getUiComponents());
	}

}