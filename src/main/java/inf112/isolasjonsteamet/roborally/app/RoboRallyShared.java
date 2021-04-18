package inf112.isolasjonsteamet.roborally.app;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.Move;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.tiles.ConveyerBeltTile;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.function.BiConsumer;

public abstract class RoboRallyShared implements ActionProcessor {

	protected abstract void foreachPlayerTile(BiConsumer<Player, Tile> handler);

	protected void processPlayerTiles(Phase phase) {
		foreachPlayerTile((player, tile) -> {
			for (Action action : tile.getActions()) {
				performActionNow(player.getRobot(), action, phase);
			}
		});
	}

	private boolean isConveyorBelt(Tile tile, boolean expressOnly) {
		if (!(tile instanceof ConveyerBeltTile)) {
			return false;
		}

		return !expressOnly || ((ConveyerBeltTile) tile).isExpress();
	}

	private boolean isConveyerMovementBlocked(Coordinate pos, Orientation dir) {
		//TODO
		return false;
	}

	private void processConveyorBelts(boolean expressOnly) {
		foreachPlayerTile((player, tile) -> {
			if (isConveyorBelt(tile, expressOnly)) {
				ConveyerBeltTile conveyerTile = (ConveyerBeltTile) tile;
				var robot = player.getRobot();
				var pos = robot.getPos();

				if (!isConveyerMovementBlocked(pos, conveyerTile.getDirection())) {
					if (conveyerTile.isRotateLeft()) {
						performActionNow(robot, new RotateLeft(), Phase.BOARD_ELEMENTS_CONVEYOR);
					}

					if (conveyerTile.isRotateRight()) {
						performActionNow(robot, new RotateRight(), Phase.BOARD_ELEMENTS_CONVEYOR);
					}

					performActionNow(robot, new Move(conveyerTile.getDirection(), 1), Phase.BOARD_ELEMENTS_CONVEYOR);
				}
			}
		});
	}

	protected void processBoardElements() {
		processConveyorBelts(true);
		processConveyorBelts(false);
		processPlayerTiles(Phase.BOARD_ELEMENTS_PUSH);
		processPlayerTiles(Phase.BOARD_ELEMENTS_ROTATE);
	}

	protected void fireLasers() {
		//TODO: Implement me Noora
	}

	protected void processCheckpoints() {
		processPlayerTiles(Phase.CHECKPOINTS);
	}
}
