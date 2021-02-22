package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * An action a player piece can take on the board.
 */
public interface ActionImpl {

	/**
	 * Perfom an Action on the board.
	 * @param player
	 */
	void perform(BoardImpl board, PlayerImpl player);
}
