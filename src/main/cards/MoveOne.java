package inf112.isolasjonsteamet.roborally.cards;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;

/**
 * Represents a card that makes the player move one tile forward on the board
 */
public class MoveOne {

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