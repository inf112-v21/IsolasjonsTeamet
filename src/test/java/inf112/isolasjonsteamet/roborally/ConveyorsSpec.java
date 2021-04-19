package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.app.RoboRallyShared;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.ConveyorBeltTile;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConveyorsSpec {

	private final TestingRoboRallyShared roboShared = new TestingRoboRallyShared();
	private Board board;
	private List<Player> players = new ArrayList<>();
	private boolean doBoardCheck = true;
	private int playerNum;

	private PlayerImpl newPlayer(String name, Coordinate pos, Orientation dir) {
		var player = new PlayerImpl(name, roboShared, pos, dir);
		players.add(player);
		board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));
		return player;
	}

	private PlayerImpl newPlayer(Coordinate pos, Orientation dir) {
		return newPlayer("player" + playerNum++, pos, dir);
	}

	private Coordinate coord(int x, int y) {
		return new Coordinate(x, y);
	}

	@DisplayName("Given a board with conveyors")
	@Nested
	class Conveyors {

		String boardTemplate = """
				ooqeeo ooQEEo
				oonooo ooNooo
				oonooo ooNooo
				eerwwo EERwwo""";
		//      012345 789012

		public Conveyors() {
			var tileMapBuilder = ImmutableMap.<Character, List<Tile>>builder();
			tileMapBuilder.put('o', ImmutableList.of(Tiles.GROUND));
			tileMapBuilder.put(' ', ImmutableList.of(Tiles.HOLE));

			tileMapBuilder.put('n', ImmutableList.of(new ConveyorBeltTile(Orientation.NORTH, false, false)));
			tileMapBuilder.put('N', ImmutableList.of(new ConveyorBeltTile(Orientation.NORTH, false, true)));

			tileMapBuilder.put('e', ImmutableList.of(new ConveyorBeltTile(Orientation.EAST, false, false)));
			tileMapBuilder.put('E', ImmutableList.of(new ConveyorBeltTile(Orientation.EAST, false, true)));

			tileMapBuilder.put('w', ImmutableList.of(new ConveyorBeltTile(Orientation.WEST, false, false)));
			tileMapBuilder.put('W', ImmutableList.of(new ConveyorBeltTile(Orientation.WEST, false, true)));

			tileMapBuilder.put('r', ImmutableList.of(new ConveyorBeltTile(Orientation.NORTH, true, false)));
			tileMapBuilder.put('R', ImmutableList.of(new ConveyorBeltTile(Orientation.NORTH, true, true)));

			tileMapBuilder.put('q', ImmutableList.of(new ConveyorBeltTile(Orientation.EAST, true, false)));
			tileMapBuilder.put('Q', ImmutableList.of(new ConveyorBeltTile(Orientation.EAST, true, true)));

			board = new BoardImpl(
					tileMapBuilder.build(),
					boardTemplate
			);
		}

		@DisplayName("A conveyor belt should move the player one forward in the direction it's facing")
		@Test
		public void simpleMovement() {
			var player = newPlayer(coord(2, 1), Orientation.NORTH);

			roboShared.processBoardElements();

			assertEquals(coord(2, 2), player.getRobot().getPos());
		}

		@DisplayName("An express conveyor belt should move the player one forward, "
					 + "and then one forward again later in the direction it's facing")
		@Test
		public void simpleMovementExpress() {
			var player = newPlayer(coord(9, 1), Orientation.NORTH);

			roboShared.processBoardElements();

			assertEquals(coord(9, 3), player.getRobot().getPos());
		}

		@DisplayName("Multiple robots in a row on belts should cause no problem")
		@Test
		public void noRowProblems() {
			var player1 = newPlayer(coord(2, 0), Orientation.NORTH);
			var player2 = newPlayer(coord(2, 1), Orientation.NORTH);

			roboShared.processBoardElements();

			assertEquals(coord(2, 1), player1.getRobot().getPos());
			assertEquals(coord(2, 2), player2.getRobot().getPos());
		}

		@DisplayName("A belt should not be able to push robots not standing on a belt")
		@Test
		public void noPushGround() {
			var stillPlayer = newPlayer(coord(5, 3), Orientation.WEST);
			var beltPlayer = newPlayer(coord(4, 3), Orientation.EAST);

			roboShared.processBoardElements();

			assertEquals(coord(5, 3), stillPlayer.getRobot().getPos());
			assertEquals(coord(4, 3), beltPlayer.getRobot().getPos());
		}

		@DisplayName("If the robot at the end of a belt section can't move, then neither should robots after it")
		@Test
		public void noRobotsMoveIfObstacleAhead() {
			var blockade = newPlayer(coord(5, 3), Orientation.EAST);
			var player1 = newPlayer(coord(4, 3), Orientation.EAST);
			var player2 = newPlayer(coord(3, 3), Orientation.EAST);

			roboShared.processBoardElements();

			assertEquals(coord(5, 3), blockade.getRobot().getPos());
			assertEquals(coord(4, 3), player1.getRobot().getPos());
			assertEquals(coord(3, 3), player2.getRobot().getPos());
		}

		@DisplayName("When two belts of equal priority with robots point to a shared tile, neither belt should move the robot")
		@Test
		public void noMoveContested() {
			var player1 = newPlayer(coord(1, 0), Orientation.EAST);
			var player2 = newPlayer(coord(3, 0), Orientation.WEST);

			roboShared.processBoardElements();

			assertEquals(coord(1, 0), player1.getRobot().getPos());
			assertEquals(coord(3, 0), player2.getRobot().getPos());
		}

		@DisplayName("When two belts of unequal priority with robots point to a shared tile, then everything should work fine")
		@Test
		public void moveContestedUnequalPriority() {
			var player1 = newPlayer(coord(8, 0), Orientation.EAST);
			var player2 = newPlayer(coord(10, 0), Orientation.WEST);

			roboShared.processBoardElements();

			assertEquals(coord(9, 1), player1.getRobot().getPos());
			assertEquals(coord(9, 0), player2.getRobot().getPos());
		}


		@DisplayName("Given a player on a conveyor belt facing north, and rotating to the right on the next belt")
		@Nested
		class RotateBelt {

			@DisplayName("When the player is facing north, when the conveyor belts move, the player should be moved to "
						 + "the new conveyor belt, and facing east")
			@Test
			public void faceNorthEast() {
				var player = newPlayer(coord(2, 2), Orientation.NORTH);

				roboShared.processBoardElements();

				assertEquals(Orientation.EAST, player.getRobot().getDir());
				assertEquals(coord(2, 3), player.getRobot().getPos());
			}

			@DisplayName("When the player is facing west, when the conveyor belts move, the player should be moved to "
						 + "the new conveyor belt, and facing north")
			@Test
			public void faceWestNorth() {
				var player = newPlayer(new Coordinate(2, 2), Orientation.WEST);

				roboShared.processBoardElements();

				assertEquals(Orientation.NORTH, player.getRobot().getDir());
				assertEquals(coord(2, 3), player.getRobot().getPos());
			}
		}
	}


	class TestingRoboRallyShared extends RoboRallyShared {

		@Override
		public void performActionNow(Robot robot, Action action, Phase phase) {
			action.perform(this, board, robot, phase);

			if (doBoardCheck) {
				board.checkValid();
			}
		}

		@Override
		protected void foreachPlayerTile(BiConsumer<Player, Tile> handler) {
			for (Player player : players) {
				for (Tile tile : board.getTilesAt(player.getRobot().getPos())) {
					handler.accept(player, tile);
				}
			}
		}

		@Override
		protected Board board() {
			return board;
		}

		@Override
		protected void skipBoardValidChecks() {
			doBoardCheck = false;
		}

		@Override
		protected void enableBoardValidChecks() {
			doBoardCheck = true;
		}

		@Override
		protected void processBoardElements() {
			super.processBoardElements();
		}
	}
}
