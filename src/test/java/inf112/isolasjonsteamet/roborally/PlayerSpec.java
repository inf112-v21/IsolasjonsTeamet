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
import inf112.isolasjonsteamet.roborally.tiles.WallTileType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.function.IntFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Class to test player code, and see if it is successful.
 */
public class PlayerSpec implements ActionProcessor {

	private static final String BOARD_GIVEN = "Given a simple 5x5 board with flag at 4,4 and hole at 2,2";

	private BoardImpl board;
	private Player activePlayer;

	private boolean performingAction = false;
	private final Queue<Entry<Action, Player>> scheduledActions = new ArrayDeque<>();

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
				ooooo""");
	}

	private List<TileType> wall(Orientation... hasWallIn) {
		var wallDirs = Arrays.asList(hasWallIn);
		return ImmutableList.of(new WallTileType(
				wallDirs.contains(Orientation.NORTH),
				wallDirs.contains(Orientation.WEST),
				wallDirs.contains(Orientation.SOUTH),
				wallDirs.contains(Orientation.EAST)
		));
	}

	private void createWallBoard(Player player) {
		var charMap =
				ImmutableMap.<Character, List<TileType>>builder()
						.put('o', ImmutableList.of(Tiles.GROUND))
						.put(' ', ImmutableList.of(Tiles.HOLE))
						.put('w', wall(Orientation.WEST))
						.put('e', wall(Orientation.EAST))
						.put('n', wall(Orientation.NORTH))
						.put('s', wall(Orientation.SOUTH))
						.build();

		board = new BoardImpl(ImmutableList.of(player), charMap, """
				owo oooo
				oso oooo
				ono ensw
				oeo oooo""");
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
		if (scheduledActions.isEmpty() && !performingAction) {
			performActionNow(player, action);
		} else {
			scheduledActions.add(Map.entry(action, player));
		}
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

	static class TestWallPassageData {

		private final Coordinate playerCoord;
		private final Orientation playerDir;
		private final Orientation wallDir;

		private final Coordinate expectedLocation1;
		private final Coordinate expectedLocation2;

		TestWallPassageData(
				Coordinate playerCoord,
				Orientation playerDir,
				Orientation wallDir, Coordinate expectedLocation1,
				Coordinate expectedLocation2
		) {
			this.playerCoord = playerCoord;
			this.playerDir = playerDir;
			this.wallDir = wallDir;
			this.expectedLocation1 = expectedLocation1;
			this.expectedLocation2 = expectedLocation2;
		}

		@Override
		public String toString() {
			return ("Starting at %s facing %s with the wall facing %s, "
					+ "moving one should land us at %s and two at %s"
			).formatted(playerCoord, playerDir, wallDir, expectedLocation1, expectedLocation2);
		}
	}

	@ParameterizedTest
	@MethodSource("testWallPassageValues")
	public void testWallPassageStepwise(TestWallPassageData testData) {
		var player = createSimpleActivePlayer(testData.playerCoord, testData.playerDir);
		createWallBoard(player);

		runAction(new MoveForward(1));
		assertEquals(testData.expectedLocation1, player.getPos());

		runAction(new MoveForward(1));
		assertEquals(testData.expectedLocation2, player.getPos());
	}

	@ParameterizedTest
	@MethodSource("testWallPassageValues")
	public void testWallPassageMove2(TestWallPassageData testData) {
		var player = createSimpleActivePlayer(testData.playerCoord, testData.playerDir);
		createWallBoard(player);

		runAction(new MoveForward(2));
		assertEquals(testData.expectedLocation2, player.getPos());
	}

	private static Coordinate coord(int x, int y) {
		return new Coordinate(x, y);
	}

	private static List<TestWallPassageData> testWallPassageValues() {
		var acc = new ArrayList<TestWallPassageData>();
		var wallDirs =
				ImmutableList.of(Orientation.EAST, Orientation.NORTH, Orientation.SOUTH, Orientation.WEST);

		//The starting position for the robot when it is facing in a given direction
		Map<Orientation, IntFunction<Coordinate>> startPoses = ImmutableMap.of(
				Orientation.EAST, i -> coord(0, i),
				Orientation.WEST, i -> coord(2, i),
				Orientation.NORTH, i -> coord(i + 4, 0),
				Orientation.SOUTH, i -> coord(i + 4, 2)
		);

		startPoses.forEach((facing, makeStartPos) -> {
			for (int i = 0; i < wallDirs.size(); i++) {
				var wallDir = wallDirs.get(i);
				var facingOffset = facing.toCoord();

				// If the wall in front of us is facing the opposite direction
				// as we are, and thus immediately blocking our path.
				var oppositeFacing = wallDir.getOpposingDir().equals(facing);

				// If the wall in front of us is facing the same direction as
				// we are, and thus eventually blocking our path.
				var sameFacing = wallDir.equals(facing);

				//The max amount of step we can take before we are blocked by a wall
				int maxProgress;
				if (oppositeFacing) {
					maxProgress = 0;
				} else if (sameFacing) {
					maxProgress = 1;
				} else {
					maxProgress = 2;
				}

				var startPos = makeStartPos.apply(i);
				acc.add(new TestWallPassageData(
						startPos,
						facing,
						wallDir,
						startPos.add(facingOffset.mult(Math.min(maxProgress, 1))),
						startPos.add(facingOffset.mult(maxProgress))
				));
			}
		});

		return acc;
	}
}
