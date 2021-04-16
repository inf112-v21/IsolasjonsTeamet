package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.RepairEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Repair player is an action that repairs a player x much according to cards they receive.
 */
public class RepairPlayer implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		player.repairRobot();
	}

	@Override
	public boolean show(Player player, ClientBoard board, int framesSinceStarted) {
		if (framesSinceStarted != 3) {
			Coordinate effPos = player.getPos();
			RepairEffect eff = new RepairEffect(effPos);
			eff.setEffect(effPos, player);
			return false;
		}
		return true;
	}
}
