package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
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
	 * Get player at a given osition.
	 */
	@Nullable
	Player getPlayerAt(Vector2 pos);

	/**
	 * Get tiles at a given position.
	 */
	List<TileType> getTilesAt(Vector2 pos);
}
