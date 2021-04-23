package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which fires tile lasers, damaging the fist player in their way.
 */
public class FireTileLaser implements Action {

	private boolean hasRobotInWayOfEmitter(Board board, Coordinate pos) {
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

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		if (phase != Phase.LASERS) {
			return;
		}

		if (!hasRobotInWayOfEmitter(board, robot.getPos())) {
			processor.scheduleAction(robot, new Damage());
		}
	}
}
