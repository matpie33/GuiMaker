package from.scratch;

import java.awt.*;
import java.util.Arrays;

public enum FillType {
	HORIZONTAL(GridBagConstraints.HORIZONTAL, 1, 0), VERTICAL(
			GridBagConstraints.VERTICAL, 0, 1), NONE(GridBagConstraints.NONE, 0,
			0), BOTH(GridBagConstraints.BOTH, 1, 1);

	private int gridBagConstraintsFilling;
	private int weightX;
	private int weightY;

	FillType(int gridBagConstraintsFilling, int weightX, int weightY) {
		this.gridBagConstraintsFilling = gridBagConstraintsFilling;
		this.weightX = weightX;
		this.weightY = weightY;
	}

	public int getGridBagConstraintsFilling() {
		return gridBagConstraintsFilling;
	}

	public int getWeightX() {
		return weightX;
	}

	public int getWeightY() {
		return weightY;
	}

	public static FillType basedOnWeights(double maxWeightx,
			double maxWeighty) {
		return Arrays.stream(FillType.values())
					 .filter(fill -> fill.getWeightX() == maxWeightx)
					 .filter(fill -> fill.getWeightY() == maxWeighty)
					 .findFirst()
					 .orElseThrow(FillType::howIsThisPossible);
	}

	private static IllegalStateException howIsThisPossible() {
		return new IllegalStateException("How is this possible?");
	}
}
