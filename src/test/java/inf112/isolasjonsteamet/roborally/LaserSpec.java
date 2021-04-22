package inf112.isolasjonsteamet.roborally;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaserSpec extends RoboRallySharedSpec {

	private final TestingRoboRallyShared roboShared = new TestingRoboRallyShared();

	@Override
	protected TestingRoboRallyShared roboShared() {
		return roboShared;
	}

    @Test
    public void laserDamagesPlayers() {
		var board = new BoardImpl(
				ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)),
				"l"
		);
		var robot = newPlayer(new Coordinate(0, 0), Orientation.NORTH).getRobot();

        roboShared.fireLasers();

        assertEquals(1, robot.getDamageTokens());
    }

    @Test
    public void laserEmitterDamagesPlayers() {
		board = new BoardImpl(
				ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER)),
				"l"
		);
		var robot = newPlayer(new Coordinate(0, 0), Orientation.NORTH).getRobot();

		roboShared.fireLasers();

        assertEquals(1, robot.getDamageTokens());
    }

    @Test
    public void laserDoesNotPassThroughPlayers() {
		board = new BoardImpl(
				ImmutableMap.of(
						'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
						'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
				),
				"el"
		);
		var robot1 = newPlayer(new Coordinate(0, 0), Orientation.NORTH).getRobot();
		var robot2 = newPlayer(new Coordinate(1, 0), Orientation.NORTH).getRobot();

		roboShared.fireLasers();

        assertEquals(1, robot1.getDamageTokens());
        assertEquals(0, robot2.getDamageTokens());
    }

    @Test
    public void laserCanComeFromMultipleDirections() {
		board = new BoardImpl(
				ImmutableMap.of(
						'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
						'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
				),
				"ellle"
		);
		var robot1 = newPlayer(new Coordinate(1, 0), Orientation.NORTH).getRobot();
		var robot2 = newPlayer(new Coordinate(2, 0), Orientation.NORTH).getRobot();
		var robot3 = newPlayer(new Coordinate(3, 0), Orientation.NORTH).getRobot();

		roboShared.fireLasers();

        assertEquals(1, robot1.getDamageTokens());
        assertEquals(0, robot2.getDamageTokens());
        assertEquals(1, robot3.getDamageTokens());
    }


	private class TestingRoboRallyShared extends AbstractTestingRoboRallyShared {

		@Override
		protected void fireLasers() {
			super.fireLasers();
		}
	}
}
