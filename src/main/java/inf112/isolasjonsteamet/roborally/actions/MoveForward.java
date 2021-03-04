package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * Class to move or item forward.
 */
public class MoveForward implements Action {

	private final int numMoves;

	public MoveForward(int numMoves) {
		this.numMoves = numMoves;
	}
  
	@Override
	public void perform(Board board, Player player) {
		new Move(player.getDir(), numMoves).perform(board, player);
		System.out.println(player.getName() + " moved " + numMoves + " forward. Current pos: " + player.getPos());
	}

	@Override
	public String toString() {
		return "MoveForward " + numMoves;
	}
}
