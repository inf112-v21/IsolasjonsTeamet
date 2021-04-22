package inf112.isolasjonsteamet.roborally.tiles;

import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;

/**
 * Holds all the different known tile types.
 */
public class Tiles {

	public static final Tile HOLE = new Tile(); //TODO: Add actions to this tile
	public static final Tile LASER = new Tile();
	public static final Tile LASER_EMITTER = new Tile();
	public static final Tile FLAG = new Tile(); //TODO: Add actions to this tile
	public static final Tile REPAIR_SITES_HAMMER_WRENCH = new Tile(); //TODO: Add actions to this tile
	public static final Tile REPAIR_SITES_WRENCH = new Tile(); //TODO: Add actions to this tile
	public static final Tile CONVEYOR_BELT = new Tile(new MoveForward(1));
	public static final Tile ROTATING_CONVEYOR_BELT_LEFT = new Tile(new RotateLeft());
	public static final Tile ROTATING_CONVEYOR_BELT_RIGHT = new Tile(new RotateRight());
	public static final Tile GROUND = new Tile();
}
