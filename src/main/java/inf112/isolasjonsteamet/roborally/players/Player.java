package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
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
	 * Sets the position of a player.
	 */
	void setPos(Coordinate c);

	/**
	 * Move the player on the board.
	 */
	void move(BoardClientImpl board, Coordinate playerVec, int dx, int dy);

	/**
	 * Returns the name of the player.
	 */
	String getName();

	Orientation getDir();

	void setDir(Orientation dir);

	void setMovDir(Orientation movingDir);

	void setNextPos(Coordinate c);
}
