package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.effects.RepairEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Repair robot is an action that repairs a robot.
 */
public class RepairRobot implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		robot.repairRobot();
	}

	@Override
	public boolean show(Robot robot, Board board, int framesSinceStarted) {
		if (framesSinceStarted != 3) {
			Coordinate effPos = robot.getPos();
			RepairEffect eff = new RepairEffect(effPos);
			eff.setEffect(effPos, robot);
			return false;
		}
		return true;
	}
}
