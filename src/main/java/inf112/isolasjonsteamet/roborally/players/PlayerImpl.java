package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.tiles.Tiles;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Player class that holds player methods.
 */
public class PlayerImpl implements Player {

	private final String playerName;
	private Orientation direction;
	private int life;
	private int damageToken;
	private Coordinate pos;

	/**
	 * Constructor of a new player.
	 */
	public PlayerImpl(String playerName, Coordinate pos, Orientation orientation) {
		this.playerName = playerName;
		this.life = 5;
		this.direction = orientation;
		this.pos = pos;
		this.damageToken = 0;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public Coordinate getPos() {
		return pos;
	}

	/**
	 * Move the player on the board.
	 */
	@Override
	public void move(Coordinate offset) {
		pos = pos.add(offset);
	}


	public boolean checkWinCondition(Board board) {
		return board.getTilesAt(pos).contains(Tiles.FLAG);
	}

	public boolean checkLossCondition(Board board) {
		return board.getTilesAt(pos).contains(Tiles.HOLE);
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
	 * Damage a players robot with 1 damage token
	 */
	@Override
	public void damageRobot() {
		if (++this.damageToken >= 10) {
			killRobot();
		}
	}

	/**
	 * Repair a players robot with 1 damage token
	 */
	@Override
	public void repairRobot() {
		if (damageToken == 0) {
			throw new IllegalStateException("Can not get negative damage tokens");
		} else {
			this.damageToken -= 1;
		}
	}


	/**
	 * If a player gets 10 damageTokens on his robot, player loose 1 life
	 */
	@Override
	public void killRobot() {
		this.life -= 1;
		this.damageToken = 0;
	}

	/**
	 * Gets damageToken for an player robot
	 *
	 * @return damageToken
	 */
	public int getDamageTokens() {
		return this.damageToken;
	}

	/**
	 * Returns how many life an player has
	 *
	 * @return life
	 */
	public int getLife() {
		return life;
	}
}
