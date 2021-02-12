package inf112.isolasjonsteamet.roborally.tiles;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;

/**
 * A unique tile type found on the board.
 */
public class TileType {

	private final List<Action> actions;

	public TileType(List<Action> actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	public TileType(Action... actions) {
		this.actions = ImmutableList.copyOf(actions);
	}
}
