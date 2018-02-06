package com.guimaker.options;

public class TextAreaOptions extends AbstractTextComponentOptions<TextAreaOptions> {

	private boolean wrapStyleWord = true;
	private boolean lineWrap = true;
	private boolean moveToNextComponentWhenTabbed = true;

	public boolean isMoveToNextComponentWhenTabbed() {
		return moveToNextComponentWhenTabbed;
	}

	public TextAreaOptions moveToNextComponentWhenTabbed(boolean moveToNextComponentWhenTabbed) {
		this.moveToNextComponentWhenTabbed = moveToNextComponentWhenTabbed;
		return getThis();
	}

	public boolean isWrapStyleWord() {
		return wrapStyleWord;
	}

	public TextAreaOptions wrapStyleWord(boolean wrapStyleWord) {
		this.wrapStyleWord = wrapStyleWord;
		return getThis();
	}

	public boolean isLineWrap() {
		return lineWrap;
	}

	public TextAreaOptions lineWrap(boolean lineWrap) {
		this.lineWrap = lineWrap;
		return getThis();
	}

	@Override public TextAreaOptions getThis() {
		return this;
	}

}
