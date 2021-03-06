package com.guimaker.utilities;

import java.util.ArrayList;
import java.util.List;

public class SetOfRanges {

	private List<Range> ranges;

	public SetOfRanges() {
		ranges = new ArrayList<>();
	}

	public void addRange(Range newRange) {
		boolean isModified = false;
		for (int i = 0; i < ranges.size(); i++) {
			Range rangeFromSet = ranges.get(i);
			if (rangeFromSet.includesRange(newRange)) {
				return;
			}

			if (rangeFromSet.isValueInsideRange(newRange.rangeStart)) {
				ranges.remove(rangeFromSet);
				i--;
				newRange = new Range(rangeFromSet.rangeStart,
						newRange.rangeEnd);
				isModified = true;

			}
			else if (rangeFromSet.isValueInsideRange(newRange.rangeEnd)) {
				ranges.remove(rangeFromSet);
				i--;
				newRange = new Range(newRange.rangeStart,
						rangeFromSet.rangeEnd);
				isModified = true;

			}
			else if (newRange.includesRange(rangeFromSet)) {
				ranges.remove(rangeFromSet);
				i--;
				isModified = true;
			}
			else if (newRange.isFollowedBy(rangeFromSet)) {
				ranges.remove(rangeFromSet);
				i--;
				isModified = true;
				newRange = new Range(newRange.rangeStart,
						rangeFromSet.rangeEnd);
			}
			else if (rangeFromSet.isFollowedBy(newRange)) {
				ranges.remove(rangeFromSet);
				i--;
				isModified = true;
				newRange = new Range(rangeFromSet.rangeStart,
						newRange.rangeEnd);

			}
		}
		ranges.add(newRange);
	}

	@Override
	public String toString() {
		String msg = "";
		for (int i = 0; i < ranges.size(); i++) {
			Range r = ranges.get(i);
			msg += "od " + r.rangeStart + " do " + r.rangeEnd;
			if (i < ranges.size() - 1) {
				msg += ", ";
				msg += "\n";
			}
		}
		return msg;
	}

	public List<Range> getRangesAsList() {
		return ranges;
	}

	public int sumRangeInclusive() {
		int sum = 0;
		for (Range range : ranges) {
			if (range.isEmpty()) {
				continue;
			}
			sum += range.rangeEnd - range.rangeStart + 1;
		}
		return sum;
	}

	public boolean isValueInsideThisSet(int value) {
		for (Range r : ranges) {
			if (r.isValueInsideRange(value)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		for (Range r : ranges) {
			if (!r.isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
