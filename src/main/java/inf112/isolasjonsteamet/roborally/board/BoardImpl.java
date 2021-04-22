package inf112.isolasjonsteamet.roborally.board;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.tiles.WallTileType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Board class that will create and manipulate boards.
 */
public class BoardImpl implements Board {

	protected List<Robot> robots = ImmutableList.of();
	protected List<List<List<Tile>>> tiles;

	protected int width;
	protected int height;

	/**
	 * New instance of BoardImpl.
	 */
	protected BoardImpl(List<List<List<Tile>>> tiles) {
		this.tiles = tiles;
		height = tiles.size();
		width = tiles.isEmpty() ? 0 : tiles.get(0).size();
	}

	public BoardImpl(Map<Character, List<Tile>> charMap, String board) {
		this(tilesFromString(charMap, board));
	}

	/**
	 * Return width of the board.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return height of the board.
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public boolean hasWallInDir(Coordinate coord, Orientation dir) {
		var coordTiles = new ArrayList<>(getTilesAt(coord));
		var nextCoordTiles = new ArrayList<>(getTilesAt(coord.add(dir.toCoord())));

		var dirWalls = getWallTypesForDir(dir);
		var oppositeDirWalls = getWallTypesForDir(dir.getOpposingDir());

		return coordTiles.removeAll(dirWalls) || nextCoordTiles.removeAll(oppositeDirWalls);
	}

	private List<WallTileType> getWallTypesForDir(Orientation dir) {
		List<WallTileType> allWallTypes = WallTileType.ALL_WALL_TYPES;
		List<WallTileType> wallTypes = new ArrayList<>();

		for (WallTileType type : allWallTypes) {
			if (type.hasWallInDir(dir)) {
				wallTypes.add(type);
			}
		}
		return wallTypes;
	}

	/**
	 * Get tiles from string.
	 *
	 * @return list
	 */
	private static List<List<List<Tile>>> tilesFromString(Map<Character, List<Tile>> charMap, String board) {
		int width = board.split("\n", 2)[0].length();
		int height = (int) board.lines().count();
		Map<Character, List<Tile>> immutableCharMap = new HashMap<>();
		charMap.forEach((k, v) -> immutableCharMap.put(k, ImmutableList.copyOf(v)));

		List<List<List<Tile>>> accY = new ArrayList<>(height);
		var lines = board.lines().collect(Collectors.toList());
		for (int i = lines.size() - 1; i >= 0; i--) {
			var line = lines.get(i);
			List<List<Tile>> accX = new ArrayList<>(width);

			for (char c : line.toCharArray()) {
				accX.add(immutableCharMap.get(c));
			}

			accY.add(ImmutableList.copyOf(accX));
		}

		return ImmutableList.copyOf(accY);
	}

	@Override
	public List<Robot> getRobots() {
		return this.robots;
	}

	@Nullable
	@Override
	public Robot getRobotAt(Coordinate pos) {
		return robots.stream().filter(p -> p.getPos().equals(pos)).findAny().orElse(null);
	}

	/**
	 * Get tiles at a given position.
	 */
	@Override
	public List<Tile> getTilesAt(Coordinate pos) {
		if (tiles.size() <= pos.getY() || pos.getY() < 0) {
			return ImmutableList.of(Tiles.HOLE);
		}

		List<List<Tile>> tilesY = tiles.get(pos.getY());

		if (tilesY.size() <= pos.getX() || pos.getX() < 0) {
			return ImmutableList.of(Tiles.HOLE);
		}

		return tilesY.get(pos.getX());
	}
	
	/**
	 * {@inheritDoc}.
	 */
	public void checkValid() {
		for (Robot robot : robots) {
			int x = robot.getPos().getX();
			int y = robot.getPos().getY();

			if (x < 0 || x >= width) {
				throw new IllegalStateException("Robot out of bounds " + robot.getPos());
			}

			if (y < 0 || y >= height) {
				throw new IllegalStateException("Robot out of bounds " + robot.getPos());
			}
		}
	}

	@Override
	public void updateActiveRobots(List<Robot> robots) {
		this.robots = ImmutableList.copyOf(robots);
	}

	private boolean hasRobotInWayOfEmitter(Coordinate pos) {
		Coordinate originalPos = pos;

		boolean hasFoundEmitter = false;

		for (Orientation dir : Orientation.values()) {
			pos = originalPos;
			boolean hasEncounteredRobot = false;

			while (true) {
				pos = pos.add(dir.toCoord());

				var tiles = getTilesAt(pos);
				hasFoundEmitter = hasFoundEmitter || tiles.contains(Tiles.LASER_EMITTER);
				if (!tiles.contains(Tiles.LASER) && !tiles.contains(Tiles.LASER_EMITTER)) {
					break;
				}

				if (getRobotAt(pos) != null) {
					hasEncounteredRobot = true;
				}

				if (tiles.contains(Tiles.LASER_EMITTER)) {
					if (!hasEncounteredRobot) {
						return false;
					}

					break;
				}
			}
		}
		return hasFoundEmitter;
	}

	@Override
	public void fireLaser() {
		for (Robot robot : robots) {
			var tiles = getTilesAt(robot.getPos());

			if (tiles.contains(Tiles.LASER_EMITTER)) {
				robot.damageRobot();
			} else if (tiles.contains(Tiles.LASER) && !hasRobotInWayOfEmitter(robot.getPos())) {
				robot.damageRobot();
			}
		}
	}
}




