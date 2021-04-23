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
	private boolean moved = false;

	private Move(Orientation direction, int numMoves, boolean push, boolean encounteredPlayerObstacleBefore) {
		this.direction = direction;
		this.numMoves = numMoves;
		this.encounteredPlayerObstacleBefore = encounteredPlayerObstacleBefore;
		this.push = push;
	}

	/**
	 * Constructs a move action with the option if disabling pushing other robots.
	 */
	public Move(Orientation direction, int numMoves, boolean push) {
		this(direction, numMoves, push, false);
	}

	public Move(Orientation direction, int numMoves) {
		this(direction, numMoves, true);
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		Coordinate pos = robot.getPos();
		var directionCoord = direction.toCoord();
		final var originalPos = pos;

		final Coordinate offset = directionCoord.mult(numMoves);
		final Coordinate finalDestination = robot.getPos().add(offset);
		var clampedDestinationX = MathUtils.clamp(finalDestination.getX(), 0, board.getWidth() - 1);
		var clampedDestinationY = MathUtils.clamp(finalDestination.getY(), 0, board.getWidth() - 1);
		var clampedFinalDestination = new Coordinate(clampedDestinationX, clampedDestinationY);

		Robot robotAt = null;
		int movesMade = 0;
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

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY); //absolute coordinate
		var clampedOffset = moveTo.sub(robot.getPos()); //relative coordinate

		robot.move(clampedOffset);

		// If we crashed into another player, and we didn't do all the intended moves,
		// then we try to push the encountered player
		if (robotAt != null && movesMade < numMoves) {

			// We only push the encountered player if this is either the
			// first time we're encountering them, or we made movement progress
			if (!encounteredPlayerObstacleBefore || movesMade > 0) {
				processor.scheduleAction(robotAt, new Move(direction, 1));
				processor.scheduleAction(robot, new Move(direction, numMoves - movesMade, true, true));
			}
		}

		moved = !originalPos.equals(moveTo);
	}

	@Override
	public void initializeShow(Robot robot, ClientBoard board) {
		board.hide(robot);
		playerEffect = new RobotEffect(robot);
		board.addEffect(playerEffect);
	}

	@Override
	public boolean show(Robot robot, ClientBoard board, int framesSinceStart) {
		if (framesSinceStart == 20 * numMoves || !moved) {
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
