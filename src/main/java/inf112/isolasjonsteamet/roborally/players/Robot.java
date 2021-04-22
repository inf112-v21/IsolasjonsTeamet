package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * A player on the board.
 */
public interface Robot {

	/**
	 * Get the position of a player.
	 */
	Coordinate getPos();

	/**
	 * Move the player on the board.
	 */
	void move(Coordinate offset);

	/**
	 * Gets direction of the player.
	 *
	 * @return direction
	 */
	Orientation getDir();

	/**
	 * Sets the absolute position of the robot, with no concerns for the environment.
	 */
	void setPos(Coordinate position);

	/**
	 * Sets direction of the player.
	 */
	void setDir(Orientation dir);

	/**
	 * Damage a players robot with 1 damage token.
	 */
	void damageRobot();

	/**
	 * Repair a players robot with 1 damage token.
	 */
	void repairRobot();

	/**
	 * Decrements life from player and rests damage token for robot.
	 */
	void killRobot();

	/**
	 * Checks if the player is in a winning condition. Temporarily here to bridge the graphical gap.
	 */
	boolean checkWinCondition(Board board);

	/**
	 * Checks if the player is in a loosing condition. Temporarily here to bridge the graphical gap.
	 */
	boolean checkLossCondition(Board board);

	/**
	 * A name used for debugging.
	 */
	String getDebugName();
}
