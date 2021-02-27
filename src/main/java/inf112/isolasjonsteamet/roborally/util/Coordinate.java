package inf112.isolasjonsteamet.roborally.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Class to store x and y values.
 */
public class Coordinate {

	public int dx;
	public int dy;

	public Coordinate(int x, int y) {
		this.dx = x;
		this.dy = y;
	}
	public Coordinate set(int x, int y) {
		this.dx = x;
		this.dy = y;
		return this;
	}

	public int getX() {
		return this.dx;
	}

	public int getY() {
		return this.dy;
	}

	public String getPos() {
		return "("+dx+", "+dy+")";
	}

	public Coordinate copy() {
		return new Coordinate(dx, dy);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Coordinate other = (Coordinate) obj;
		return this.dx == other.dx && this.dy == other.dy;
	}
}
