package inf112.isolasjonsteamet.roborally.tiles;

import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;

/**
 * Holds all the different known tile types.
 */
public class Tiles {

	//public static final TileType FOO = new TileType(new MoveForward(1));

    public static final TileType HOLE =  new TileType(); //TODO: Add actions to this tile
    public static final TileType LASER =  new TileType(); //TODO: Add actions to this tile
    public static final TileType FLAG =  new TileType(); //TODO: Add actions to this tile
    public static final TileType REPAIR_SITES_HAMMER_WRENCH =  new TileType(); //TODO: Add actions to this tile
    public static final TileType REPAIR_SITES_WRENCH =  new TileType(); //TODO: Add actions to this tile
    public static final TileType CONVEYOR_BELT = new TileType(new MoveForward(1));
    public static final TileType ROTATING_CONVEYOR_BELT_LEFT = new TileType(new RotateLeft());
    public static final TileType ROTATING_CONVEYOR_BELT_RIGHT = new TileType(new RotateRight());

}
