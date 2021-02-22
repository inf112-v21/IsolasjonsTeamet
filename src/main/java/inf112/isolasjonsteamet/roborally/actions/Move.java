package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coords;
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
	public void perform(Board board, Player player) {

		switch (direction) {
			case NORTH:
				player.setNextPos(new Coords(player.getPos().getX(), player.getPos().getY() - 1));
				break;
			case SOUTH:
				player.setNextPos(new Coords(player.getPos().getX(), player.getPos().getY() + 1));
				break;
			case EAST:
				player.setNextPos(new Coords(player.getPos().getX() + 1, player.getPos().getY()));
				break;
			case WEST:
				player.setNextPos(new Coords(player.getPos().getX() - 1, player.getPos().getY()));
				break;
		}
	}
}
