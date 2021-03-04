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
	private Coordinate pos;

	/**
	 * Constructor of a new player.
	 */
	public PlayerImpl(String playerName, Coordinate pos, Orientation orientation) {
		this.playerName = playerName;
		this.life = 5;
		this.direction = orientation;
		this.pos = pos;
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
}
