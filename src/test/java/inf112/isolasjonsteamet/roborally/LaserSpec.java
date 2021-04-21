package inf112.isolasjonsteamet.roborally;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LaserSpec {

    @Test
    public void laserDamagesPlayers() {
        var player = new PlayerImpl(null, "foo", new Coordinate(0, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableList.of(player),
                ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)),
                "l"
        );

        board.checkValid();
        board.fireLaser();

        assertEquals(1, player.getDamageTokens());
    }

    @Test
    public void laserEmitterDamagesPlayers() {
        var player = new PlayerImpl(null, "foo", new Coordinate(0, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableList.of(player),
                ImmutableMap.of('l', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER)),
                "l"
        );

        board.checkValid();
        board.fireLaser();

        assertEquals(1, player.getDamageTokens());
    }

    @Test
    public void laserDoesNotPassThroughPlayers() {
        var player1 = new PlayerImpl(null, "foo", new Coordinate(0, 0), Orientation.NORTH);
        var player2 = new PlayerImpl(null, "bar", new Coordinate(1, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableList.of(player1, player2),
                ImmutableMap.of(
                        'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
                        'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
                ),
                "el"
        );

        board.checkValid();
        board.fireLaser();

        assertEquals(1, player1.getDamageTokens());
        assertEquals(0, player2.getDamageTokens());
    }

    @Test
    public void laserCanComeFromMultipleDirections() {
        var player1 = new PlayerImpl(null, "foo", new Coordinate(1, 0), Orientation.NORTH);
        var player2 = new PlayerImpl(null, "bar", new Coordinate(2, 0), Orientation.NORTH);
        var player3 = new PlayerImpl(null, "baz", new Coordinate(3, 0), Orientation.NORTH);
        var board = new BoardImpl(
                ImmutableList.of(player1, player2, player3),
                ImmutableMap.of(
                        'e', ImmutableList.of(Tiles.GROUND, Tiles.LASER_EMITTER),
                        'l', ImmutableList.of(Tiles.GROUND, Tiles.LASER)
                ),
                "ellle"
        );

        board.checkValid();
        board.fireLaser();

        assertEquals(1, player1.getDamageTokens());
        assertEquals(0, player2.getDamageTokens());
        assertEquals(1, player3.getDamageTokens());
    }
}
