package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.effects.PowerDownEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Power Down is an Action that let's the player take a break with their robot while the game is still going.
 */
public class PowerDown implements Action {

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {

	}

	@Override
	public boolean show(Robot robot, Board board, int framesSinceStarted) {
		if (framesSinceStarted != 3) {
			Coordinate effPos = robot.getPos();
			PowerDownEffect eff = new PowerDownEffect(effPos);
			eff.setEffect(effPos, robot);
			return false;
		}
		return true;
	}
}
