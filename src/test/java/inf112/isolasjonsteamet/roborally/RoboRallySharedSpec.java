package inf112.isolasjonsteamet.roborally;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.app.RoboRallyShared;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class RoboRallySharedSpec {

	protected Board board;
	protected List<Player> players = new ArrayList<>();
	protected Phase currentPhase = Phase.CARDS;
	protected int playerNum;

	protected abstract AbstractTestingRoboRallyShared roboShared();

	protected PlayerImpl newPlayer(String name, Coordinate pos, Orientation dir) {
		var player = new PlayerImpl(name, roboShared(), pos, dir);
		players.add(player);
		board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));
		board.checkValid();
		return player;
	}

	protected PlayerImpl newPlayer(Coordinate pos, Orientation dir) {
		return newPlayer("player" + playerNum++, pos, dir);
	}

	protected Coordinate coord(int x, int y) {
		return new Coordinate(x, y);
	}

	class AbstractTestingRoboRallyShared extends RoboRallyShared {

		private boolean performingAction = false;
		private final Queue<Entry<Action, Robot>> scheduledActions = new ArrayDeque<>();

		@Override
		public void performActionNow(Robot robot, Action action, Phase phase) {
			boolean hasWork;
			do {
				action.initialize(board, robot);

				performingAction = true;
				action.perform(this, board, robot, phase);
				performingAction = false;

				board.checkValid();

				Map.Entry<Action, Robot> nextActionEntry = scheduledActions.poll();
				hasWork = nextActionEntry != null;
				if (nextActionEntry != null) {
					robot = nextActionEntry.getValue();
					action = nextActionEntry.getKey();
				}
			} while (hasWork);
		}

		@Override
		public void scheduleAction(Robot robot, Action action) {
			if (scheduledActions.isEmpty() && !performingAction) {
				performActionNow(robot, action, currentPhase);
			} else {
				scheduledActions.add(Map.entry(action, robot));
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
		protected void setCurrentPhase(Phase phase) {
			currentPhase = phase;
		}
	}
}
