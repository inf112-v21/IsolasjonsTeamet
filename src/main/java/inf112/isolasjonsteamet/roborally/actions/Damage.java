package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.effects.DamageEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Damage is an action that damages a robot x much according to the game obstacles.
 */
public class Damage implements Action {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		player.damageRobot();
	}

	@Override
	public boolean show(Player player, Board board, int framesSinceStarted) {
		if (framesSinceStarted != 4) {
			Coordinate effPos = player.getPos();
			DamageEffect eff = new DamageEffect(effPos);
			eff.setEffect(effPos);
			return false;
		}
		return true;
	}
}
