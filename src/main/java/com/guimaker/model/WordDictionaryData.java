package com.guimaker.model;

import javax.swing.*;

public class WordDictionaryData {

	private JSplitPane splitPaneToPutDictionaryInto;
	private String searchUrlPattern;

	public WordDictionaryData(JSplitPane splitPaneToPutDictionaryInto,
			String searchUrlPattern) {
		this.splitPaneToPutDictionaryInto = splitPaneToPutDictionaryInto;
		this.searchUrlPattern = searchUrlPattern;
	}

	public JSplitPane getSplitPaneToPutDictionaryInto() {
		return splitPaneToPutDictionaryInto;
	}

	public String getSearchUrlPattern() {
		return searchUrlPattern;
	}
}
