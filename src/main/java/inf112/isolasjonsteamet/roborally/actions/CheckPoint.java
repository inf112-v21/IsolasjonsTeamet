package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.CheckPointEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Perfom an Action on the board. Checkpoint is an action that let's us keep track of when a player has reached a
 * checkpoint.
 */
public class CheckPoint implements Action {

	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {

	}

	@Override
	public boolean show(Player player, ClientBoard board, int framesSinceStarted) {
		if (framesSinceStarted != 2) {
			Coordinate effPos = player.getPos();
			CheckPointEffect eff = new CheckPointEffect(effPos);
			eff.setEffect(effPos, player);
			return false;
		}
		return true;
	}
}
