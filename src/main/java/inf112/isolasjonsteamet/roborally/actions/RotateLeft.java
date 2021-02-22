package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the player will rotate to the left.
 */
public class RotateLeft implements ActionImpl {

	public Orientation direction;

	public RotateLeft() {

		this.direction = direction;
	}

	@Override
	public void perform(BoardImpl board, PlayerImpl player) {
		switch (player.getDir()) {
			case NORTH:
				player.setDir(Orientation.WEST);
				break;
			case WEST:
				player.setDir(Orientation.SOUTH);
				break;
			case SOUTH:
				player.setDir(Orientation.EAST);
				break;
			case EAST:
				player.setDir(Orientation.NORTH);
				break;
		}
		System.out.println(player.getName() + " rotated right.");
	}
}
