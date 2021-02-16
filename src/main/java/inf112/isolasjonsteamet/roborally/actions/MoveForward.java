package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;

/**
 * An action which indicates the player will move forward.
 */
public class MoveForward implements Action {

	private final int amount;

	public MoveForward(int amount) {
		this.amount = amount;
	}

	@Override
	public void perform(Board board, Player player) {

	}
}
