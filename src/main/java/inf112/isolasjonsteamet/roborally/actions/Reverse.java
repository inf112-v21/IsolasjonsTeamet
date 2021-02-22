package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;

/**
 * An Action that let's the robot go backwards.
 */
public class Reverse implements ActionImpl {

	public Reverse() {}

	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		player.setMovDir(player.getDir().getOpposingDir());
		new Move().perform(board, player);
	}
}
