package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * Damage is an action that damages a robot x much according to the game obstacles.
 */
public class Damage implements Action {


	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(Board board, Player player) {

		player.damageRobot();
	}
}
