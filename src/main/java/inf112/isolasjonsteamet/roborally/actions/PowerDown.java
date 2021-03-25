package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.effects.PowerDownEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Power Down is an Action that let's the player take a break with their robot while the game is still going.
 */
public class PowerDown implements Action {

	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {

	}

	@Override
	public boolean show(Player player, Board board, int framesSinceStarted) {
		if (framesSinceStarted != 3) {
			Coordinate effPos = player.getPos();
			PowerDownEffect eff = new PowerDownEffect(effPos);
			eff.setEffect(effPos);
			return false;
		}
		return true;
	}
}
