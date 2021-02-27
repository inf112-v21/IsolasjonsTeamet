package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Game class that starts a new game.
 */
public class Game extends InputAdapter implements ApplicationListener {

	private SpriteBatch batch;
	private BitmapFont font;
	public OrthogonalTiledMapRenderer mapRenderer;
	public OrthographicCamera camera;

	public BoardClientImpl board;
	public PlayerImpl player;

	public TiledMapTileLayer.Cell playerWonCell;
	public TiledMapTileLayer.Cell playerDiedCell;
	public TiledMapTileLayer.Cell playerCell;
	public TiledMapTileLayer.Cell transparentCell;
	public Coordinate playerVec;

	public StaticTiledMapTile transparentTileTexture;
	public StaticTiledMapTile staticPlayTile;
	public StaticTiledMapTile staticWonTile;
	public StaticTiledMapTile staticDiedTile;


	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);

		//board = new BoardImpl("example2.tmx")
		//board = new BoardImpl("example3.tmx")
		board = new BoardClientImpl("example.tmx");

		//Create new player
		player = new PlayerImpl("player1");

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

		playerVec = new Coordinate(0,0);

		//Key input handling
		Gdx.input.setInputProcessor(this);

		//Code for our camera on the board, positions and viewangle
		camera = new OrthographicCamera();
		mapRenderer = new OrthogonalTiledMapRenderer(board.map, (float) 1 / 300);
		camera.setToOrtho(false, (float) 5, 5);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		//Create a new playerCell
		board.playerLayer.setCell(0, 0, playerCell);

		System.out.println(player.getName() + " is facing " + player.getDir());

		//Set our current view to camera
		mapRenderer.setView(camera);
	}

	/**
	 * Method for disposal of items.
	 */
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	/**
	 * Render method that places new and changes current items on the board, dynamically.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);


		//Check if a win condition is met
		player.checkWinCondition(board, playerVec);
		player.checkLossCondition(board, playerVec);

		//Render changes
		mapRenderer.render();
	}


	/**
	 * keyUp method that listens for keys released on the keyboard, and perfoms wanted action based on conditions.
	 */
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.R:
				player.setDir(Orientation.rotateRight(player.getDir()));
				System.out.println("R-Pressed: "+player.getName()+" is now facing " +player.getDir());
				return true;
			case Input.Keys.W:

				//player.move(board, playerVec, 0, 1);
				MoveForward mov = new MoveForward(1);
				mov.perform(board, playerVec, player);
				System.out.println("W-Pressed: "+player.getName()+" moved to: "+playerVec.getPos());
				return true;

			case Input.Keys.A:
				if (playerVec.dx < 1) {
					return true;
				}
				player.move(board, playerVec, -1, 0);
				System.out.println("A-Pressed: "+player.getName()+ " moved left. Current pos: "+playerVec.getPos());
				return true;

			case Input.Keys.S:
				if (playerVec.dy < 1) {
					return true;
				}
				player.move(board, playerVec, 0, -1);
				System.out.println("S-Pressed: "+player.getName()+" moved down. Current pos: "+playerVec.getPos());
				return true;

			case Input.Keys.D:
				if (playerVec.dx >= board.boardLayer.getWidth() - 1) {
					return true;
				}
				player.move(board, playerVec, 1, 0);
				System.out.println("D-Pressed: "+player.getName()+" moved right. Current pos: "+playerVec.getPos());
				return true;

			default:
				return false;
		}
	}

	/**
	 * Method for resizing.
	 */
	@Override
	public void resize(int width, int height) {
	}

	/**
	 * Pause method to pause an active game.
	 */
	@Override
	public void pause() {
	}

	/**
	 * Resume method to resume paused game.
	 */
	@Override
	public void resume() {
	}
}