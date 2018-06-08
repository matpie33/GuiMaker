package com.guimaker.utilities;

public class MathUtils {

	public static double sum(double... values) {
		double sum = 0;
		for (double d : values) {
			sum += d;
		}
		return sum;
	}

	public static int numberOfZeroElements(double... values) {
		int numberOfZeroElements = 0;
		for (double value : values) {
			if (value == 0) {
				numberOfZeroElements++;
			}
		}
		return numberOfZeroElements;
	}

}
