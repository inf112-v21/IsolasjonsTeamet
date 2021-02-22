package inf112.isolasjonsteamet.roborally.util;

/**
 * A orientation of something, indicating which way it's pointing.
 */
public enum Orientation {
	NORTH, SOUTH, EAST, WEST;

	public Orientation opposingDir;

	public Orientation getOpposingDir(){
		return opposingDir;
	}

	static {
		WEST.opposingDir = EAST;
		EAST.opposingDir = WEST;
		NORTH.opposingDir = SOUTH;
		SOUTH.opposingDir = NORTH;
	}

}
