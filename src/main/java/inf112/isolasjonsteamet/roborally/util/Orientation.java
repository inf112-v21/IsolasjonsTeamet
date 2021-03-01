package inf112.isolasjonsteamet.roborally.util;

/**
 * A orientation of something, indicating which way it's pointing.
 */
public enum Orientation {
	NORTH, SOUTH, EAST, WEST;

	public Orientation getOpposingDir() {
		return switch (this) {
			case WEST -> EAST;
			case EAST -> WEST;
			case NORTH -> SOUTH;
			case SOUTH -> NORTH;
		};
	}

	public Orientation rotateLeft() {
		return switch (this) {
			case NORTH -> WEST;
			case WEST -> SOUTH;
			case SOUTH -> EAST;
			case EAST -> NORTH;
		};
	}

	public Orientation rotateRight() {
		return switch (this) {
			case NORTH -> EAST;
			case EAST -> SOUTH;
			case SOUTH -> WEST;
			case WEST -> NORTH;
		};
	}

	public Coordinate toCoord() {
		return switch (this) {
			case NORTH -> Coordinate.NORTH;
			case EAST -> Coordinate.EAST;
			case WEST -> Coordinate.WEST;
			case SOUTH -> Coordinate.SOUTH;
		};
	}
}
