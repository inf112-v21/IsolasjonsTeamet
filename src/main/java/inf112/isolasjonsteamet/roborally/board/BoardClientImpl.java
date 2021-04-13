package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.tiles.WallTileType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class BoardClientImpl extends BoardImpl implements ClientBoard {

	private final TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer playerLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;
	public TiledMapTileLayer wallLayer;

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

		var wallLayerRaw = map.getLayers().get("Wall");
		if (wallLayerRaw == null) {
			int width = playerLayer.getWidth();
			int height = playerLayer.getHeight();
			int tileWidth = playerLayer.getTileWidth();
			int tileHeight = playerLayer.getTileHeight();
			wallLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		} else {
			wallLayer = (TiledMapTileLayer) wallLayerRaw;
		}

		var tileSize = getTextureTileSize();

		Texture playerTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(playerTx, tileSize, tileSize);

		Texture defTx = new Texture("tiles.png");
		final TextureRegion[][] tReg2 = TextureRegion.split(defTx, tileSize, tileSize);

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

				if (wallLayer.getCell(x, y) != null) {
					Cell cell = wallLayer.getCell(x, y);
					int id = cell.getTile().getId();

					var tileType = switch (id) {
						case 1 -> new WallTileType(true, false, false, false);
						case 2 -> new WallTileType(false, false, false, true);
						case 3 -> new WallTileType(false, false, true, false);
						case 4 -> new WallTileType(false, true, false, false);

						case 9 -> new WallTileType(true, false, false, false);
						case 10 -> new WallTileType(false, false, false, true);
						case 11 -> new WallTileType(false, false, true, false);
						case 12 -> new WallTileType(false, true, false, false);

						case 8 -> new WallTileType(false, false, true, true);
						case 16 -> new WallTileType(true, false, false, true);
						case 24 -> new WallTileType(true, true, false, false);
						case 32 -> new WallTileType(false, true,  true, false);

						case 23 -> new WallTileType(false, false,  false, true);
						case 31 -> new WallTileType(true, false,  false, false);
						case 30 -> new WallTileType(false, true,  false, false);
						case 29 -> new WallTileType(false, false,  true, false);

						case 37 -> new WallTileType(false, false,  true, false);
						case 38 -> new WallTileType(false, true,  false, false);
						case 45 -> new WallTileType(true, false,  false, false);
						case 46 -> new WallTileType(false, false,  false, true);

						case 87 -> new WallTileType(false, false,  true, false);
						case 93 -> new WallTileType(false, true,  false, false);
						case 94 -> new WallTileType(true, false,  false, false);
						case 95 -> new WallTileType(false, false,  false, true);

						default -> null;
					};

					if (tileType != null) {
						acc.add(tileType);
					}
				}

				accY.add(ImmutableList.copyOf(acc));
			}
			accX.add(ImmutableList.copyOf(accY));
		}

		return ImmutableList.copyOf(accX);
	}

	private void clearLayer(TiledMapTileLayer layer) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				layer.setCell(x, y, null);
			}
		}
	}

	/**
	 * Update the playerview.
	 */
	public void updatePlayerView() {
		clearLayer(playerLayer);

		for (Player player : players) {
			Coordinate pos = player.getPos();
			playerLayer.setCell(pos.getX(), pos.getX(), playerCell);
		}
	}

	@Override
	public TiledMap getMap() {
		return map;
	}

	@Override
	public int getTextureTileSize() {
		return 300; //TODO: Make configurable somehow
	}
}
