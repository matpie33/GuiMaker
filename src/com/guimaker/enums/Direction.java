package com.guimaker.enums;

public enum Direction {
	FORWARD(1), BACKWARD(-1);
	private int decrementValue;

	Direction(int decrementValue) {
		this.decrementValue = decrementValue;
	}

	public int getDecrementValue() {
		return decrementValue;
	}
}
