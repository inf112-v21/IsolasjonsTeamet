package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.effects.Effect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.tiles.WallTileType;
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
	private TiledMapTileLayer wallLayer;
	private MapLayers layers;

	private StaticTiledMapTile robotTile;
	private StaticTiledMapTile robotWonTile;
	private StaticTiledMapTile robotDiedTile;

	private final List<BoardRobot> boardRobots = new ArrayList<>();

	private final List<Effect> effects = new ArrayList<>();

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

		var wallLayerRaw = map.getLayers().get("Wall");
		if (wallLayerRaw == null) {
			int width = boardLayer.getWidth();
			int height = boardLayer.getHeight();
			int tileWidth = boardLayer.getTileWidth();
			int tileHeight = boardLayer.getTileHeight();
			wallLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		} else {
			wallLayer = (TiledMapTileLayer) wallLayerRaw;
		}

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
	@SuppressWarnings("checkstyle:Indentation")
	private ImmutableList<List<List<Tile>>> tilesFromMap() {
		int width = boardLayer.getWidth();
		int height = boardLayer.getHeight();

		List<List<List<Tile>>> accY = new ArrayList<>(height);
		for (int y = 0; y < height; y++) {
			List<List<Tile>> accX = new ArrayList<>(width);
			for (int x = 0; x < width; x++) {
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
						case 32 -> new WallTileType(false, true, true, false);

						case 23 -> new WallTileType(false, false, false, true);
						case 31 -> new WallTileType(true, false, false, false);
						case 30 -> new WallTileType(false, true, false, false);
						case 29 -> new WallTileType(false, false, true, false);

						case 37 -> new WallTileType(false, false, true, false);
						case 38 -> new WallTileType(false, true, false, false);
						case 45 -> new WallTileType(true, false, false, false);
						case 46 -> new WallTileType(false, false, false, true);

						case 87 -> new WallTileType(false, false, true, false);
						case 93 -> new WallTileType(false, true, false, false);
						case 94 -> new WallTileType(true, false, false, false);
						case 95 -> new WallTileType(false, false, false, true);

						default -> null;
					};

					if (tileType != null) {
						acc.add(tileType);
					}
				}

				accX.add(ImmutableList.copyOf(acc));
			}
			accY.add(ImmutableList.copyOf(accX));
		}

		return ImmutableList.copyOf(accY);
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

	@Override
	public int getTextureRenderSize() {
		return 100;
	}

	@Override
	public void show(Robot robot) {
		for (BoardRobot boardRobot : boardRobots) {
			if (boardRobot.robot.equals(robot)) {
				layers.add(boardRobot.layer);
			}
		}
	}

	@Override
	public void hide(Robot robot) {
		for (BoardRobot boardRobot : boardRobots) {
			if (boardRobot.robot.equals(robot)) {
				layers.remove(boardRobot.layer);
			}
		}
	}

	@Override
	public void addEffect(Effect effect) {
		effects.add(effect);
	}

	@Override
	public void removeEffect(Effect effect) {
		effects.remove(effect);
	}

	@Override
	public void renderEffects(Batch batch) {
		for (Effect effect : effects) {
			effect.render(batch);
		}
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
