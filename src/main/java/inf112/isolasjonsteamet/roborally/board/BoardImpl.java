package inf112.isolasjonsteamet.roborally.board;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
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

		List<List<List<Tile>>> accX = new ArrayList<>(width);

		var linesList = board.lines().collect(Collectors.toList());
		for (int i = linesList.size() - 1; i >= 0; i--) {
			String line = linesList.get(i);
			List<List<Tile>> accY = new ArrayList<>(height);

			for (char c : line.toCharArray()) {
				accY.add(immutableCharMap.get(c));
			}

			accX.add(ImmutableList.copyOf(accY));
		}

		return ImmutableList.copyOf(accX);
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
		if (tiles.size() <= pos.getY()) {
			return ImmutableList.of(Tiles.HOLE);
		}

		List<List<Tile>> tilesX = tiles.get(pos.getY());

		if (tilesX.size() <= pos.getX()) {
			return ImmutableList.of(Tiles.HOLE);
		}

		return tilesX.get(pos.getX());
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
}
