package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Board class that will create and manipulate boards.
 */
public class BoardImpl implements Board {

	public TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer playerLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;

	/**
	 * Create a new board by creating a new instance of BoardImpl. * @param boardName
	 */
	public BoardImpl(String boardName) {
		map = new TmxMapLoader().load(boardName);

		boardLayer = (TiledMapTileLayer) map.getLayers().get("Board");
		holeLayer = (TiledMapTileLayer) map.getLayers().get("Hole");
		flagLayer = (TiledMapTileLayer) map.getLayers().get("Flag");
		playerLayer = (TiledMapTileLayer) map.getLayers().get("Player");
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
