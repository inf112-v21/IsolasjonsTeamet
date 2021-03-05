package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action that let's the player make a turn in the opposite direction.
 */
public class Uturn implements Action {

	@Override
	public void perform(Board board, Player player) {

		Orientation currentDir = player.getDir();
		player.setDir(currentDir.getOpposingDir());
		System.out.println(player.getName() + " rotated to " + player.getDir());
	}
}
