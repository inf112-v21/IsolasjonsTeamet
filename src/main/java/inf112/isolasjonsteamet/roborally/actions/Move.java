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
	private final boolean encounteredPlayerObstacleBefore;

	public Move(Orientation direction, int numMoves, boolean encounteredPlayerObstacleBefore) {
		this.direction = direction;
		this.numMoves = numMoves;
		this.encounteredPlayerObstacleBefore = encounteredPlayerObstacleBefore;
	}

	public Move(Orientation direction, int numMoves) {
		this(direction, numMoves, false);
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		Coordinate pos = player.getPos();
		var directionCoord = direction.toCoord();

		final Coordinate offset = directionCoord.mult(numMoves);
		final Coordinate finalDestination = player.getPos().add(offset);
		var clampedDestinationX = MathUtils.clamp(finalDestination.getX(), 0, board.getWidth() - 1);
		var clampedDestinationY = MathUtils.clamp(finalDestination.getY(), 0, board.getWidth() - 1);
		var clampedFinalDestination = new Coordinate(clampedDestinationX, clampedDestinationY);

		Player playerAt = null;
		int movesMade = 0;
		while (!pos.equals(clampedFinalDestination) && playerAt == null) {
			var newPos = pos.add(directionCoord);

			playerAt = board.getPlayerAt(newPos);
			if (playerAt == null) {
				pos = newPos;
				movesMade++;
			}
		}

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY); //absolute coordinate
		var clampedOffset = moveTo.sub(player.getPos()); //relative coordinate

		player.move(clampedOffset);

		// If we crashed into another player, and we didn't do all the intended moves,
		// then we try to push the encountered player
		if (playerAt != null && movesMade < numMoves) {

			// We only push the encountered player if this is either the
			// first time we're encountering them, or we made movement progress
			if (!encounteredPlayerObstacleBefore || movesMade > 0) {
				processor.scheduleAction(playerAt, new Move(direction, 1));
				processor.scheduleAction(player, new Move(direction, numMoves - movesMade, true));
			}
		}
	}
}
