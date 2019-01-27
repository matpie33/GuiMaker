package com.guimaker.webPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WebPageActions {

	private WebPagePanel webPagePanel;
	private final static String CREATE_TOOLTIP_JS = "createTooltip.js";
	private final static String CALL_ENGLISH_DICTIONARY = "callEnglishDictionary.js";

	private EnglishDictionaryCaller englishDictionaryCaller;

	public WebPageActions(WebPagePanel webPagePanel) {
		this.webPagePanel = webPagePanel;
		englishDictionaryCaller = new EnglishDictionaryCaller();
	}

	public String getJavascriptFileForShowingTooltip() {
		return CREATE_TOOLTIP_JS;
	}

	public AbstractAction createActionCallEnglishDictionary() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				webPagePanel.addJavaObjectReferenceForJavascript(
						englishDictionaryCaller);
				webPagePanel.executeJavascriptFiles(CALL_ENGLISH_DICTIONARY,
						CREATE_TOOLTIP_JS);

			}
		};
	}

}
