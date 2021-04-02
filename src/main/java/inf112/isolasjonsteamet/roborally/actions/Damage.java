package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * Damage is an action that damages a robot x much according to the game obstacles.
 */
public class Damage implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		robot.damageRobot();
	}
}
