package inf112.isolasjonsteamet.roborally;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.RobotImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaserSpec {

    @Test
    public void laserDamagesPlayers() {
        var robot = new RobotImpl(null, new Coordinate(0, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)),
                "l"
        );
        board.updateActiveRobots(ImmutableList.of(robot));

        board.checkValid();
        board.fireLaser();

        assertEquals(1, robot.getDamageTokens());
    }

    @Test
    public void laserEmitterDamagesPlayers() {
        var robot = new RobotImpl(null, new Coordinate(0, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER)),
                "l"
        );
		board.updateActiveRobots(ImmutableList.of(robot));

        board.checkValid();
        board.fireLaser();

        assertEquals(1, robot.getDamageTokens());
    }

    @Test
    public void laserDoesNotPassThroughPlayers() {
        var robot1 = new RobotImpl(null, new Coordinate(0, 0), Orientation.NORTH);
        var robot2 = new RobotImpl(null, new Coordinate(1, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableMap.of(
                        'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
                        'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
                ),
                "el"
        );
		board.updateActiveRobots(ImmutableList.of(robot1, robot2));

        board.checkValid();
        board.fireLaser();

        assertEquals(1, robot1.getDamageTokens());
        assertEquals(0, robot2.getDamageTokens());
    }

    @Test
    public void laserCanComeFromMultipleDirections() {
        var robot1 = new RobotImpl(null, new Coordinate(1, 0), Orientation.NORTH);
        var robot2 = new RobotImpl(null, new Coordinate(2, 0), Orientation.NORTH);
        var robot3 = new RobotImpl(null, new Coordinate(3, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableMap.of(
                        'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
                        'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
                ),
                "ellle"
        );
		board.updateActiveRobots(ImmutableList.of(robot1, robot2, robot3));

        board.checkValid();
        board.fireLaser();

        assertEquals(1, robot1.getDamageTokens());
        assertEquals(0, robot2.getDamageTokens());
        assertEquals(1, robot3.getDamageTokens());
    }
}
