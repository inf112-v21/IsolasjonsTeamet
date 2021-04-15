package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.actions.Uturn;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.players.RobotImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Class to test robot code, and see if it is successful.
 */
public class RobotSpec implements ActionProcessor {

	private static final String BOARD_GIVEN = "Given a simple 5x5 board with flag at 4,4 and hole at 2,2";

	private BoardImpl board;
	private Robot activeRobot;

	/**
	 * Creates a new simple robot for testing.
	 */
	private RobotImpl createSimpleRobot(Coordinate coordinate, Orientation orientation) {
		return new RobotImpl(this, coordinate, orientation);
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

	/**
	 * Assert current robotpos with wanted pos.
	 */
	private void assertRobotPos(Robot robot, Coordinate coord) {
		assertEquals(coord, robot.getPos());
		assertEquals(robot, board.getRobotAt(coord));
	}

	@Override
	public void performActionNow(Robot robot, Action action) {
		action.perform(this, board, robot);
		board.checkValid();
	}

	/**
	 * Runs an action on our testboard.
	 */
	private void runAction(Action action) {
		performActionNow(activeRobot, action);
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
}
