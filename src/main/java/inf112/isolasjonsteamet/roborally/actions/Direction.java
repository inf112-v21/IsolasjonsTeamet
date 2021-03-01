package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * A Class that provides the actions with directions and positions of a player.
 */
public class Direction implements Action {

	private final Orientation direction;

	public Direction(Orientation direction) {
		this.direction = direction;
	}


	/**
	 * Perfom an Action on the board.
	 */
	@Override
	public void perform(Board board, Player player) {

	}
}
