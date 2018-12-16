package com.guimaker.utilities;

import java.util.List;

public class MathUtils {

	public static double sum(double... values) {
		double sum = 0;
		for (double d : values) {
			sum += d;
		}
		return sum;
	}

	public static double average(List<Double> values) {
		double average = 0;
		for (double value : values) {
			average += value;
		}
		return average / (double) values.size();
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
