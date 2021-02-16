package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * An action a player piece can take on the board.
 */
public interface Action {

	void perform(Board board, Player player);
}
