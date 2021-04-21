package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.PlayerEffect;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * An action which indicates the player will rotate to the right.
 */
public class RotateRight implements Action {

	private PlayerEffect playerEffect;

	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		player.setDir(player.getDir().rotateRight());
		System.out.println(player.getName() + " rotated right. Current dir: " + player.getDir());
	}

	@Override
	public String toString() {
		return "RotateRight";
	}

	@Override
	public void initializeShow(Player player, ClientBoard board) {
		board.hide(player);
		playerEffect = new PlayerEffect(player);
		board.addEffect(playerEffect);
	}

	@Override
	public boolean show(Player player, ClientBoard board, int framesSinceStart) {
		if (framesSinceStart == 10) {
			board.show(player);
			board.removeEffect(playerEffect);
			return true;
		}
		playerEffect.rotate();

		return false;
	}
}
