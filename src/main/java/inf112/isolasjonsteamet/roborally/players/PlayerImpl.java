package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.board.BoardImpl;
import inf112.isolasjonsteamet.roborally.board.ClientImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Player class that holds player methods.
 */
public class PlayerImpl implements Player {

	public TiledMapTileLayer.Cell playerWonCell;
	public TiledMapTileLayer.Cell playerDiedCell;
	public TiledMapTileLayer.Cell playerCell;
	public TiledMapTileLayer.Cell transparentCell;

	public int id;
	public String playerName;
	public Orientation direction;
	public int life;
	private Coordinate pos;
	private Coordinate nextPos;

	/**
	 * Constructor of a new player.
	 */
	public PlayerImpl(String playerName) {

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
	public void move(ClientImpl board, Vector2 playerVec, int dx, int dy, TiledMapTileLayer.Cell playerCell) {
		board.playerLayer.setCell((int) playerVec.x + dx, (int) playerVec.y + dy, playerCell);
		board.playerLayer.setCell((int) playerVec.x, (int) playerVec.y, transparentCell);
		playerVec.set(playerVec.x + dx, playerVec.y + dy);
	}

	public void checkWinCondition(ClientImpl board, Vector2 playerVec) {
		if (board.flagLayer.getCell((int) playerVec.x, (int) playerVec.y) != null) {
			board.playerLayer.setCell((int) playerVec.x, (int) playerVec.y, playerWonCell);
		}
	}

	public void checkLossCondition(ClientImpl board, Vector2 playerVec) {
		//Check if a loss condition is met
		if (board.holeLayer.getCell((int) playerVec.x, (int) playerVec.y) != null) {
			board.playerLayer.setCell((int) playerVec.x, (int) playerVec.y, playerDiedCell);
		}
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
