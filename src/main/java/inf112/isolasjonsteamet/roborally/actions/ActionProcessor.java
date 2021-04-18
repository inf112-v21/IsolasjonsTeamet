package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * Something capable of running actions in the game.
 */
public interface ActionProcessor {

	/**
	 * Runs an action now immediately.
	 *
	 * @param robot The robot to run the action for.
	 */
	void performActionNow(Robot robot, Action action, Phase phase);
}
