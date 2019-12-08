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
		PanelRows panelRows = new PanelRows(new JButton("Test"),
				new JLabel("Test label"),
				new JButton("Test button")).withFillType(FillType.HORIZONTAL);

		//then
		assertEquals(FillType.HORIZONTAL, panelRows.getFillType());
	}

	@Test
	void shouldCreate2Rows() {
		//given
		JComponent[] componentsRow1 = { new JButton("B1"), new JLabel("L1") };
		List<JComponent> row1 = Arrays.asList(componentsRow1);
		JComponent[] componentsRow2 = { new JButton("B2"), new JLabel("L2") };
		List<JComponent> row2 = Arrays.asList(componentsRow2);

		//when
		PanelRows panelRows = new PanelRows(componentsRow1).withFillType(FillType.HORIZONTAL)
										   .nextRow(componentsRow2)
										   .withFillType(FillType.VERTICAL);

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