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
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for our boards that hold all the client related code (libgdbx).
 */
public class BoardClientImpl extends BoardImpl implements ClientBoard {

	private final TiledMap map;
	private TiledMapTileLayer boardLayer;
	private TiledMapTileLayer holeLayer;
	private TiledMapTileLayer flagLayer;

	private MapLayers layers;
	private StaticTiledMapTile robotTile;
	private StaticTiledMapTile robotWonTile;
	private StaticTiledMapTile robotDiedTile;

	private final List<BoardRobot> boardRobots = new ArrayList<>();

	/**
	 * Create a new instance of BoardClientImpl.
	 */
	public BoardClientImpl(String boardFilename) {
		super(new ArrayList<>());
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
		layers = map.getLayers();

		boardLayer = (TiledMapTileLayer) layers.get("Board");
		holeLayer = (TiledMapTileLayer) layers.get("Hole");
		flagLayer = (TiledMapTileLayer) layers.get("Flag");

		var tileSize = getTextureTileSize();

		Texture robotTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(robotTx, tileSize, tileSize);

		robotTile = new StaticTiledMapTile(tReg[0][0]);
		robotWonTile = new StaticTiledMapTile(tReg[0][2]);
		robotDiedTile = new StaticTiledMapTile(tReg[0][1]);

		for (Robot robot : robots) {
			boardRobots.add(makeBoardRobot(robot));
		}
	}

	private BoardRobot makeBoardRobot(Robot robot) {
		int width = boardLayer.getWidth();
		int height = boardLayer.getHeight();
		int tileWidth = boardLayer.getTileWidth();
		int tileHeight = boardLayer.getTileHeight();

		var robotCell = new Cell().setTile(robotTile);
		var robotWonCell = new Cell().setTile(robotWonTile);
		var robotDiedCell = new Cell().setTile(robotDiedTile);
		var layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		layers.add(layer);

		return new BoardRobot(robot, robotCell, robotWonCell, robotDiedCell, layer);
	}

	/**
	 * Get tiles from the map.
	 */
	private ImmutableList<List<List<Tile>>> tilesFromMap() {
		int width = boardLayer.getWidth();
		int height = boardLayer.getHeight();

		List<List<List<Tile>>> accX = new ArrayList<>(width);
		for (int x = 0; x < width; x++) {
			List<List<Tile>> accY = new ArrayList<>(height);
			for (int y = 0; y < height; y++) {
				List<Tile> acc = new ArrayList<>();

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

	@Override
	public void updateActiveRobots(List<Robot> robots) {
		super.updateActiveRobots(robots);

		var existingRobots = new ArrayList<Robot>();

		Iterator<BoardRobot> boardRobotsIt = boardRobots.iterator();
		while (boardRobotsIt.hasNext()) {
			BoardRobot boardRobot = boardRobotsIt.next();

			if (!robots.contains(boardRobot.robot)) {
				boardRobotsIt.remove();
				boardRobot.dispose();
			} else {
				existingRobots.add(boardRobot.robot);
			}
		}

		var newRobots = new ArrayList<>(robots);
		newRobots.removeAll(existingRobots);

		for (Robot newRobot : newRobots) {
			boardRobots.add(makeBoardRobot(newRobot));
		}
	}

	/**
	 * Update the playerview.
	 */
	public void act() {
		for (BoardRobot robot : boardRobots) {
			robot.act();
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

		private void act() {
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

		private void dispose() {
			layers.remove(layer);
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
