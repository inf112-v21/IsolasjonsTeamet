package inf112.isolasjonsteamet.roborally.actions;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.players.Robot;

public class ScheduledAction {

	private final Robot robot;
	private final Action action;
	private final Phase phase;

	public ScheduledAction(Robot robot, Action action, Phase phase) {
		this.robot = robot;
		this.action = action;
		this.phase = phase;
	}

	public Robot getRobot() {
		return robot;
	}

	public Action getAction() {
		return action;
	}

	public Phase getPhase() {
		return phase;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ScheduledAction that = (ScheduledAction) o;
		return Objects.equal(robot, that.robot) && Objects.equal(action, that.action) && phase == that.phase;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(robot, action, phase);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("robot", robot)
				.add("action", action)
				.add("phase", phase)
				.toString();
	}
}
