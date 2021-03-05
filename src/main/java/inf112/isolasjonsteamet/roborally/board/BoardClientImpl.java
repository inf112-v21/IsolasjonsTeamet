package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class BoardClientImpl extends BoardImpl {

	public TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer playerLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;

	public TiledMapTileLayer.Cell playerWonCell;
	public TiledMapTileLayer.Cell playerDiedCell;
	public TiledMapTileLayer.Cell playerCell;
	public TiledMapTileLayer.Cell transparentCell;

	/**
	 * Create a new instance of BoardClientImpl.
	 */
	public BoardClientImpl(List<Player> players, String boardFilename) {
		super(players, new ArrayList<>());
		map = new TmxMapLoader().load(boardFilename);
		loadCells();
		tiles = tilesFromMap(boardFilename);
		height = playerLayer.getHeight();
		width = playerLayer.getWidth();
	}

	/**
	 * Load cells needed to build the board.
	 */
	private void loadCells() {
		boardLayer = (TiledMapTileLayer) map.getLayers().get("Board");
		playerLayer = (TiledMapTileLayer) map.getLayers().get("Player");
		holeLayer = (TiledMapTileLayer) map.getLayers().get("Hole");
		flagLayer = (TiledMapTileLayer) map.getLayers().get("Flag");

		Texture playerTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(playerTx, 300, 300);

		Texture defTx = new Texture("tiles.png");
		final TextureRegion[][] tReg2 = TextureRegion.split(defTx, 300, 300);

		//Static tiles for playing, dead and winning player cells
		var transparentTile = new StaticTiledMapTile(tReg2[15][4]);
		transparentCell = new TiledMapTileLayer.Cell().setTile(transparentTile);

		var playerTile = new StaticTiledMapTile(tReg[0][0]);
		playerCell = new TiledMapTileLayer.Cell().setTile(playerTile);

		var playerWonTile = new StaticTiledMapTile(tReg[0][2]);
		playerWonCell = new TiledMapTileLayer.Cell().setTile(playerWonTile);

		var playerDiedTile = new StaticTiledMapTile(tReg[0][1]);
		playerDiedCell = new TiledMapTileLayer.Cell().setTile(playerDiedTile);
	}

	/**
	 * Get tiles from the map.
	 */
	private ImmutableList<List<List<TileType>>> tilesFromMap(String boardFilename) {
		int width = playerLayer.getWidth();
		int height = playerLayer.getHeight();

		List<List<List<TileType>>> accX = new ArrayList<>(width);
		for (int x = 0; x < width; x++) {
			List<List<TileType>> accY = new ArrayList<>(height);
			for (int y = 0; y < height; y++) {
				List<TileType> acc = new ArrayList<>();

				if (boardLayer.getCell(x, y) != null) {
					acc.add(Tiles.GROUND);
				}

				if (holeLayer.getCell(x, y) != null) {
					acc.add(Tiles.HOLE);
				}

				if (flagLayer.getCell(x, y) != null) {
					acc.add(Tiles.FLAG);
				}

				accY.add(ImmutableList.copyOf(acc));
			}
			accX.add(ImmutableList.copyOf(accY));
		}

		return ImmutableList.copyOf(accX);
	}

	/**
	 * Update the playerview.
	 */
	public void updatePlayerView() {
		for (Player player : players) {
			Coordinate pos = player.getPos();
			playerLayer.setCell(pos.getX(), pos.getX(), playerCell);
		}
	}
}
