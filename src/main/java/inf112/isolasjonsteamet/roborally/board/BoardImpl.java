package inf112.isolasjonsteamet.roborally.board;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Board class that will create and manipulate boards.
 */
public class BoardImpl implements Board {

	protected final List<Player> players;
	protected List<List<List<TileType>>> tiles;

	protected int width;
	protected int height;

	/**
	 * New instance of BoardImpl.
	 */
	protected BoardImpl(List<Player> players, List<List<List<TileType>>> tiles) {
		this.players = ImmutableList.copyOf(players);
		this.tiles = tiles;
		width = tiles.size();
		height = tiles.isEmpty() ? 0 : tiles.get(0).size();
	}

	public BoardImpl(List<Player> players, Map<Character, List<TileType>> charMap, String board) {
		this(players, tilesFromString(charMap, board));
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
	private static List<List<List<TileType>>> tilesFromString(Map<Character, List<TileType>> charMap, String board) {
		int width = board.split("\n", 2)[0].length();
		int height = (int) board.lines().count();
		Map<Character, List<TileType>> immutableCharMap = new HashMap<>();
		charMap.forEach((k, v) -> immutableCharMap.put(k, ImmutableList.copyOf(v)));

		List<List<List<TileType>>> accX = new ArrayList<>(width);
		for (String line : board.lines().collect(Collectors.toList())) {
			List<List<TileType>> accY = new ArrayList<>(height);

			for (char c : line.toCharArray()) {
				accY.add(immutableCharMap.get(c));
			}

			accX.add(ImmutableList.copyOf(accY));
		}

		return ImmutableList.copyOf(accX);
	}

	/**
	 * Get a list of the Players on the Board.
	 *
	 * @return playerList
	 */
	@Override
	public List<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Get a player at a given pos.
	 *
	 * @return Player
	 */
	@Nullable
	@Override
	public Player getPlayerAt(Coordinate pos) {
		return players.stream().filter(p -> p.getPos().equals(pos)).findAny().orElse(null);
	}

	/**
	 * Get tiles at a given position.
	 */
	@Override
	public List<TileType> getTilesAt(Coordinate pos) {
		if (tiles.size() <= pos.getX() || pos.getX() < 0) {
			return ImmutableList.of(Tiles.HOLE);
		}

		List<List<TileType>> tilesX = tiles.get(pos.getX());

		if (tilesX.size() <= pos.getY() || pos.getY() < 0) {
			return ImmutableList.of(Tiles.HOLE);
		}

		return tilesX.get(pos.getY());
	}

	/**
	 * Check if the board is in a valid state.
	 */
	public void checkValid() {
		for (Player player : players) {
			int x = player.getPos().getX();
			int y = player.getPos().getY();

			if (x < 0 || x >= width) {
				throw new IllegalStateException("Player out of bounds " + player.getPos());
			}

			if (y < 0 || y >= height) {
				throw new IllegalStateException("Player out of bounds " + player.getPos());
			}
		}
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

				if(getPlayerAt(pos) != null) {
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
		for (Player player : players) {
			var tiles = getTilesAt(player.getPos());

			if (tiles.contains(Tiles.LASER_EMITTER)) {
				player.damageRobot();
			} else if (tiles.contains(Tiles.LASER) && !hasRobotInWayOfEmitter(player.getPos())) {
				player.damageRobot();
			}
		}
	}
}
