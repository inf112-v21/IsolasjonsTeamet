package inf112.isolasjonsteamet.roborally.actions;

import com.badlogic.gdx.math.MathUtils;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.effects.RobotEffect;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * An action which indicates the robot will move forward.
 */
public class Move implements Action {

	private final Orientation direction;
	private final int numMoves;

	private RobotEffect playerEffect;
	private boolean moved = false;

	public Move(Orientation direction, int numMoves) {
		this.direction = direction;
		this.numMoves = numMoves;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot) {
		Coordinate pos = robot.getPos();
		final var originalPos = pos;

		final Coordinate offset = direction.toCoord().mult(numMoves);
		final Coordinate finalDestination = robot.getPos().add(offset);
		var clampedDestinationX = MathUtils.clamp(finalDestination.getX(), 0, board.getWidth() - 1);
		var clampedDestinationY = MathUtils.clamp(finalDestination.getY(), 0, board.getWidth() - 1);
		var clampedFinalDestination = new Coordinate(clampedDestinationX, clampedDestinationY);

		while (!board.hasWallInDir(pos, direction) && !pos.equals(clampedFinalDestination)) {
			pos = pos.add(direction.toCoord());
		}

		int clampedX = MathUtils.clamp(pos.getX(), 0, board.getWidth() - 1);
		int clampedY = MathUtils.clamp(pos.getY(), 0, board.getHeight() - 1);

		var moveTo = new Coordinate(clampedX, clampedY); //absolute coordinate
		var clampedOffset = moveTo.sub(robot.getPos()); //relative coordinate

		robot.move(clampedOffset);

		moved = !originalPos.equals(moveTo);
		System.out.println("Moved = " + moved);
	}

	@Override
	public void initializeShow(Robot robot, ClientBoard board) {
		board.hide(robot);
		playerEffect = new RobotEffect(robot);
		board.addEffect(playerEffect);
	}

	@Override
	public boolean show(Robot robot, ClientBoard board, int framesSinceStart) {
		if (framesSinceStart == 20 * numMoves || !moved) {
			System.out.println("Moved was " + moved);
			board.show(robot);
			board.removeEffect(playerEffect);
			return true;
		}

		Coordinate coord = direction.toCoord();
		int x = coord.getX();
		int y = coord.getY();

		playerEffect.move(x * 0.05F, y * 0.05F);

		return false;
	}
}



