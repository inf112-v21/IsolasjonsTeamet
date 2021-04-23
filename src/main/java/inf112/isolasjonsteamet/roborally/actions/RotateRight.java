package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.effects.RobotEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action which indicates the robot will rotate to the right.
 */
public class RotateRight implements Action {

	public static final Logger LOGGER = LoggerFactory.getLogger(RotateRight.class);
	private RobotEffect playerEffect;

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		robot.setDir(robot.getDir().rotateRight());
		LOGGER.debug(robot.getDebugName() + " rotated right. Current dir: " + robot.getDir());
	}

	@Override
	public String toString() {
		return "RotateRight";
	}

	@Override
	public void initializeShow(Robot robot, ClientBoard board) {
		board.hide(robot);
		playerEffect = new RobotEffect(robot);
		board.addEffect(playerEffect);
	}

	@Override
	public boolean show(Robot robot, ClientBoard board, int framesSinceStart) {
		if (framesSinceStart == 10) {
			board.show(robot);
			board.removeEffect(playerEffect);
			return true;
		}
		playerEffect.rotate(-90F / 10F);

		return false;
	}
}
