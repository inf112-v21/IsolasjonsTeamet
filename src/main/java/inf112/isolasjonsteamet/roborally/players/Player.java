package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.util.Coords;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * A player on the board.
 */
public interface Player {

	/**
	 * Get the position of a player.
	 * @return
	 */
	Coords getPos();

	/**
	 * Sets the position of a player.
	 * @param c
	 */
	void setPos(Coords c);

	/**
	 * Move the player on the board.
	 * @param amount
	 */
	void move(Vector2 amount);

	/**
	 * Returns the name of the player
	 * @return
	 */
	String getName();
	Orientation getDir();

	void setDir(Orientation dir);

	void setMovDir(Orientation movingDir);

	void setNextPos(Coords c);
}
