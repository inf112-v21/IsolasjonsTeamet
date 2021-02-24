package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Player class that holds player methods.
 */
public class PlayerImpl implements Player {

	public int id;
	public String playerName;
	public Orientation direction;
	public int life;
	private Coordinate pos;
	private Coordinate nextPos;

	/**
	 * Constructor of a new player.
	 */
	public PlayerImpl() {
		this.id = id;
		this.playerName = playerName;
		this.life = 5;
		this.direction = Orientation.EAST;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public Coordinate getPos() {
		return pos;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public void setPos(Coordinate c) {
		this.pos = c;
	}

	/**
	 * Move the player on the board.
	 */
	@Override
	public void move(Vector2 amount) {

	}

	/**
	 * Returns the name of the player.
	 */
	@Override
	public String getName() {
		return playerName;
	}

	/**
	 * Gets direction of the player.
	 *
	 * @return direction
	 */
	@Override
	public Orientation getDir() {
		return direction;
	}

	/**
	 * Sets direction of the player.
	 */
	@Override
	public void setDir(Orientation dir) {
		this.direction = dir;
	}

	/**
	 * Sets moving direction of the player.
	 */
	@Override
	public void setMovDir(Orientation movingDir) {

	}

	/**
	 * Sets next positions of the player.
	 */
	public void setNextPos(Coordinate c) {
		this.nextPos = c;
	}
}
