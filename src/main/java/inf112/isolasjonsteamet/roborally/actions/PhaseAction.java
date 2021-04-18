package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import java.util.function.Supplier;

public class PhaseAction implements Action {

	private final Phase activeOnPhase;
	private final Supplier<Action> actions;

	public PhaseAction(Phase activeOnPhase, Supplier<Action> actions) {
		this.activeOnPhase = activeOnPhase;
		this.actions = actions;
	}

	@Override
	public void perform(ActionProcessor processor, Board board, Robot robot, Phase phase) {
		if (phase.equals(activeOnPhase)) {
			processor.performActionNow(robot, actions.get(), phase);
		}
	}
}
