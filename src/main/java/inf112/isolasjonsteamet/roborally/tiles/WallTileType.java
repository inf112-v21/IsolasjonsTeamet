package inf112.isolasjonsteamet.roborally.tiles;


import static javax.management.MBeanServerFactory.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;

public class WallTileType extends TileType {

	public static final List<WallTileType> ALL_WALL_TYPES = computeAllWallTypes();

	private final boolean hasNorthWall;
	private final boolean hasWestWall;
	private final boolean hasSouthWall;
	private final boolean hasEastWall;

	public WallTileType(boolean hasNorthWall, boolean hasWestWall, boolean hasSouthWall, boolean hasEastWall) {
		this.hasNorthWall = hasNorthWall;
		this.hasWestWall = hasWestWall;
		this.hasSouthWall = hasSouthWall;
		this.hasEastWall = hasEastWall;
	}

	public boolean hasWallInDir(Orientation dir) {
		return switch (dir) {
			case NORTH -> hasNorthWall;
			case WEST -> hasWestWall;
			case SOUTH -> hasSouthWall;
			case EAST -> hasEastWall;
		};
	}

	private static List<WallTileType> computeAllWallTypes() {
		Builder<WallTileType> builder = ImmutableList.builder();
		boolean choices[] = new boolean[] {true, false};




		}

		return builder.build();
	}
}
