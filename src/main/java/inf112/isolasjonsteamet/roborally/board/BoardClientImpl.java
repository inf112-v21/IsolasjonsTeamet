package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class BoardClientImpl extends BoardImpl implements ClientBoard {

	private final TiledMap map;
	public TiledMapTileLayer boardLayer;
	public TiledMapTileLayer holeLayer;
	public TiledMapTileLayer flagLayer;

	public TiledMapTileLayer.Cell transparentCell;

	private final List<BoardRobot> boardRobots = new ArrayList<>();

	/**
	 * Create a new instance of BoardClientImpl.
	 */
	public BoardClientImpl(List<Robot> robots, String boardFilename) {
		super(robots, new ArrayList<>());
		map = new TmxMapLoader().load(boardFilename);
		loadCells();
		tiles = tilesFromMap();
		height = boardLayer.getHeight();
		width = boardLayer.getWidth();
	}

	/**
	 * Load cells needed to build the board.
	 */
	private void loadCells() {
		MapLayers layers = map.getLayers();

		boardLayer = (TiledMapTileLayer) layers.get("Board");
		holeLayer = (TiledMapTileLayer) layers.get("Hole");
		flagLayer = (TiledMapTileLayer) layers.get("Flag");

		var tileSize = getTextureTileSize();

		Texture robotTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(robotTx, tileSize, tileSize);

		Texture defTx = new Texture("tiles.png");
		final TextureRegion[][] tReg2 = TextureRegion.split(defTx, tileSize, tileSize);

		//Static tiles for playing, dead and winning robot cells
		var transparentTile = new StaticTiledMapTile(tReg2[15][4]);
		transparentCell = new TiledMapTileLayer.Cell().setTile(transparentTile);

		var robotTile = new StaticTiledMapTile(tReg[0][0]);
		var robotWonTile = new StaticTiledMapTile(tReg[0][2]);
		var robotDiedTile = new StaticTiledMapTile(tReg[0][1]);

		int width = boardLayer.getWidth();
		int height = boardLayer.getHeight();
		int tileWidth = boardLayer.getTileWidth();
		int tileHeight = boardLayer.getTileHeight();

		for (Robot robot : robots) {
			var robotCell = new Cell().setTile(robotTile);
			var robotWonCell = new Cell().setTile(robotWonTile);
			var robotDiedCell = new Cell().setTile(robotDiedTile);
			var layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
			layers.add(layer);

			boardRobots.add(new BoardRobot(robot, robotCell, robotWonCell, robotDiedCell, layer));
		}
	}

	/**
	 * Get tiles from the map.
	 */
	private ImmutableList<List<List<TileType>>> tilesFromMap() {
		int width = boardLayer.getWidth();
		int height = boardLayer.getHeight();

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
		for (BoardRobot robot : boardRobots) {
			robot.draw();
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

	private class BoardRobot {

		private final Robot robot;
		private final TiledMapTileLayer.Cell robotCell;
		private final TiledMapTileLayer.Cell robotWonCell;
		private final TiledMapTileLayer.Cell robotDiedCell;
		private final TiledMapTileLayer layer;

		private BoardRobot(
				Robot robot,
				Cell robotCell,
				Cell robotWonCell,
				Cell robotDiedCell,
				TiledMapTileLayer layer
		) {
			this.robot = robot;
			this.robotCell = robotCell;
			this.robotWonCell = robotWonCell;
			this.robotDiedCell = robotDiedCell;
			this.layer = layer;
		}

		private void draw() {
			clearLayer(layer);

			Coordinate pos = robot.getPos();
			Orientation dir = robot.getDir();

			TiledMapTileLayer.Cell cell;

			if (robot.checkWinCondition(BoardClientImpl.this)) {
				cell = robotWonCell;
			} else if (robot.checkLossCondition(BoardClientImpl.this)) {
				cell = robotDiedCell;
			} else {
				cell = robotCell;
			}

			layer.setCell(pos.getX(), pos.getY(), cell);

			int rotation = orientationToCellRotation(dir);
			cell.setRotation(rotation);
		}

		private int orientationToCellRotation(Orientation orientation) {
			return switch (orientation) {
				case NORTH -> 0;
				case WEST -> 1;
				case SOUTH -> 2;
				case EAST -> 3;
			};
		}
	}
}
