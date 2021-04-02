package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * Kill robot is an action that let's a player kill a robot or the main master robot.
 */
public class KillRobot implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		robot.killRobot();
	}
}
