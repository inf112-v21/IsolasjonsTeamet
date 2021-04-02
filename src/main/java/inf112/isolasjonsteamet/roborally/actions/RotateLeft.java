package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * An action which indicates the robot will rotate to the left.
 */
public class RotateLeft implements Action {

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		robot.setDir(robot.getDir().rotateLeft());
		System.out.println(robot.getDebugName() + " rotated left. Current dir: " + robot.getDir());
	}

	@Override
	public String toString() {
		return "RotateLeft";
	}
}
