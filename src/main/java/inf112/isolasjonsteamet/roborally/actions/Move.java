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
	public void perform(Board board, Player player) {
		final Coordinate offset = direction.toCoord().mult(numMoves);
		final Coordinate pos = player.getPos().add(offset);

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY);
		var clampedOffset = moveTo.sub(player.getPos());

		player.move(clampedOffset);
	}
}
