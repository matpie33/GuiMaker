package com.guimaker.utilities;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class LimitDocumentFilter extends DocumentFilter {

	private int limit;
	private boolean digitsOnly;

	public LimitDocumentFilter(int limit, boolean digitsOnly) {
		if (limit <= 0) {
			throw new IllegalArgumentException("Limit can not be <= 0");
		}
		this.digitsOnly = digitsOnly;
		this.limit = limit;
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
		int currentLength = fb.getDocument()
							  .getLength();
		int overLimit = (currentLength + text.length()) - limit - length;
		if (overLimit > 0) {
			text = text.substring(0, text.length() - overLimit);
		}
		if (digitsOnly && !text.matches("\\d+")) {
			return;
		}
		if (text.length() > 0) {
			super.replace(fb, offset, length, text, attrs);
		}
	}

}