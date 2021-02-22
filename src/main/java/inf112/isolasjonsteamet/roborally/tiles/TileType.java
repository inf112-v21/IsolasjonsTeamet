package inf112.isolasjonsteamet.roborally.tiles;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionImpl;
import java.util.List;

/**
 * A unique tile type found on the board.
 */
public class TileType {

	private final List<ActionImpl> actions;

	public TileType(List<ActionImpl> actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	public TileType(ActionImpl... actions) {
		this.actions = ImmutableList.copyOf(actions);
	}
}
