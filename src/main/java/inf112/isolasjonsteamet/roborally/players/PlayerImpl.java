package inf112.isolasjonsteamet.roborally.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
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

	public StaticTiledMapTile transparentTileTexture;
	public StaticTiledMapTile staticPlayTile;
	public StaticTiledMapTile staticWonTile;
	public StaticTiledMapTile staticDiedTile;

	public int id;
	public String playerName;
	public Orientation direction;
	public int life;
	public Coordinate playerVec;
	private Coordinate nextPos;

	/**
	 * Constructor of a new player.
	 */
	public PlayerImpl(String playerName) {

		Texture playerTx = new Texture("player.png");
		final TextureRegion[][] tReg = new TextureRegion().split(playerTx, 300, 300);

		Texture defTx = new Texture("tiles.png");
		final TextureRegion[][] tReg2 = new TextureRegion().split(defTx, 300, 300);

		//Static tiles for playing, dead and winning player cells
		transparentTileTexture = new StaticTiledMapTile(tReg2[15][4]);
		staticPlayTile = new StaticTiledMapTile(tReg[0][0]);
		staticWonTile = new StaticTiledMapTile(tReg[0][2]);
		staticDiedTile = new StaticTiledMapTile(tReg[0][1]);

		//Creating new instances of our field variables
		transparentCell = new TiledMapTileLayer.Cell().setTile(transparentTileTexture);
		playerWonCell = new TiledMapTileLayer.Cell().setTile(staticWonTile);
		playerDiedCell = new TiledMapTileLayer.Cell().setTile(staticDiedTile);
		playerCell = new TiledMapTileLayer.Cell().setTile(staticPlayTile);

		this.id = id;
		this.playerName = playerName;
		this.life = 5;
		this.direction = Orientation.EAST;
		this.playerVec = playerVec;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public Coordinate getPos() {
		return playerVec;
	}

	/**
	 * Get the position of a player.
	 */
	@Override
	public void setPos(Coordinate c) {

		this.playerVec = c;
	}

	/**
	 * Move the player on the board.
	 */
	@Override
	public void move(BoardClientImpl board, Coordinate playerVec, int dx, int dy) {
		board.playerLayer.setCell((int) playerVec.dx + dx, (int) playerVec.dy + dy, playerCell);
		board.playerLayer.setCell((int) playerVec.dx, (int) playerVec.dy, transparentCell);
		playerVec.set(playerVec.dx + dx, playerVec.dy + dy);
	}


	public void checkWinCondition(BoardClientImpl board, Coordinate playerVec) {
		if (board.flagLayer.getCell((int) playerVec.dx, (int) playerVec.dy) != null) {
			board.playerLayer.setCell((int) playerVec.dx, (int) playerVec.dy, playerWonCell);
		}
	}

	public void checkLossCondition(BoardClientImpl board, Coordinate playerVec) {
		//Check if a loss condition is met
		if (board.holeLayer.getCell((int) playerVec.dx, (int) playerVec.dy) != null) {
			board.playerLayer.setCell((int) playerVec.dx, (int) playerVec.dy, playerDiedCell);
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
