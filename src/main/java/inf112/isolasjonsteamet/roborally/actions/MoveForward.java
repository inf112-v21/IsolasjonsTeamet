package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

public class MoveForward implements Action {

	int numMoves;

	public MoveForward(int numMoves) {
		this.numMoves = numMoves;
	}

	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(Board board, Player player) {
		player.setMovDir(player.getDir());
		new Move().perform(board, player);
	}

}
