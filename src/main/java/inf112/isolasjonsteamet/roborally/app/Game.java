package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
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


	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);

		//Create new player
		player = new PlayerImpl("player1", new Coordinate(0, 0), Orientation.EAST);

		//board = new BoardImpl("example2.tmx")
		//board = new BoardImpl("example3.tmx")
		board = new BoardClientImpl(ImmutableList.of(player), "example.tmx");

		//Key input handling
		Gdx.input.setInputProcessor(this);

		//Code for our camera on the board, positions and viewangle
		camera = new OrthographicCamera();
		mapRenderer = new OrthogonalTiledMapRenderer(board.map, (float) 1 / 300);
		camera.setToOrtho(false, (float) 5, 5);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		//Create a new playerCell
		board.updatePlayerView();

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
		Coordinate playerPos = player.getPos();
		if (player.checkWinCondition(board)) {
			board.playerLayer.setCell(playerPos.getX(), playerPos.getY(), board.playerWonCell);
		}

		if (player.checkLossCondition(board)) {
			board.playerLayer.setCell(playerPos.getX(), playerPos.getY(), board.playerDiedCell);
		}

		//Render changes
		mapRenderer.render();
	}

	private void performAction(Action action) {
		action.perform(board, player);
		board.checkValid();
	}


	/**
	 * keyUp method that listens for keys released on the keyboard, and perfoms wanted action based on conditions.
	 */
	@SuppressWarnings("checkstyle:Indentation")
	@Override
	public boolean keyDown(int keycode) {
		Coordinate oldPos = player.getPos();

		boolean handled = switch (keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degress to the right.
			case Input.Keys.R -> {
				player.setDir(player.getDir().rotateRight());
				System.out.println("R-Pressed: " + player.getName() + " is now facing " + player.getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Input.Keys.E -> {

				//player.move(board, playerVec, 0, 1);
				performAction(new MoveForward(1));
				System.out.println("E-Pressed: " + player.getName() + " moved forward to: " + player.getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Input.Keys.Q -> {

				performAction(new MoveForward(-1));
				System.out.println("Q-Pressed: " + player.getName() + " moved backwards to: " + player.getPos());
				yield true;
			}

			case Input.Keys.W -> {
				if (oldPos.getY() < board.boardLayer.getHeight() - 1) {
					player.move(Coordinate.NORTH);
					System.out.println("W-Pressed: " + player.getName()
							+ " moved up. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Input.Keys.A -> {
				if (oldPos.getX() >= 1) {
					player.move(Coordinate.WEST);
					System.out.println("A-Pressed: " + player.getName()
							+ " moved left. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Input.Keys.S -> {
				if (oldPos.getY() >= 1) {
					player.move(Coordinate.SOUTH);
					System.out.println("s-Pressed: " + player.getName()
							+ " moved down. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Input.Keys.D -> {
				if (oldPos.getX() < board.boardLayer.getWidth() - 1) {
					player.move(Coordinate.EAST);
					System.out.println("D-Pressed: " + player.getName()
							+ " moved right. Current pos: " + player.getPos());
				}
				yield true;
			}

			default -> false;
		};

		if (handled) {
			Coordinate newPos = player.getPos();

			if (!oldPos.equals(newPos)) {
				board.playerLayer.setCell(oldPos.getX(), oldPos.getY(), board.transparentCell);
				board.playerLayer.setCell(newPos.getX(), newPos.getY(), board.playerCell);
			}
		}

		return handled;
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