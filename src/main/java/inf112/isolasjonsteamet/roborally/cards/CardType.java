package inf112.isolasjonsteamet.roborally.cards;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionImpl;
import java.util.List;

/**
 * Describes a unique card type.
 */
public class CardType {

	private final int priority;
	private final List<ActionImpl> actions;

	public CardType(int priority, List<ActionImpl> actions) {
		this.priority = priority;
		this.actions = ImmutableList.copyOf(actions);
	}

	public CardType(int priority, ActionImpl... actions) {
		this.priority = priority;
		this.actions = ImmutableList.copyOf(actions);
	}

	public int getPriority() {
		return priority;
	}

	public List<ActionImpl> getActions() {
		return actions;
	}
}
