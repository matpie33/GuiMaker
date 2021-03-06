package com.guimaker.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Message panel";
	private String message;
	protected AbstractButton buttonClose;

	public MessagePanel(String message) {
		this.message = message;
	}

	@Override
	public void createElements() {
		buttonClose = createButtonClose();
		JScrollPane scrollPane = GuiElementsCreator.createTextPaneWrappedInScrollPane(
				// TODO add vertical alignment
				new TextPaneOptions().backgroundColor(
						getDialog().getParentConfiguration()
								   .getContentPanelColor())
									 .textAlignment(TextAlignment.JUSTIFIED)
									 .text(message)
									 .preferredSize(new Dimension(200, 100))
									 .editable(false),
				new ScrollPaneOptions().opaque(false));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, scrollPane));
		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

}
