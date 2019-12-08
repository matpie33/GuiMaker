package from.scratch;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		SwiftPanel swiftPanel = new SwiftPanel();
		swiftPanel.addElements(
				new PanelRows(new JButton("hi"), new JButton("Test"))
						.nextRow(new JButton("Test")));
		frame.setContentPane(swiftPanel.getPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
