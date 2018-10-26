package com.guimaker.webPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.model.WebContext;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.Prompts;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ConnectionFailMessagePage implements ConnectionFailPageHandler {

	private MainPanel messagePanel;
	private JTextComponent messageComponent;

	public ConnectionFailMessagePage(AbstractButton buttonReload) {
		messagePanel = new MainPanel(new PanelConfiguration().setColorToUse(
				BasicColors.BLUE_DARK_3));
		messageComponent = GuiElementsCreator.createTextPane(
				new TextPaneOptions().
											 text(Prompts.CONNECTION_ERROR)
									 .fontSize(20)
									 .textAlignment(TextAlignment.CENTERED)
									 .editable(false));
		messagePanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				messageComponent));
		messagePanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						buttonReload));

	}

	@Override
	public JPanel getConnectionFailPage() {
		return messagePanel.getPanel();

	}

	@Override
	public void modifyConnectionFailPage(WebContext context) {
		return;
	}
}
