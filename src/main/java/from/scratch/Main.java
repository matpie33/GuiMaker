package from.scratch;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		SwiftPanel swiftPanel = new SwiftPanel();
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test long labellllllllllllllll");
		JButton row1Element3 = new JButton("Test very long butttoooooooon");
		JButton row2Element1 = new JButton("Test");
		JButton row2Element2 = new JButton("Test");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRow(row2Element1, row2Element2)
							 .fillElement(FillType.HORIZONTAL, row2Element2);
		swiftPanel.addElements(panelRows);

		frame.setContentPane(swiftPanel.getPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
