package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action that let's the robot make a turn in the opposite direction.
 */
public class Uturn implements Action {

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		Orientation currentDir = robot.getDir();
		robot.setDir(currentDir.getOpposingDir());
		System.out.println(robot.getDebugName() + " rotated to " + robot.getDir());
	}
}
