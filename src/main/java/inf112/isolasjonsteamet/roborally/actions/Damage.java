package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.DamageEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

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

	@Override
	public boolean show(Robot robot, ClientBoard board, int framesSinceStarted) {
		if (framesSinceStarted != 4) {
			Coordinate effPos = robot.getPos();
			DamageEffect eff = new DamageEffect(effPos);
			eff.setEffect(effPos, robot);
			return false;
		}
		return true;
	}
}
