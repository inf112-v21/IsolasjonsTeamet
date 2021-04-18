package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.effects.KillEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Kill robot is an action that let's a player kill a robot or the main master robot.
 */
public class KillRobot implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		robot.killRobot();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean show(Robot robot, Board board, int framesSinceStarted) {
		if (framesSinceStarted != 2) {
			Coordinate effPos = robot.getPos();
			KillEffect eff = new KillEffect(effPos);
			eff.setEffect(effPos, robot);
			return false;
		}
		return true;
	}
}
