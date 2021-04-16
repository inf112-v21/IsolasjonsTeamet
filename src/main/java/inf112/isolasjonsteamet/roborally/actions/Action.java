package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * An action a player piece can take on the board.
 */
public interface Action {

    default void initialize(Board board, Player player) {
    }

	/**
	 * Perform an action on the board.
	 */
	void perform(ActionProcessor processor, Board board, Player player);

	default void initializeShow(Player player, ClientBoard board) {
    }

	/**
	 * Shows the action being executed.
	 *
	 * @return If the effect is done being showed.
	 */
	default boolean show(Player player, ClientBoard board, int framesSinceStarted) {
		return true;
	}
}
