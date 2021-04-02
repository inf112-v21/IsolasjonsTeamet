package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class BoardClientImpl extends BoardImpl implements ClientBoard {

	private final TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer robotLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;

	public TiledMapTileLayer.Cell robotWonCell;
	public TiledMapTileLayer.Cell robotDiedCell;
	public TiledMapTileLayer.Cell robotCell;
	public TiledMapTileLayer.Cell transparentCell;

	/**
	 * Create a new instance of BoardClientImpl.
	 */
	public BoardClientImpl(List<Robot> robots, String boardFilename) {
		super(robots, new ArrayList<>());
		map = new TmxMapLoader().load(boardFilename);
		loadCells();
		tiles = tilesFromMap(boardFilename);
		height = robotLayer.getHeight();
		width = robotLayer.getWidth();
	}

	/**
	 * Load cells needed to build the board.
	 */
	private void loadCells() {
		boardLayer = (TiledMapTileLayer) map.getLayers().get("Board");
		robotLayer = (TiledMapTileLayer) map.getLayers().get("Player");
		holeLayer = (TiledMapTileLayer) map.getLayers().get("Hole");
		flagLayer = (TiledMapTileLayer) map.getLayers().get("Flag");

		var tileSize = getTextureTileSize();

		Texture robotTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(robotTx, tileSize, tileSize);

		Texture defTx = new Texture("tiles.png");
		final TextureRegion[][] tReg2 = TextureRegion.split(defTx, tileSize, tileSize);

		//Static tiles for playing, dead and winning robot cells
		var transparentTile = new StaticTiledMapTile(tReg2[15][4]);
		transparentCell = new TiledMapTileLayer.Cell().setTile(transparentTile);

		var robotTile = new StaticTiledMapTile(tReg[0][0]);
		robotCell = new TiledMapTileLayer.Cell().setTile(robotTile);

		var robotWonTile = new StaticTiledMapTile(tReg[0][2]);
		robotWonCell = new TiledMapTileLayer.Cell().setTile(robotWonTile);

		var robotDiedTile = new StaticTiledMapTile(tReg[0][1]);
		robotDiedCell = new TiledMapTileLayer.Cell().setTile(robotDiedTile);
	}

	/**
	 * Get tiles from the map.
	 */
	private ImmutableList<List<List<TileType>>> tilesFromMap(String boardFilename) {
		int width = robotLayer.getWidth();
		int height = robotLayer.getHeight();

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
	public void updateRobotView() {
		clearLayer(robotLayer);

		for (Robot robot : robots) {
			Coordinate pos = robot.getPos();
			robotLayer.setCell(pos.getX(), pos.getX(), robotCell);
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
