package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class ClientImpl extends BoardImpl{

	public TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer playerLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;

	/**
	 * Create a new board by creating a new instance of BoardImpl. * @param boardName
	 */
	public ClientImpl(String boardName) {
		super(boardName);

		map = new TmxMapLoader().load(boardName);

		boardLayer = (TiledMapTileLayer) map.getLayers().get("Board");
		holeLayer = (TiledMapTileLayer) map.getLayers().get("Hole");
		flagLayer = (TiledMapTileLayer) map.getLayers().get("Flag");
		playerLayer = (TiledMapTileLayer) map.getLayers().get("Player");
	}
}
