package inf112.isolasjonsteamet.roborally.actions;

import com.badlogic.gdx.math.MathUtils;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.effects.RobotEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the robot will move forward.
 */
public class Move implements Action {

	private final Orientation direction;
	private final int numMoves;
	private final boolean encounteredPlayerObstacleBefore;
	private final boolean push;

	private RobotEffect playerEffect;
	private int movesMade = 0;

	private Move(Orientation direction, int numMoves, boolean push, boolean encounteredPlayerObstacleBefore) {
		if (numMoves < 0) {
			this.direction = direction.getOpposingDir();
			this.numMoves = numMoves * -1;
		} else {
			this.direction = direction;
			this.numMoves = numMoves;
		}

		this.encounteredPlayerObstacleBefore = encounteredPlayerObstacleBefore;
		this.push = push;
	}

	/**
	 * Constructs a move action with the option of disabling pushing other robots.
	 */
	public Move(Orientation direction, int numMoves, boolean push) {
		this(direction, numMoves, push, false);
	}

	public Move(Orientation direction, int numMoves) {
		this(direction, numMoves, true);
	}

	private static Coordinate clampCoord(Board board, Coordinate coord) {
		var clampedX = MathUtils.clamp(coord.getX(), 0, board.getWidth() - 1);
		var clampedY = MathUtils.clamp(coord.getY(), 0, board.getWidth() - 1);
		return new Coordinate(clampedX, clampedY);
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		Coordinate pos = robot.getPos();
		var directionCoord = direction.toCoord();

		var offset = directionCoord.mult(numMoves);
		var finalDestination = robot.getPos().add(offset);
		var clampedFinalDestination = clampCoord(board, finalDestination);

		Robot robotAt = null;
		movesMade = 0;
		while (!board.hasWallInDir(pos, direction) && !pos.equals(clampedFinalDestination) && robotAt == null) {
			var newPos = pos.add(directionCoord);

			if (push) {
				robotAt = board.getRobotAt(newPos);
			}

			if (robotAt == null) {
				pos = newPos;
				movesMade++;
			}
		}

		var moveTo = clampCoord(board, pos); //absolute coordinate
		var clampedOffset = moveTo.sub(robot.getPos()); //relative coordinate

		robot.move(clampedOffset);

		// If we crashed into another player, and we didn't do all the intended moves,
		// then we try to push the encountered player
		if (robotAt != null && movesMade < numMoves) {

			// We only push the encountered player if this is either the
			// first time we're encountering them, or we made movement progress
			if (!encounteredPlayerObstacleBefore || movesMade > 0) {
				processor.scheduleActionFirst(robotAt, new Move(direction, 1), phase);
				processor.scheduleActionLast(robot, new Move(direction, numMoves - movesMade, true, true), phase);
			}
		}
	}

	@Override
	public void initializeShow(Robot robot, ClientBoard board) {
		board.hide(robot);
		playerEffect = new RobotEffect(board, robot);
		board.addEffect(playerEffect);
	}

	@Override
	public boolean show(Robot robot, ClientBoard board, int framesSinceStart) {
		if (framesSinceStart == 20 * movesMade) {
			board.show(robot);
			board.removeEffect(playerEffect);
			return true;
		}

		Coordinate coord = direction.toCoord();
		int x = coord.getX();
		int y = coord.getY();

		playerEffect.move(x * 0.05F, y * 0.05F);

		return false;
	}
}
