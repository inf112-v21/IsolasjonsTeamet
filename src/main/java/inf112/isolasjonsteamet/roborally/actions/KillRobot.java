package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.KillEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Kill robot is an action that let's a player kill a robot or the main master robot.
 */
public class KillRobot implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		player.killRobot();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean show(Player player, ClientBoard board, int framesSinceStarted) {
		if (framesSinceStarted != 2) {
			Coordinate effPos = player.getPos();
			KillEffect eff = new KillEffect(effPos);
			eff.setEffect(effPos, player);
			return false;
		}
		return true;
	}
}
