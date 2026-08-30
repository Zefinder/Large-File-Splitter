package com.logic;

public enum SplitSize {
	KB(1024), MB(1024 * 1024), GB(1024 * 1024 * 1024);

	private long multiplier;

	private SplitSize(long multiplier) {
		this.multiplier = multiplier;
	}

	public long getMultiplier() {
		return multiplier;
	}
}
