package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the player will move forward.
 */
public class Move implements Action {

	private Orientation direction;

	public Move() {
		this.direction = null;
	}

	public Move(Orientation direction) {
		this.direction = direction;
	}

	@Override
	public void perform(BoardClientImpl board, PlayerImpl player) {

		switch (direction) {
			case NORTH:
				player.setNextPos(new Coordinate(player.getPos().getX(), player.getPos().getY() - 1));
				break;
			case SOUTH:
				player.setNextPos(new Coordinate(player.getPos().getX(), player.getPos().getY() + 1));
				break;
			case EAST:
				player.setNextPos(new Coordinate(player.getPos().getX() + 1, player.getPos().getY()));
				break;
			case WEST:
				player.setNextPos(new Coordinate(player.getPos().getX() - 1, player.getPos().getY()));
				break;
			default:
				break;
		}
	}
}
