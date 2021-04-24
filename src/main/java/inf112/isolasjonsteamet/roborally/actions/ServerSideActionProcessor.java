package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import java.util.function.Supplier;

public class ServerSideActionProcessor extends DequeActionProcessor {

	//Board is in a supplier to make the class easier to use in tests, where the board often changes
	private final Supplier<Board> board;

	private boolean performingAction = false;

	public ServerSideActionProcessor(Supplier<Board> board) {
		this.board = board;
	}

	@Override
	protected boolean isExecutingAction() {
		return performingAction;
	}

	@Override
	public void performActionNow(Robot robot, Action action, Phase phase) {
		boolean hasWork;
		do {
			Board board = this.board.get();
			action.initialize(board, robot);

			performingAction = true;
			action.perform(this, board, robot, phase);
			performingAction = false;

			board.checkValid();

			hasWork = hasNext();
			if (hasWork) {
				ScheduledAction nextActionEntry = next();
				robot = nextActionEntry.getRobot();
				action = nextActionEntry.getAction();
				phase = nextActionEntry.getPhase();
			}
		} while (hasWork);
	}
}
