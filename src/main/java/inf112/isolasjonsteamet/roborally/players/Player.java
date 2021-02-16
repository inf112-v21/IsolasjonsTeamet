package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.math.Vector2;

/**
 * A player on the board.
 */
public interface Player {

	Vector2 getPos();

	void move(Vector2 amount);

}
