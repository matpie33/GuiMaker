package com.guimaker.enums;

public enum MoveDirection {
	RIGHT(1), LEFT(-1), BELOW(1), ABOVE(-1);
	private int incrementValue;

	private MoveDirection(int incrementValue) {
		this.incrementValue = incrementValue;
	}

	public int getIncrementValue() {
		return incrementValue;
	}
}
