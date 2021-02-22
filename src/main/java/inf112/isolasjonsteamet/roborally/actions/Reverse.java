package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * An Action that let's the robot go backwards.
 */
public class Reverse implements Action {

	public Reverse() {}

	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(Board board, Player player) {
		player.setMovDir(player.getDir().getOpposingDir());
		new Move().perform(board, player);
	}
}
