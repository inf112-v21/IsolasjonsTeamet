package inf112.isolasjonsteamet.roborally.cards;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;

/**
 * Represents a card that makes the player rotate 180 degrees and face in the oposite direction.
 */
public class UTurn {

    private final int priority;
    private final List<Action> actions;

    public CardType(int priority, List<Action> actions) {
        this.priority = priority;
        this.actions = ImmutableList.copyOf(actions);
    }

    public CardType(int priority, Action... actions) {
        this.priority = priority;
        this.actions = ImmutableList.copyOf(actions);
    }

    public int getPriority() {
        return priority;
    }

    public List<Action> getActions() {
        return actions;
    }
}