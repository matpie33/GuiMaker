package com.guimaker.utilities;

import java.io.Serializable;

public class Pair<Left, Right> implements Serializable {

	private static final long serialVersionUID = -8791119012067367819L;
	private Left left;
	private Right right;

	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}

	public Left getLeft() {
		return left;
	}

	public Right getRight() {
		return right;
	}

	public static <A, B> Pair of(A left, B right) {
		return new Pair<>(left, right);
	}

}
