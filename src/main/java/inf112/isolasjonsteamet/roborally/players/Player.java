package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * A player on the board.
 */
public interface Player {

	/**
	 * Get the position of a player.
	 */
	Coordinate getPos();

	/**
	 * Move the player on the board.
	 */
	void move(Coordinate offset);

	/**
	 * Returns the name of the player.
	 */
	String getName();

	/**
	 * Gets direction of the player.
	 *
	 * @return direction
	 */
	Orientation getDir();

	/**
	 * Sets direction of the player.
	 */
	void setDir(Orientation dir);
}