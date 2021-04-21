package inf112.isolasjonsteamet.roborally.board;

import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Interface for classes handling boards.
 */
public interface Board {

	/**
	 * Get a list of the robots on the Board.
	 */
	List<Robot> getRobots();

	/**
	 * Get robot at a given position.
	 */
	@Nullable
	Robot getRobotAt(Coordinate pos);

	/**
	 * Get tiles at a given position.
	 */
	List<Tile> getTilesAt(Coordinate pos);

	/**
	 * Get width of board.
	 *
	 * @return width
	 */
	int getWidth();

	/**
	 * Get height of the board.
	 *
	 * @return height
	 */
	int getHeight();

	/**
	 * Check if the board is in a valid state.
	 */
	void checkValid();

	/**
	 * Updates the list of active robots still on the board.
	 */
	void updateActiveRobots(List<Robot> robots);

	/*
	 * Processes all the laser tiles on this board and damages the players that stand in their way.
	 */
	void fireLaser();
}
