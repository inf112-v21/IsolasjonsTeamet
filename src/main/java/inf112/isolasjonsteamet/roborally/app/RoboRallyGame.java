package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.cards.DequeCardDeckImpl;
import inf112.isolasjonsteamet.roborally.gui.PrintStreamLabel;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game class that starts a new game.
 */
public class RoboRallyGame extends InputAdapter implements ApplicationListener {

	private OrthogonalTiledMapRenderer mapRenderer;
	private final OrthographicCamera camera = new OrthographicCamera();

	private BoardClientImpl board;
	private PlayerImpl player;
	private CardDeck deck;
	private List<CardType> givenCards;
	private List<CardType> orderCards;

	private Stage stage;
	private Skin skin;

	private PrintStream out;

	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		//Create new player
		player = new PlayerImpl("player1", new Coordinate(0, 0), Orientation.EAST);

		//board = new BoardImpl("example2.tmx")
		//board = new BoardImpl("example3.tmx")
		board = new BoardClientImpl(ImmutableList.of(player), "example.tmx");

		//Code for our camera on the board, positions and view-angle
		mapRenderer = new OrthogonalTiledMapRenderer(board.map, (float) 1 / 300);
		camera.setToOrtho(false, (float) 5, 5);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		camera.update();

		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		var table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);

		table.top();
		table.add(" ").width(500).height(700);
		table.row();

		var bottomConsole = new PrintStreamLabel(10, System.out, skin, "default-font", Color.WHITE);
		out = bottomConsole.getStream();

		table.add(bottomConsole).top().left();

		//Create a new playerCell
		board.updatePlayerView();

		out.println("------------| How to play |------------");
		out.println("G: Get 5 cards.");
		out.println("1-5: Change order of cards.");
		out.println("X: Remove card from order.");
		out.println("C: Perform actions from cards.");
		out.println(player.getName() + " pos: " + player.getPos() + ", dir: " + player.getDir());
		out.flush();
		//Set our current view to camera
		mapRenderer.setView(camera);
	}

	/**
	 * Method for disposal of items.
	 */
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	/**
	 * Render method that places new and changes current items on the board, dynamically.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
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
		
		stage.act();
		stage.draw();
	}

	private void performAction(Action action) {
		action.perform(board, player);
		board.checkValid();
	}


	/**
	 * keyUp method that listens for keys released on the keyboard, and performs wanted action based on conditions.
	 */
	@SuppressWarnings("checkstyle:Indentation")
	@Override
	public boolean keyDown(int keycode) {
		Coordinate oldPos = player.getPos();

		boolean handled = switch (keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degrees to the right.
			case Keys.R -> {
				performAction(new RotateRight());
				out.println("R-Pressed: " + player.getName() + " is now facing " + player.getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				performAction(new MoveForward(1));
				out.println("E-Pressed: " + player.getName() + " moved forward to: " + player.getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				performAction(new MoveForward(-1));
				out.println("Q-Pressed: " + player.getName() + " moved backwards to: " + player.getPos());
				yield true;
			}

			//Lets player grab cards
			case Keys.G -> {
				//Create new random carddeck
				deck = new DequeCardDeckImpl(
						ImmutableList.of(Cards.BACK_UP, Cards.ROTATE_RIGHT, Cards.ROTATE_LEFT, Cards.MOVE_ONE,
								Cards.MOVE_ONE, Cards.MOVE_TWO, Cards.MOVE_THREE, Cards.U_TURN),
						new Random() //Chosen randomly, by a set of dice
				);
				givenCards = deck.grabCards(5);
				orderCards = new ArrayList<>();
				out.println("Given cards: " + givenCards);
				out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_1 -> {
				orderCards.add(givenCards.get(0));
				out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_2 -> {
				orderCards.add(givenCards.get(1));
				out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_3 -> {
				orderCards.add(givenCards.get(2));
				out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_4 -> {
				orderCards.add(givenCards.get(3));
				out.println("Current order: " + orderCards);
				yield true;
			}
			case Keys.NUM_5 -> {
				orderCards.add(givenCards.get(4));
				out.println("Current order: " + orderCards);
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
					out.println("Current order: " + orderCards);
				}
				yield true;
			}
			case Keys.W -> {
				if (oldPos.getY() < board.boardLayer.getHeight() - 1) {
					player.move(Coordinate.NORTH);
					out.println("W-Pressed: " + player.getName()
							+ " moved up. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Keys.A -> {
				if (oldPos.getX() >= 1) {
					player.move(Coordinate.WEST);
					out.println("A-Pressed: " + player.getName()
							+ " moved left. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Keys.S -> {
				if (oldPos.getY() >= 1) {
					player.move(Coordinate.SOUTH);
					out.println("s-Pressed: " + player.getName()
							+ " moved down. Current pos: " + player.getPos());
				}
				yield true;
			}

			case Keys.D -> {
				if (oldPos.getX() < board.boardLayer.getWidth() - 1) {
					player.move(Coordinate.EAST);
					out.println("D-Pressed: " + player.getName()
							+ " moved right. Current pos: " + player.getPos());
				}
				yield true;
			}

			default -> false;
		};
		out.flush();

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
		stage.getViewport().update(width, height, true);
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