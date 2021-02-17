package com.jsganglia.server.ganglia;



public enum Gslope {

	/** Plot the points as step functions (no interpolation). */
	zero(0),
	/** Interpolate on positive value change. */
	positive(1),
	/** Interpolate on negative value change. */
	negative(2),
	/** Interpolate on any value change. */
	both(3),
	/**
	 * Used (by Ganglia) for things like <code>heartbeat</code> and
	 * <code>location</code>. (Location is reported with
	 * <code>units := (x,y,z)</code>.)
	 */
	unspecified(4);

	private Gslope(final int v) {
		this.v = v;
	}

	private final int v;

	public int value() {
		return v;
	}

	public static final Gslope valueOf(final int v) {
		switch (v) {
		case 0:
			return zero;
		case 1:
			return positive;
		case 2:
			return negative;
		case 3:
			return both;
		case 4:
			return unspecified;
		default:
			throw new IllegalArgumentException("value=" + v);
		}
	}
	
}
