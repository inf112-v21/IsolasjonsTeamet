package inf112.isolasjonsteamet.roborally.board;

import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Interface for classes handling boards.
 */
public interface Board {

	/**
	 * Get a list of the Players on the Board.
	 *
	 * @return playerList
	 */
	List<Player> getPlayers();

	/**
	 * Get player at a given position.
	 */
	@Nullable
	Player getPlayerAt(Coordinate pos);

	/**
	 * Get tiles at a given position.
	 */
	List<TileType> getTilesAt(Coordinate pos);

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
	 * Checks if there is an in wall in givven direction.
	 *
	 * @param coord1 position
	 * @param dir direction
	 *
	 * @return true if there is an wall in direction the given direction
	 */
	boolean hasWallInDir(Coordinate coord1, Orientation dir);

	/**
	 * Processes all the laser tiles on this board and damages the players that stand in their way.
	 */
	void fireLaser();
}


