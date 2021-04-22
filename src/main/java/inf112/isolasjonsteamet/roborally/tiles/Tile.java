package inf112.isolasjonsteamet.roborally.tiles;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;

/**
 * A unique tile type found on the board.
 */
public class Tile {

	private final List<Action> actions;

	public Tile(List<Action> actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	public Tile(Action... actions) {
		this.actions = ImmutableList.copyOf(actions);
	}
}
