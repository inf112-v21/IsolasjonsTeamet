package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * An action a robot piece can take on the board.
 */
public interface Action {

	default void initialize(Board board, Robot robot) {
	}

	/**
	 * Perform an action on the board.
	 */
	void perform(ActionProcessor processor, Board board, Robot robot);

	default void initializeShow(Robot robot, ClientBoard board) {
	}

	/**
	 * Shows the action being executed.
	 *
	 * @return If the effect is done being showed.
	 */
	default boolean show(Robot robot, ClientBoard board, int framesSinceStarted) {
		return true;
	}
}
