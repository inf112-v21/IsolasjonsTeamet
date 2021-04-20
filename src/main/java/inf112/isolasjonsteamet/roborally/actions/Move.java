package inf112.isolasjonsteamet.roborally.actions;

import com.badlogic.gdx.math.MathUtils;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the player will move forward.
 */
public class Move implements Action {

	private final Orientation direction;
	private final int numMoves;

	public Move(Orientation direction, int numMoves) {
		this.direction = direction;
		this.numMoves = numMoves;
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		Coordinate pos = player.getPos();

		final Coordinate offset = direction.toCoord().mult(numMoves);
		final Coordinate finalDestination = player.getPos().add(offset);
		var clampedDestinationX = MathUtils.clamp(finalDestination.getX(), 0, board.getWidth() - 1);
		var clampedDestinationY = MathUtils.clamp(finalDestination.getY(), 0, board.getWidth() - 1);
		var clampedFinalDestination = new Coordinate(clampedDestinationX, clampedDestinationY);

		while (!board.hasWallInDir(pos, direction) && !pos.equals(clampedFinalDestination)) {
			pos = pos.add(direction.toCoord());
		}

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY); //absolute coordinate
		var clampedOffset = moveTo.sub(player.getPos()); //relative coordinate

		player.move(clampedOffset);
	}
}



