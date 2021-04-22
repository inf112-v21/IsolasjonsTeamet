package inf112.isolasjonsteamet.roborally.app;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.Move;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.ConveyorBeltTile;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.function.BiConsumer;

/**
 * Holds common code for the client and server.
 */
public abstract class RoboRallyShared implements ActionProcessor {

	/**
	 * Performs an action for each tile each player is standing on.
	 */
	protected abstract void foreachPlayerTile(BiConsumer<Player, Tile> handler);

	/** Access to the board. */
	protected abstract Board board();

	/** Signals what the current phase is. */
	protected abstract void setCurrentPhase(Phase phase);

	/**
	 * Disables board validation checks.
	 */
	protected abstract void skipBoardValidChecks();

	/**
	 * Enables board validation checks.
	 */
	protected abstract void enableBoardValidChecks();

	protected void processPlayerTiles(Phase phase) {
		setCurrentPhase(phase);
		foreachPlayerTile((player, tile) -> {
			for (Action action : tile.getActions()) {
				performActionNow(player.getRobot(), action, phase);
			}
		});
	}

	private boolean isConveyorBelt(Tile tile, boolean expressOnly) {
		if (!(tile instanceof ConveyorBeltTile)) {
			return false;
		}

		return !expressOnly || ((ConveyorBeltTile) tile).isExpress();
	}

	private boolean isConveyorBeltFacing(Tile tile, boolean expressOnly, Orientation facing) {
		return isConveyorBelt(tile, expressOnly) && ((ConveyorBeltTile) tile).getDirection().equals(facing);
	}

	private boolean isConveyorMovementBlocked(Coordinate pos, Orientation dir, boolean expressOnly) {
		Board board = board();
		var destination = pos.add(dir.toCoord());
		var opposingDestination = pos.add(dir.toCoord().mult(2));

		var robotAtOpposingDestination = board.getRobotAt(opposingDestination);
		var robotAtDestination = board.getRobotAt(destination);

		if (robotAtOpposingDestination != null) {
			boolean hasOpposingConveyor =
					board.getTilesAt(opposingDestination)
							.stream()
							.anyMatch(t -> isConveyorBeltFacing(t, expressOnly, dir.getOpposingDir()));

			if (hasOpposingConveyor) {
				return true;
			}
		}

		//TODO: This case needs a lot of testing
		if (robotAtDestination != null) {
			boolean hasDestinationOpposingOrNoConveyor =
					board.getTilesAt(destination)
							.stream()
							.anyMatch(t ->
									!isConveyorBelt(t, false) || isConveyorBeltFacing(t, false, dir.getOpposingDir())
							);

			if (hasDestinationOpposingOrNoConveyor) {
				return true;
			}

			//We know there is a conveyor belt in here somewhere
			for (Tile tile : board.getTilesAt(destination)) {
				if (isConveyorBelt(tile, false)) {
					var conveyorTile = (ConveyorBeltTile) tile;
					return isConveyorMovementBlocked(destination, conveyorTile.getDirection(), false);
				}
			}
		}

		return false;
	}

	private void processConveyorBelts(boolean expressOnly) {
		skipBoardValidChecks();

		foreachPlayerTile((player, tile) -> {
			if (isConveyorBelt(tile, expressOnly)) {
				ConveyorBeltTile conveyorTile = (ConveyorBeltTile) tile;
				var robot = player.getRobot();
				var pos = robot.getPos();

				if (!isConveyorMovementBlocked(pos, conveyorTile.getDirection(), expressOnly)) {
					for (Tile nextTile : board().getTilesAt(pos.add(conveyorTile.getDirection().toCoord()))) {
						if (isConveyorBelt(nextTile, false)) {
							var nextConveyorTile = (ConveyorBeltTile) nextTile;

							if (nextConveyorTile.isRotateBelt()) {
								robot.setDir(nextConveyorTile.getDirection());
							}
						}
					}

					performActionNow(
							robot, new Move(conveyorTile.getDirection(), 1, true), Phase.BOARD_ELEMENTS_CONVEYOR
					);
				}
			}
		});

		enableBoardValidChecks();
		board().checkValid();
	}

	protected void processBoardElements() {
		setCurrentPhase(Phase.BOARD_ELEMENTS_CONVEYOR);
		processConveyorBelts(true);
		processConveyorBelts(false);
		processPlayerTiles(Phase.BOARD_ELEMENTS_PUSH);
		processPlayerTiles(Phase.BOARD_ELEMENTS_ROTATE);
	}

	private boolean hasRobotInWayOfEmitter(Coordinate pos) {
		Board board = board();
		Coordinate originalPos = pos;

		boolean hasFoundEmitter = false;

		for (Orientation dir : Orientation.values()) {
			pos = originalPos;
			boolean hasEncounteredRobot = false;

			while (true) {
				pos = pos.add(dir.toCoord());

				var tiles = board.getTilesAt(pos);
				hasFoundEmitter = hasFoundEmitter || tiles.contains(Tiles.LASER_EMITTER);
				if (!tiles.contains(Tiles.LASER) && !tiles.contains(Tiles.LASER_EMITTER)) {
					break;
				}

				if (board.getRobotAt(pos) != null) {
					hasEncounteredRobot = true;
				}

				if (tiles.contains(Tiles.LASER_EMITTER)) {
					if (!hasEncounteredRobot) {
						return false;
					}

					break;
				}
			}
		}

		return hasFoundEmitter;
	}

	/*
	 * Processes all the laser tiles on this board and damages the players that stand in their way.
	 */
	protected void fireLasers() {
		setCurrentPhase(Phase.LASERS);
		Board board = board();
		for (Robot robot : board.getRobots()) {
			var tiles = board.getTilesAt(robot.getPos());

			if (tiles.contains(Tiles.LASER_EMITTER)) {
				robot.damageRobot();
			} else if (tiles.contains(Tiles.LASER) && !hasRobotInWayOfEmitter(robot.getPos())) {
				robot.damageRobot();
			}
		}
	}

	protected void processCheckpoints() {
		processPlayerTiles(Phase.CHECKPOINTS);
	}

	protected void processCleanup() {
		processPlayerTiles(Phase.CLEANUP);
	}
}
