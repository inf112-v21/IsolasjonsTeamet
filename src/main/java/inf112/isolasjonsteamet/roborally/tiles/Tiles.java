package inf112.isolasjonsteamet.roborally.tiles;

import inf112.isolasjonsteamet.roborally.actions.Damage;
import inf112.isolasjonsteamet.roborally.actions.FireTileLaser;
import inf112.isolasjonsteamet.roborally.actions.PhaseAction;
import inf112.isolasjonsteamet.roborally.board.Phase;

/**
 * Holds all the different known tile types.
 */
public class Tiles {

	public static final Tile HOLE = new Tile(); //TODO: Add actions to this tile
	public static final Tile LASER = new Tile(FireTileLaser::new);
	public static final Tile LASER_EMITTER = new Tile(() -> new PhaseAction(Phase.LASERS, Damage::new));
	public static final Tile FLAG = new Tile(); //TODO: Add actions to this tile
	public static final Tile REPAIR_SITES_HAMMER_WRENCH = new Tile(); //TODO: Add actions to this tile
	public static final Tile REPAIR_SITES_WRENCH = new Tile(); //TODO: Add actions to this tile
	public static final Tile GROUND = new Tile();
}
