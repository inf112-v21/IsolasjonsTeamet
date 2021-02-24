package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import java.util.List;
import javax.annotation.Nullable;

public class BoardImpl implements Board{

	public BoardImpl(){

	}

	/**
	 * Get a list of the Players on the Board.
	 *
	 * @return playerList
	 */
	@Override
	public List<Player> getPlayers() {
		return null;
	}

	@Nullable
	@Override
	public Player getPlayerAt(Vector2 pos) {
		return null;
	}

	/**
	 * Get tiles at a given position.
	 */
	@Override
	public List<TileType> getTilesAt(Vector2 pos) {
		return null;
	}

}
