package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.ConveyorBeltTile;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action for conveyor belts, which moves the player along without pushing anyone else.
 */
public class ConveyorAction implements Action {

	private final Orientation dir;
	private final boolean expressOnly;

	public ConveyorAction(Orientation dir, boolean expressOnly) {
		this.dir = dir;
		this.expressOnly = expressOnly;
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

	private boolean isConveyorMovementBlocked(Board board, Coordinate pos, Orientation dir, boolean expressOnly) {
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
					return isConveyorMovementBlocked(board, destination, conveyorTile.getDirection(), false);
				}
			}
		}

		return false;
	}

	private void processConveyor(ActionProcessor processor, Board board, Robot robot) {
		var pos = robot.getPos();

		if (!isConveyorMovementBlocked(board, pos, this.dir, expressOnly)) {
			for (Tile nextTile : board.getTilesAt(pos.add(this.dir.toCoord()))) {
				if (isConveyorBelt(nextTile, false)) {
					var nextConveyorTile = (ConveyorBeltTile) nextTile;

					if (nextConveyorTile.isRotateBelt()) {
						robot.setDir(nextConveyorTile.getDirection());
					}
				}
			}

			processor.scheduleAction(robot, new Move(this.dir, 1, false));
		}
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		boolean processExpress = expressOnly && phase.equals(Phase.CONVEYOR_BELTS_EXPRESS);
		boolean processAllBelts = phase.equals(Phase.CONVEYOR_BELTS);

		if (processExpress || processAllBelts) {
			processConveyor(processor, board, robot);
		}
	}
}
