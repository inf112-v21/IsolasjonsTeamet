package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action which indicates the robot will rotate to the right.
 */
public class RotateRight implements Action {

	public static final Logger LOGGER = LoggerFactory.getLogger(RotateRight.class);

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		robot.setDir(robot.getDir().rotateRight());
		LOGGER.debug(robot.getDebugName() + " rotated right. Current dir: " + robot.getDir());
	}

	@Override
	public String toString() {
		return "RotateRight";
	}
}
