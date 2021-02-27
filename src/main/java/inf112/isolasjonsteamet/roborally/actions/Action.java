package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * An action a player piece can take on the board.
 */
public interface Action {

	/**
	 * Perfom an Action on the board.
	 */
	void perform(BoardClientImpl board, PlayerImpl player);
}
