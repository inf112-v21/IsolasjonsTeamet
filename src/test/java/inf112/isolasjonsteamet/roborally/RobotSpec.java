package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.DequeActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.actions.ScheduledAction;
import inf112.isolasjonsteamet.roborally.actions.ServerSideActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.Uturn;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.players.RobotImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.tiles.WallTileType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Class to test robot code, and see if it is successful.
 */
public class RobotSpec {

	private static final String BOARD_GIVEN = "Given a simple 5x5 board with flag at 4,4 and hole at 2,2";

	private BoardImpl board;
	private Robot activeRobot;

	private final ActionProcessor actionProcessor = new ServerSideActionProcessor(() -> board);

	/**
	 * Creates a new simple robot for testing.
	 */
	private RobotImpl createSimpleRobot(Coordinate coordinate, Orientation orientation) {
		return new RobotImpl(actionProcessor, coordinate, orientation);
	}

	private RobotImpl createSimpleRobot() {
		return createSimpleRobot(new Coordinate(0, 0), Orientation.EAST);
	}

	/**
	 * Method for making a new robot active.
	 */
	private RobotImpl createSimpleActiveRobot(Coordinate coordinate, Orientation orientation) {
		var robot = createSimpleRobot(coordinate, orientation);
		activeRobot = robot;
		return robot;
	}

	private RobotImpl createSimpleActiveRobot() {
		var robot = createSimpleRobot();
		activeRobot = robot;
		return robot;
	}

	/**
	 * Method for creating a new simple board we will be running our tests on.
	 */
	private void createSimpleBoard(Robot robot) {
		var charMap =
				ImmutableMap.<Character, List<Tile>>builder()
						.put('o', ImmutableList.of(Tiles.GROUND))
						.put('x', ImmutableList.of(Tiles.HOLE))
						.put('f', ImmutableList.of(Tiles.FLAG))
						.build();

		board = new BoardImpl(charMap, """
				oooof
				ooooo
				ooxoo
				ooooo
				ooooo""");
		board.updateActiveRobots(ImmutableList.of(robot));
	}


	private List<Tile> wall(Orientation... hasWallIn) {
		var wallDirs = Arrays.asList(hasWallIn);
		return ImmutableList.of(new WallTileType(
				wallDirs.contains(Orientation.NORTH),
				wallDirs.contains(Orientation.WEST),
				wallDirs.contains(Orientation.SOUTH),
				wallDirs.contains(Orientation.EAST)
		));
	}

	private void createWallBoard(Robot robot) {
		var charMap =
				ImmutableMap.<Character, List<Tile>>builder()
						.put('o', ImmutableList.of(Tiles.GROUND))
						.put(' ', ImmutableList.of(Tiles.HOLE))
						.put('w', wall(Orientation.WEST))
						.put('e', wall(Orientation.EAST))
						.put('n', wall(Orientation.NORTH))
						.put('s', wall(Orientation.SOUTH))
						.build();

		board = new BoardImpl(charMap, """
				owo oooo
				oso oooo
				ono ensw
				oeo oooo""");
	}

	/**
	 * Assert current robotpos with wanted pos.
	 */
	private void assertRobotPos(Robot robot, Coordinate coord) {
		assertEquals(coord, robot.getPos());
		assertEquals(robot, board.getRobotAt(coord));
	}

	/**
	 * Runs an action on our testboard.
	 */
	private void runAction(Action action) {
		actionProcessor.performActionNow(activeRobot, action, Phase.CARDS);
	}

	/**
	 * Test method to check if the robot gets the correct position we move him 1 forward.
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot at 0,0 facing EAST. Moving forward should move the robot")
	@Test
	public void move1Forward() {
		var robot = createSimpleActiveRobot();
		createSimpleBoard(robot);

		runAction(new MoveForward(1));

		assertRobotPos(robot, new Coordinate(1, 0));
	}

	/**
	 * Test method to check if a robot is unable to move out of bounds.
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot at 0,0 facing WEST. Moving forward should do nothing")
	@Test
	public void moveOutOfBounds() {
		var robot = createSimpleActiveRobot();
		createSimpleBoard(robot);
		robot.setDir(Orientation.WEST);

		runAction(new MoveForward(1));

		assertRobotPos(robot, new Coordinate(0, 0));
	}

	/**
	 * Test method to check if a robot can leap out of bounds.
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot at 1,0 facing WEST. Moving forward 2 should move the robot once")
	@Test
	public void leapOutOfBounds() {
		var robot = createSimpleActiveRobot(new Coordinate(1, 0), Orientation.WEST);
		createSimpleBoard(robot);

		runAction(new MoveForward(2));

		assertRobotPos(robot, new Coordinate(0, 0));
	}

	/**
	 * Test method for checking that robots can take damage
	 */
	@Test
	public void testDamageRobot() {
		var robot = createSimpleRobot();
		robot.damageRobot();
		assertEquals(1, robot.getDamageTokens());
	}

	/**
	 * Test method to check that an robot not can get negative damage tokens
	 */
	@Test
	public void testNotNegativeDamageToken() {
		var robot = createSimpleRobot();
		assertThrows(IllegalStateException.class, robot::repairRobot);
	}

	/**
	 * Test method to check than an robot can get repaired
	 */
	@Test
	public void testRepairRobot() {
		var robot = createSimpleRobot();
		robot.damageRobot();
		robot.repairRobot();
		assertEquals(0, robot.getDamageTokens());
	}

	/**
	 * Test method to check if Robots gets killed when it reaches 10 damage tokens and damage tokens
	 */
	@Test
	public void testKillRobot() {
		var robot = createSimpleRobot();
		createSimpleBoard(robot);

		for (int i = 0; i < 10; i++) {
			robot.damageRobot();
		}
		assertEquals(0, robot.getDamageTokens());
		assertEquals(4, robot.getLife());
	}

	/**
	 * Test method to check if a robot facing EAST, turning around will make the robot face WEST
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot facing EAST, changing to opposite direction should return WEST")
	@Test
	public void rotateToOppositeDir() {
		var robot = createSimpleActiveRobot();
		createSimpleBoard(robot);

		runAction(new Uturn());

		assertEquals(robot.getDir(), Orientation.WEST);
	}

	/**
	 * Test method to check if a robot facing EAST, rotates to SOUTH if rotateRight is called
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot facing EAST, rotating 1 to the right should return SOUTH")
	@Test
	public void rotate1ToTheRight() {
		var robot = createSimpleActiveRobot();
		createSimpleBoard(robot);

		runAction(new RotateRight());

		assertEquals(robot.getDir(), Orientation.SOUTH);
	}

	/**
	 * Test method to check if a robot facing EAST, rotates to WEST if rotateLeft is called
	 */
	@DisplayName(BOARD_GIVEN + ", given a robot facing EAST, rotating 1 to the left should return WEST")
	@Test
	public void rotate1ToTheLeft() {
		var robot = createSimpleActiveRobot();
		createSimpleBoard(robot);

		runAction(new RotateLeft());

		assertEquals(robot.getDir(), Orientation.NORTH);
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
		var robot = createSimpleActiveRobot(testData.playerCoord, testData.playerDir);
		createWallBoard(robot);

		runAction(new MoveForward(1));
		assertEquals(testData.expectedLocation1, robot.getPos());

		runAction(new MoveForward(1));
		assertEquals(testData.expectedLocation2, robot.getPos());
	}

	@ParameterizedTest
	@MethodSource("testWallPassageValues")
	public void testWallPassageMove2(TestWallPassageData testData) {
		var robot = createSimpleActiveRobot(testData.playerCoord, testData.playerDir);
		createWallBoard(robot);

		runAction(new MoveForward(2));
		assertEquals(testData.expectedLocation2, robot.getPos());
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

	@Test
	public void pushRobots() {
		var player1 = createSimpleRobot(new Coordinate(0, 0), Orientation.EAST);
		var player2 = createSimpleRobot(new Coordinate(1, 0), Orientation.EAST);
		createSimpleBoard(player1);
		board.updateActiveRobots(ImmutableList.of(player1, player2));

		actionProcessor.performActionNow(player1, new MoveForward(1), Phase.MISC);
		assertEquals(new Coordinate(1, 0), player1.getPos());
		assertEquals(new Coordinate(2, 0), player2.getPos());
	}
}
