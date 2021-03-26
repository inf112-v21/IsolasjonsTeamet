package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.cards.DequeCardDeckImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game class that starts a new game.
 */
public class RoboRallyGame extends InputAdapter implements ApplicationListener {

	private SpriteBatch batch;
	private BitmapFont font;
	private OrthogonalTiledMapRenderer mapRenderer;
	private final OrthographicCamera camera = new OrthographicCamera();

	private BoardClientImpl board;
	private final List<PlayerImpl> players = new ArrayList<>();
	private PlayerImpl activePlayer;
	private CardDeck deck;
	private List<CardType> givenCards;
	private List<CardType> orderCards;
	private int playerNum;


	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.RED);

		for (int i = 0; i < 9; i++) {
			players.add(null);
			switchToPlayer(i + 1);
		}

		//Create new player
		switchToPlayer(1);

		//board = new BoardImpl("example2.tmx")
		//board = new BoardImpl("example3.tmx")
		board = new BoardClientImpl(ImmutableList.copyOf(players), "example.tmx");

		//Code for our camera on the board, positions and view-angle
		mapRenderer = new OrthogonalTiledMapRenderer(board.map, (float) 1 / 300);
		camera.setToOrtho(false, (float) 5, 5);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		//Create a new playerCell
		board.updatePlayerView();

		System.out.println("------------| How to play |------------");
		System.out.println("G: Get 5 cards.");
		System.out.println("1-5: Change order of cards.");
		System.out.println("X: Remove card from order.");
		System.out.println("C: Perform actions from cards.");
		System.out.println(activePlayer.getName() + " pos: " + activePlayer.getPos() + ", dir: " + activePlayer.getDir());

		//Set our current view to camera
		mapRenderer.setView(camera);
	}

	public void showPlayer(int playerNum){
		PlayerImpl player = players.get(playerNum - 1);
		this.playerNum = playerNum;
		for (int i = 0; i < 9; i++) {
			Coordinate playerPos = player.getPos();
			board.playerLayer.setCell(playerPos.getX(), playerPos.getY(), board.playerCell);
			board.updatePlayerView();
		}
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

		for (var player : players) {
			if (player == null) {
				continue;
			}

			//Check if a win condition is met
			Coordinate playerPos = player.getPos();
			if (player.checkWinCondition(board)) {
				board.playerLayer.setCell(playerPos.getX(), playerPos.getY(), board.playerWonCell);
			}

			if (player.checkLossCondition(board)) {
				board.playerLayer.setCell(playerPos.getX(), playerPos.getY(), board.playerDiedCell);
			}
		}

		//Render changes
		mapRenderer.render();
	}

	private void performAction(Action action) {
		action.perform(board, activePlayer);
		board.checkValid();
	}


	/**
	 * keyUp method that listens for keys released on the keyboard, and performs wanted action based on conditions.
	 */
	@SuppressWarnings("checkstyle:Indentation")
	@Override
	public boolean keyDown(int keycode) {
		Coordinate oldPos = activePlayer.getPos();

		boolean handled;
		if (switch(keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degrees to the right.
			case Keys.R -> {
				activePlayer.setDir(activePlayer.getDir().rotateRight());
				System.out.println("R-Pressed: " + activePlayer.getName() + " is now facing " + activePlayer.getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {

				//player.move(board, playerVec, 0, 1);
				performAction(new MoveForward(1));
				System.out.println("E-Pressed: " + activePlayer.getName() + " moved forward to: " + activePlayer.getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {

				performAction(new MoveForward(-1));
				System.out.println("Q-Pressed: " + activePlayer.getName() + " moved backwards to: " + activePlayer.getPos());
				yield true;
			}

			//Lets player grab cards
			case Keys.G -> {
				//Create new random card deck
				deck = new DequeCardDeckImpl(
						ImmutableList.of(Cards.BACK_UP, Cards.ROTATE_RIGHT, Cards.ROTATE_LEFT, Cards.MOVE_ONE,
								Cards.MOVE_ONE, Cards.MOVE_TWO, Cards.MOVE_THREE, Cards.U_TURN),
						new Random() //Chosen randomly, by a set of dice
				);
				givenCards = deck.grabCards(5);
				orderCards = new ArrayList<>();
				System.out.println("Given cards: " + givenCards);
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_1 -> {
				orderCards.add(givenCards.get(0));
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_2 -> {
				orderCards.add(givenCards.get(1));
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_3 -> {
				orderCards.add(givenCards.get(2));
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_4 -> {
				orderCards.add(givenCards.get(3));
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_5 -> {
				orderCards.add(givenCards.get(4));
				System.out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.F1 -> {
				switchToPlayer(1);
				showPlayer(1);
				yield true;
			}
			case Keys.F2 -> {
				switchToPlayer(2);
				showPlayer(2);
				yield true;
			}
			case Keys.F3 -> {
				switchToPlayer(3);
				showPlayer(3);
				yield true;
			}
			case Keys.F4 -> {
				switchToPlayer(4);
				showPlayer(4);
				yield true;
			}
			case Keys.F5 -> {
				switchToPlayer(5);
				showPlayer(5);
				yield true;
			}
			case Keys.F6 -> {
				switchToPlayer(6);
				showPlayer(6);
				yield true;
			}
			case Keys.F7 -> {
				switchToPlayer(7);
				showPlayer(7);
				yield true;
			}
			case Keys.F8 -> {
				switchToPlayer(8);
				showPlayer(8);
				yield true;
			}
			case Keys.F9 -> {
				switchToPlayer(9);
				showPlayer(9);
				yield true;
			}
			//Perform 1-5 actions from orderCards
			case Keys.C -> {
				if (orderCards != null) {
					for (CardType card : orderCards) {
						List<Action> actionList = card.getActions();
						for (Action act : actionList) {
							performAction(act);
						}
					}
				}
				yield true;
			}
			//Remove latest card from order
			case Keys.X -> {
				if (orderCards.size() > 0) {
					int lastIndex = orderCards.size() - 1;
					orderCards.remove(lastIndex);
					System.out.println("Current order: " + orderCards);
				}
				yield true;
			}
			case Keys.W -> {
				if (oldPos.getY() < board.boardLayer.getHeight() - 1) {
					activePlayer.move(Coordinate.NORTH);
					System.out.println("W-Pressed: " + activePlayer.getName()
							+ " moved up. Current pos: " + activePlayer.getPos());
				}
				yield true;
			}

			case Keys.A -> {
				if (oldPos.getX() >= 1) {
					activePlayer.move(Coordinate.WEST);
					System.out.println("A-Pressed: " + activePlayer.getName()
							+ " moved left. Current pos: " + activePlayer.getPos());
				}
				yield true;
			}

			case Keys.S -> {
				if (oldPos.getY() >= 1) {
					activePlayer.move(Coordinate.SOUTH);
					System.out.println("s-Pressed: " + activePlayer.getName()
							+ " moved down. Current pos: " + activePlayer.getPos());
				}
				yield true;
			}

			case Keys.D -> {
				if (oldPos.getX() < board.boardLayer.getWidth() - 1) {
					activePlayer.move(Coordinate.EAST);
					System.out.println("D-Pressed: " + activePlayer.getName()
							+ " moved right. Current pos: " + activePlayer.getPos());
				}
				yield true;
			}

			default -> false;
		}) handled = true;
		else handled = false;

		if (handled) {
			Coordinate newPos = activePlayer.getPos();

			if (!oldPos.equals(newPos)) {
				board.playerLayer.setCell(oldPos.getX(), oldPos.getY(), board.transparentCell);
				board.playerLayer.setCell(newPos.getX(), newPos.getY(), board.playerCell);
			}
		}

		return handled;
	}

	private void switchToPlayer(int playerNum) {
		System.out.println("Switching to player " + playerNum);

		if (playerNum > players.size()) {
			throw new IllegalStateException("Not enough player slots to add player " + playerNum);
		}

		PlayerImpl player = players.get(playerNum - 1);
		if (player == null) {
			player = new PlayerImpl("Player" + playerNum, new Coordinate(0, 0), Orientation.EAST);
			players.set(playerNum - 1, player);
		}

		activePlayer = player;
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