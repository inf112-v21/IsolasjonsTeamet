package inf112.isolasjonsteamet.roborally;

import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.ServerSideActionProcessor;
import inf112.isolasjonsteamet.roborally.app.RoboRallyShared;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class RoboRallySharedSpec {

	protected Board board;
	protected List<Player> players = new ArrayList<>();
	protected int playerNum;

	protected abstract AbstractTestingRoboRallyShared roboShared();

	protected PlayerImpl newPlayer(String name, Coordinate pos, Orientation dir) {
		var player = new PlayerImpl(name, roboShared().actionProcessor, pos, dir);
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

		private final ActionProcessor actionProcessor = new ServerSideActionProcessor(() -> board);

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
		protected ActionProcessor actionProcessor() {
			return actionProcessor;
		}
	}
}
