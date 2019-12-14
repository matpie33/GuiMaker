package from.scratch;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		SwiftPanel swiftPanel = new SwiftPanel();
		JButton row1Element1 = new JButton("Test");
		JLabel row1Element2 = new JLabel("Test label");
		JButton row1Element3 = new JButton("Test button");
		JButton row2Element1 = new JButton("Row2");
		JButton row2Element2 = new JButton("Row2.22");
		PanelRows panelRows = new PanelRows(row1Element1, row1Element2,
				row1Element3).nextRowKeepingColumnSize(row2Element1,
				row2Element2);
		swiftPanel.addElements(panelRows);


		frame.setContentPane(swiftPanel.getPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
