package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * Checkpoint is an action that let's us keep track of when a player has reached a chekcpoint.
 *
 */
public class CheckPoint implements Action {

	private int id;

	public CheckPoint(int id) {
		this.id = id;
	}


	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(BoardClientImpl board, PlayerImpl player) {

	}
}
