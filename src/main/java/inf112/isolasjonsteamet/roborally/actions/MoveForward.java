package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

public class MoveForward implements ActionImpl{

	int numMoves;

	public MoveForward(int numMoves) {
		this.numMoves = numMoves;
	}

	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		player.setMovDir(player.getDir());
		new Move().perform(board, player);
	}

}
