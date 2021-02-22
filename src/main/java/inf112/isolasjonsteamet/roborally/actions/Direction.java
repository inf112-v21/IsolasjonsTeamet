package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * A Class that provides the actions with directions and positions of a player.
 */
public class Direction implements ActionImpl {

	private final Orientation direction;

	public Direction(Orientation direction) {
		this.direction = direction;
	}

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {

	}

}
