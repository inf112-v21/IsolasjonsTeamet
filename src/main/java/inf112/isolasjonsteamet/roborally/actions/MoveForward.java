package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to move or item forward.
 */
public class MoveForward implements Action {

	public static final Logger LOGGER = LoggerFactory.getLogger(MoveForward.class);

	private final int numMoves;

	public MoveForward(int numMoves) {
		this.numMoves = numMoves;
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		processor.scheduleAction(robot, new Move(robot.getDir(), numMoves));
		LOGGER.debug(robot.getDebugName() + " moved " + numMoves + " forward. Current pos: " + robot.getPos());
	}

	@Override
	public String toString() {
		return "MoveForward " + numMoves;
	}
}
