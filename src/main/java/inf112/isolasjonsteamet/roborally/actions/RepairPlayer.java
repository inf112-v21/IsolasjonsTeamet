package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * Repair player is an action that repairs a player x much according to cards they receive.
 */
public class RepairPlayer implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(Board board, Player player) {
		player.repairRobot();
	}
}
