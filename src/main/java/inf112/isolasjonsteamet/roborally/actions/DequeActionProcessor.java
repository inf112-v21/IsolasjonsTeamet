package inf112.isolasjonsteamet.roborally.actions;

import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class DequeActionProcessor implements ActionProcessor, Iterator<ScheduledAction> {

	private final Deque<ScheduledAction> scheduledActions = new ArrayDeque<>();

	protected abstract boolean isExecutingAction();

	@Override
	public boolean hasNext() {
		return !scheduledActions.isEmpty();
	}

	@Override
	public ScheduledAction next() {
		if (scheduledActions.isEmpty()) {
			throw new NoSuchElementException();
		}

		return scheduledActions.poll();
	}

	@Override
	public void scheduleActionFirst(Robot robot, Action action, Phase phase) {
		if (scheduledActions.isEmpty() && !isExecutingAction()) {
			performActionNow(robot, action, phase);
		} else {
			scheduledActions.addFirst(new ScheduledAction(robot, action, phase));
		}
	}

	@Override
	public void scheduleActionLast(Robot robot, Action action, Phase phase) {
		if (scheduledActions.isEmpty() && !isExecutingAction()) {
			performActionNow(robot, action, phase);
		} else {
			scheduledActions.addLast(new ScheduledAction(robot, action, phase));
		}
	}
}
