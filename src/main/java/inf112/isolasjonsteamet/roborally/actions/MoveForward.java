package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * Class to move or item forward.
 */
public class MoveForward implements Action {

	private final int numMoves;

	public MoveForward(int numMoves) {
		this.numMoves = numMoves;
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		processor.performActionNow(robot, new Move(robot.getDir(), numMoves));
		System.out.println(robot.getName() + " moved " + numMoves + " forward. Current pos: " + robot.getPos());
	}

	@Override
	public String toString() {
		return "MoveForward " + numMoves;
	}
}
