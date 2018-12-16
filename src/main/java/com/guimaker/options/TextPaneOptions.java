package com.guimaker.options;

import com.guimaker.enums.TextAlignment;

public class TextPaneOptions
		extends AbstractTextComponentOptions<TextPaneOptions> {

	private TextAlignment textAlignment;

	public TextPaneOptions textAlignment(TextAlignment alignment) {
		this.textAlignment = alignment;
		border(null);
		return getThis();
	}

	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	@Override
	public TextPaneOptions getThis() {
		return this;
	}

}
