package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the player will rotate to the left.
 */
public class RotateLeft implements Action {

	@Override
	public void perform(Board board, Player player) {
		player.setDir(player.getDir().rotateLeft());
		System.out.println(player.getName() + " rotated right.");
	}
}
