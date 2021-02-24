package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.util.Coords;
import inf112.isolasjonsteamet.roborally.util.Orientation;

public class PlayerImpl implements Player {
	public int playerID;
	public String playerName;
	public Orientation direction;
	public int life;
	private Coords pos;
	private Coords nextPos;

	public PlayerImpl() {
		this.playerID = playerID;
		this.playerName = playerName;
		this.life = 5;
		this.direction = Orientation.EAST;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public Coords getPos() {
		return pos;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public void setPos(Coords c) {
		this.pos = c;
	}

	/**
	 * Move the player on the board.
	 */
	@Override
	public void move(Vector2 amount) {

	}

	/**
	 * Returns the name of the player
	 */
	@Override
	public String getName() {
		return playerName;
	}

	/**
	 * Gets direction of the player.
	 * @return direction
	 */
	@Override
	public Orientation getDir() {
		return direction;
	}

	/**
	 * Sets direction of the player.
	 * @param dir
	 */
	@Override
	public void setDir(Orientation dir) {
		this.direction = dir;
	}

	/**
	 * Sets moving direction of the player.
	 * @param movingDir
	 */
	@Override
	public void setMovDir(Orientation movingDir) {

	}

	/**
	 * Sets next positions of the player.
	 * @param c
	 */
	public void setNextPos(Coords c) {
		this.nextPos  = c;
	}

}
