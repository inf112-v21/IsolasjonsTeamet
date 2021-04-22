package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.actions.Uturn;
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

/**
 * Class to test player code, and see if it is successful.
 */
public class PlayerSpec implements ActionProcessor {

	private static final String BOARD_GIVEN = "Given a simple 5x5 board with flag at 4,4 and hole at 2,2";

	private BoardImpl board;
	private Player activePlayer;

	/**
	 * Creates a new simple player for testing.
	 *
	 * @return PlayerImpl
	 */
	private PlayerImpl createSimplePlayer(Coordinate coordinate, Orientation orientation) {
		return new PlayerImpl(this, "dummy", coordinate, orientation);
	}

	private PlayerImpl createSimplePlayer() {
		return createSimplePlayer(new Coordinate(0, 0), Orientation.EAST);
	}

	/**
	 * Method for making a new player active.
	 *
	 * @return player
	 */
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

	/**
	 * Method for creating a new simple board we will be running our tests on.
	 */
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

	/**
	 * Assert current playerpos with wanted pos.
	 */
	private void assertPlayerPos(Player player, Coordinate coord) {
		assertEquals(coord, player.getPos());
		assertEquals(player, board.getPlayerAt(coord));
	}

	/**
	 * Perform one action.
	 *
	 * @param player The player to run the action for.
	 */
	@Override
	public void performActionNow(Player player, Action action) {
		action.initialize(board, player);
		action.perform(this, board, player);
		board.checkValid();
	}

	/**
	 * Schedule an action.
	 */
	@Override
	public void scheduleAction(Player player, Action action) {

	}

	/**
	 * Runs an action on our testboard.
	 */
	private void runAction(Action action) {
		performActionNow(activePlayer, action);
	}

	/**
	 * Test method to check if the player gets the correct position we move him 1 forward.
	 */
	@DisplayName(BOARD_GIVEN + ", given a player at 0,0 facing EAST. Moving forward should move the player")
	@Test
	public void move1Forward() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);

		runAction(new MoveForward(1));

		assertPlayerPos(player, new Coordinate(1, 0));
	}

	/**
	 * Test method to check if a player is unable to move out of bounds.
	 */
	@DisplayName(BOARD_GIVEN + ", given a player at 0,0 facing WEST. Moving forward should do nothing")
	@Test
	public void moveOutOfBounds() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);
		player.setDir(Orientation.WEST);

		runAction(new MoveForward(1));

		assertPlayerPos(player, new Coordinate(0, 0));
	}

	/**
	 * Test method to check if a player can leap out of bounds.
	 */
	@DisplayName(BOARD_GIVEN + ", given a player at 1,0 facing WEST. Moving forward 2 should move the player once")
	@Test
	public void leapOutOfBounds() {
		var player = createSimpleActivePlayer(new Coordinate(1, 0), Orientation.WEST);
		createSimpleBoard(player);

		runAction(new MoveForward(2));

		assertPlayerPos(player, new Coordinate(0, 0));
	}

	/**
	 * Test method for checking that robots can take damage
	 */
	@Test
	public void testDamageRobot() {
		var player = createSimplePlayer();
		player.damageRobot();
		assertEquals(1, player.getDamageTokens());
	}

	/**
	 * Test method to check that an robot not can get negative damage tokens
	 */
	@Test
	public void testNotNegativeDamageToken() {
		var player = createSimplePlayer();
		assertThrows(IllegalStateException.class, () -> player.repairRobot());
	}

	/**
	 * Test method to check than an robot can get repaired
	 */
	@Test
	public void testRepairRobot() {
		var player = createSimplePlayer();
		player.damageRobot();
		player.repairRobot();
		assertEquals(0, player.getDamageTokens());
	}

	/**
	 * Test method to check if Robots gets killed when it reaches 10 damage tokens and damage tokens
	 */
	@Test
	public void testKillRobot() {
		var player = createSimplePlayer();
		createSimpleBoard(player);

		for (int i = 0; i < 10; i++) {
			player.damageRobot();
		}
		assertEquals(0, player.getDamageTokens());
		assertEquals(4, player.getLife());
	}

	/**
	 * Test method to check if a player facing EAST, turning around will make the player face WEST
	 */
	@DisplayName(BOARD_GIVEN + ", given a player facing EAST, changing to opposite direction should return WEST")
	@Test
	public void rotateToOppositeDir() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);

		runAction(new Uturn());

		assertEquals(player.getDir(), Orientation.WEST);
	}

	/**
	 * Test method to check if a player facing EAST, rotates to SOUTH if rotateRight is called
	 */
	@DisplayName(BOARD_GIVEN + ", given a player facing EAST, rotating 1 to the right should return SOUTH")
	@Test
	public void rotate1ToTheRight() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);

		runAction(new RotateRight());

		assertEquals(player.getDir(), Orientation.SOUTH);
	}

	/**
	 * Test method to check if a player facing EAST, rotates to WEST if rotateLeft is called
	 */
	@DisplayName(BOARD_GIVEN + ", given a player facing EAST, rotating 1 to the left should return WEST")
	@Test
	public void rotate1ToTheLeft() {
		var player = createSimpleActivePlayer();
		createSimpleBoard(player);

		runAction(new RotateLeft());

		assertEquals(player.getDir(), Orientation.NORTH);
	}
}
