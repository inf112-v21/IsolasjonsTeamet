package inf112.isolasjonsteamet.roborally.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Class to store x and y values.
 */
public class Coordinate {

	@SuppressWarnings("checkstyle:MemberName")
	private final int x;
	@SuppressWarnings("checkstyle:MemberName")
	private final int y;

	public static final Coordinate NORTH = new Coordinate(0, 1);
	public static final Coordinate SOUTH = new Coordinate(0, -1);
	public static final Coordinate WEST = new Coordinate(-1, 0);
	public static final Coordinate EAST = new Coordinate(1, 0);

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public Coordinate add(Coordinate that) {
		return new Coordinate(this.x + that.x, this.y + that.y);
	}

	public Coordinate sub(Coordinate that) {
		return new Coordinate(this.x - that.x, this.y - that.y);
	}

	public Coordinate mult(int num) {
		return new Coordinate(x * num, y * num);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Coordinate that = (Coordinate) o;
		return x == that.x && y == that.y;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(x, y);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("x", x)
				.add("y", y)
				.toString();
	}
}
