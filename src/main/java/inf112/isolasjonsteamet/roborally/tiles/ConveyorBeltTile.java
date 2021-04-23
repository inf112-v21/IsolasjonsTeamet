package inf112.isolasjonsteamet.roborally.tiles;

import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Represents tiles which are conveyor belts.
 */
public class ConveyorBeltTile extends Tile {

	private final Orientation direction;
	private final boolean rotateBelt;
	private final boolean express;

	/**
	 * Constructs a new conveyor belt tile. Only one of rotateLeft or rotateRight should be enabled.
	 */
	public ConveyorBeltTile(Orientation direction, boolean rotateBelt, boolean express) {
		this.direction = direction;
		this.rotateBelt = rotateBelt;
		this.express = express;
	}

	public Orientation getDirection() {
		return direction;
	}

	public boolean isExpress() {
		return express;
	}

	public boolean isRotateBelt() {
		return rotateBelt;
	}
}
