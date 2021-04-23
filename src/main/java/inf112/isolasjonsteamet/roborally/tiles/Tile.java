package inf112.isolasjonsteamet.roborally.tiles;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A unique tile type found on the board.
 */
public class Tile {

	private final List<Supplier<Action>> actions;

	public Tile(List<Supplier<Action>> actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	@SafeVarargs
	public Tile(Supplier<Action>... actions) {
		this.actions = ImmutableList.copyOf(actions);
	}

	public List<Action> createActions() {
		return actions.stream().map(Supplier::get).collect(Collectors.toList());
	}
}
