package inf112.isolasjonsteamet.roborally.util;

/**
 * A orientation of something, indicating which way it's pointing.
 */
public enum Orientation {
	NORTH, SOUTH, EAST, WEST;

	/**
	 * Return the opposite direction of a given direction.
	 */
	public Orientation getOpposingDir() {
		return switch (this) {
			case WEST -> EAST;
			case EAST -> WEST;
			case NORTH -> SOUTH;
			case SOUTH -> NORTH;
		};
	}

	/**
	 * Return the direction to the left of a given direction.
	 *
	 * @return dir
	 */
	public Orientation rotateLeft() {
		return switch (this) {
			case NORTH -> WEST;
			case WEST -> SOUTH;
			case SOUTH -> EAST;
			case EAST -> NORTH;
		};
	}

	/**
	 * Return the direction to the right of a given direction.
	 *
	 * @return dir
	 */
	public Orientation rotateRight() {
		return switch (this) {
			case NORTH -> EAST;
			case EAST -> SOUTH;
			case SOUTH -> WEST;
			case WEST -> NORTH;
		};
	}

	/**
	 * Return the coordinate.
	 *
	 * @return coord
	 */
	public Coordinate toCoord() {
		return switch (this) {
			case NORTH -> Coordinate.NORTH;
			case EAST -> Coordinate.EAST;
			case WEST -> Coordinate.WEST;
			case SOUTH -> Coordinate.SOUTH;
		};
	}
}
