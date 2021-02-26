package inf112.isolasjonsteamet.roborally.util;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * A orientation of something, indicating which way it's pointing.
 */
public enum Orientation {
	NORTH, SOUTH, EAST, WEST;

	public Orientation opposingDir;

	public Orientation getOpposingDir() {
		return opposingDir;
	}

	static {
		WEST.opposingDir = EAST;
		EAST.opposingDir = WEST;
		NORTH.opposingDir = SOUTH;
		SOUTH.opposingDir = NORTH;
	}

	public Orientation rotateLeft(Orientation currentDir) {
		switch (currentDir) {
			case NORTH:
				return WEST;
			case WEST:
				return SOUTH;
			case SOUTH:
				return EAST;
			case EAST:
				return NORTH;
			default:
				return currentDir;
		}
	}

	public Orientation rotateRight(Orientation currentDir) {
		switch (currentDir) {
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				return currentDir;
		}
	}
}
