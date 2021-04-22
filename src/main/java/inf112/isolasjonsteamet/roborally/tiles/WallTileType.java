package inf112.isolasjonsteamet.roborally.tiles;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;

/**
 * Class for WallTileTypes.
 */
public class WallTileType extends TileType {

	public static final List<WallTileType> ALL_WALL_TYPES = computeAllWallTypes();

	private final boolean hasNorthWall;
	private final boolean hasWestWall;
	private final boolean hasSouthWall;
	private final boolean hasEastWall;

	/**
	 * Constructor for creating WallTileType.
	 */
	public WallTileType(boolean hasNorthWall, boolean hasWestWall, boolean hasSouthWall, boolean hasEastWall) {
		this.hasNorthWall = hasNorthWall;
		this.hasWestWall = hasWestWall;
		this.hasSouthWall = hasSouthWall;
		this.hasEastWall = hasEastWall;
	}

	/**
	 * Checks if there is an wall in given direction.
	 */
	public boolean hasWallInDir(Orientation dir) {
		return switch (dir) {
			case NORTH -> hasNorthWall;
			case WEST -> hasWestWall;
			case SOUTH -> hasSouthWall;
			case EAST -> hasEastWall;
		};
	}

	private static List<WallTileType> computeAllWallTypes() {
		ImmutableList.Builder<WallTileType> builder = ImmutableList.builder();
		boolean[] choices = new boolean[]{true, false};

		for (boolean hasNorthWall : choices) {
			for (boolean hasWestWall : choices) {
				for (boolean hasEastWall : choices) {
					for (boolean hasSouthWall : choices) {
						builder.add(new WallTileType(hasNorthWall, hasWestWall, hasSouthWall, hasEastWall));
					}
				}
			}
		}
		return builder.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		WallTileType that = (WallTileType) o;
		//@formatter:off
		return hasNorthWall == that.hasNorthWall
			&& hasWestWall == that.hasWestWall
			&& hasSouthWall == that.hasSouthWall
			&& hasEastWall == that.hasEastWall;
		//@formatter:on
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hasNorthWall, hasWestWall, hasSouthWall, hasEastWall);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("hasNorthWall", hasNorthWall)
				.add("hasWestWall", hasWestWall)
				.add("hasSouthWall", hasSouthWall)
				.add("hasEastWall", hasEastWall)
				.toString();
	}
}
