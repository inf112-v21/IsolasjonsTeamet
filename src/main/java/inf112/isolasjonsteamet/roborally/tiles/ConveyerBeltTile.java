package inf112.isolasjonsteamet.roborally.tiles;

import inf112.isolasjonsteamet.roborally.util.Orientation;

public class ConveyerBeltTile extends Tile {

	private final Orientation direction;
	private final boolean rotateLeft;
	private final boolean rotateRight;
	private final boolean express;

	public ConveyerBeltTile(Orientation direction, boolean rotateLeft, boolean rotateRight, boolean express) {
		this.direction = direction;
		this.rotateLeft = rotateLeft;
		this.rotateRight = rotateRight;
		this.express = express;
	}

	public Orientation getDirection() {
		return direction;
	}

	public boolean isRotateLeft() {
		return rotateLeft;
	}

	public boolean isRotateRight() {
		return rotateRight;
	}

	public boolean isExpress() {
		return express;
	}
}
