package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * Something capable of running actions in the game.
 */
public interface ActionProcessor {

	/**
	 * Runs an action now immediately.
	 *
	 * @param player The player to run the action for.
	 */
	void performActionNow(Player player, Action action);
}
