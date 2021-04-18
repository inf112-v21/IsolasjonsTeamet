package inf112.isolasjonsteamet.roborally.actions;

import com.badlogic.gdx.math.MathUtils;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the robot will move forward.
 */
public class Move implements Action {

	private final Orientation direction;
	private final int numMoves;

	public Move(Orientation direction, int numMoves) {
		this.direction = direction;
		this.numMoves = numMoves;
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		final Coordinate offset = direction.toCoord().mult(numMoves);
		final Coordinate pos = robot.getPos().add(offset);

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY);
		var clampedOffset = moveTo.sub(robot.getPos());

		robot.move(clampedOffset);
	}
}
