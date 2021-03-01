package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.tiles.TileType;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlayerSpec {

	private static final String BOARD_GIVEN = "Given a simple 5x5 board with flag at 4,4 and hole at 2,2";

	private BoardImpl board;
	private Player activePlayer;

	private PlayerImpl createSimplePlayer(Coordinate coordinate, Orientation orientation) {
		return new PlayerImpl("foo", coordinate, orientation);
	}

	private PlayerImpl createSimplePlayer() {
		return createSimplePlayer(new Coordinate(0, 0), Orientation.EAST);
	}

	private PlayerImpl createSimpleActivePlayer(Coordinate coordinate, Orientation orientation) {
		var player = createSimplePlayer(coordinate, orientation);
		activePlayer = player;
		return player;
	}

	private PlayerImpl createSimpleActivePlayer() {
		var player = createSimplePlayer();
		activePlayer = player;
		return player;
	}

	private void createSimpleBoard(Player player) {
		var charMap =
				ImmutableMap.<Character, List<TileType>>builder()
						.put('o', ImmutableList.of(Tiles.GROUND))
						.put('x', ImmutableList.of(Tiles.HOLE))
						.put('f', ImmutableList.of(Tiles.FLAG))
						.build();

		board = new BoardImpl(ImmutableList.of(player), charMap, """
				oooof
				ooooo
				ooxoo
				ooooo
				ooooo""");
	}

	private void assertPlayerPos(Player player, Coordinate coord) {
		assertEquals(coord, player.getPos());
		assertEquals(player, board.getPlayerAt(coord));
	}

	private void runAction(Action action) {
		action.perform(board, activePlayer);
		board.checkValid();
	}

	@DisplayName(BOARD_GIVEN + ", given a player at 0,0 facing EAST. Moving forward should move the player")
	@Test
	public void move1Forward() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);

		runAction(new MoveForward(1));

		assertPlayerPos(player, new Coordinate(1, 0));
	}

	@DisplayName(BOARD_GIVEN + ", given a player at 0,0 facing WEST. Moving forward should do nothing")
	@Test
	public void moveOutOfBounds() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);
		player.setDir(Orientation.WEST);

		runAction(new MoveForward(1));

		assertPlayerPos(player, new Coordinate(0, 0));
	}

	@DisplayName(BOARD_GIVEN + ", given a player at 1,0 facing WEST. Moving forward 2 should move the player once")
	@Test
	public void leapOutOfBounds() {
		var player = createSimpleActivePlayer(new Coordinate(1, 0), Orientation.WEST);
		createSimpleBoard(player);

		runAction(new MoveForward(2));

		assertPlayerPos(player, new Coordinate(0, 0));
	}
}
