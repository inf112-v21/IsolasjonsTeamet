package inf112.isolasjonsteamet.roborally.util;

public class Coords {
	private final int x;
	private final int y;

	public Coords(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}

	public Coords copy() {
		return new Coords(x,y);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Coords other = (Coords) obj;
		return this.x == other.x && this.y == other.y;
	}
}
