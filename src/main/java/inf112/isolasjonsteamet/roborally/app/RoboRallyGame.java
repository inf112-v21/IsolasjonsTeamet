package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.cards.DequeCardDeckImpl;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.gui.MapRendererWidget;
import inf112.isolasjonsteamet.roborally.gui.PrintStreamLabel;
import inf112.isolasjonsteamet.roborally.players.Player;
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
public class RoboRallyGame
		implements ApplicationListener, DelegatingInputProcessor, ActionProcessor {

	private BoardClientImpl board;
	private final List<PlayerImpl> players = new ArrayList<>();
	private PlayerImpl activePlayer;
	private CardDeck deck;
	private List<CardType> givenCards;
	private List<CardType> orderCards;
	private Action showingAction;
	private Player showingPlayer;
	private int framesSinceStartedShowingAction = 0;

	private Stage stage;
	private Skin skin;
	private Texture card;

	private PrintStream out;

	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		for (int i = 0; i < 9; i++) {
			players.add(null);
			switchToPlayer(i + 1);
		}

		//Create new player
		switchToPlayer(1);

		board = new BoardClientImpl(ImmutableList.copyOf(players), "RiskyExchangeBoard.tmx");

		var viewport = new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		var table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);

		table.top();
		table.add(new MapRendererWidget(board, 35));
		table.row();

		var bottomConsole = new PrintStreamLabel(3, System.out, skin, "default-font", Color.WHITE);
		bottomConsole.setColor(Color.ROYAL);
		out = bottomConsole.getStream();

		table.add(bottomConsole).top().left();

		//Create new random carddeck
		deck = new DequeCardDeckImpl(
				ImmutableList.of(Cards.BACK_UP, Cards.ROTATE_RIGHT, Cards.ROTATE_LEFT, Cards.MOVE_ONE,
						Cards.MOVE_ONE, Cards.MOVE_TWO, Cards.MOVE_THREE, Cards.U_TURN),
				new Random() //Chosen randomly, by a set of dice
		);
		givenCards = deck.grabCards(8);
		orderCards = new ArrayList<>();

		//Adds buttons with the graphic of the card
		int x = -50;
		for (CardType cards : givenCards) {
			card = new Texture(cards.toString() + ".jpg");
			Button.ButtonStyle tbs = new Button.ButtonStyle();
			tbs.up = new TextureRegionDrawable(new TextureRegion(card));

			Button b = new Button(tbs);
			b.setSize(64, 89);
			b.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (!orderCards.contains(cards) && orderCards.size() < 5) {
						orderCards.add(cards);
						System.out.println(cards.toString() + " added to order.");
					} else if (orderCards.contains(cards))  {
						orderCards.remove(cards);
						System.out.println(cards.toString() + " removed from order.");
					}
				}
			});
			b.setPosition(x += 70, 10);

			stage.addActor(b);
		}
		//Create button for performing moves from cards
		TextButton textB = new TextButton("Start round", skin);

		textB.setSize(100, 30);
		textB.setColor(Color.ROYAL);
		textB.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {

				//Moves the robot for each card in list
				if (orderCards != null) {
					for (CardType card : orderCards) {
						for (Action act : card.getActions()) {
							performActionActivePlayer(act);

						}
					}
				}
			}
		});
		textB.setPosition(Gdx.graphics.getWidth() - 118, 10);
		stage.addActor(textB);

		//Create a new playerCell
		board.updatePlayerView();
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

		if (showingAction != null) {
			if (showingAction.show(showingPlayer, board, framesSinceStartedShowingAction++)) {
				showingAction = null;
				framesSinceStartedShowingAction = 0;
			}
		}

		TextField textF = new TextField("Cards: " + orderCards, skin);
		textF.setPosition(19, 105);
		textF.setSize(Gdx.graphics.getWidth() - 38, 30);
		textF.setColor(Color.ROYAL);
		stage.addActor(textF);
		stage.act(Gdx.graphics.getDeltaTime());

		//stage.act();
		stage.draw();
	}

	/**
	 * {@inheritDoc}
	 */
	public void performActionNow(Player player, Action action) {
		Coordinate oldPos = activePlayer.getPos();
		final Orientation oldDir = activePlayer.getDir();

		action.perform(this, board, activePlayer);
		board.checkValid();

		Coordinate newPos = activePlayer.getPos();
		final Orientation newDir = activePlayer.getDir();

		if (!oldPos.equals(newPos)) {
			if (board.getPlayerAt(oldPos) == null) {
				//Only one player was standing on the old position, so we clear the cell
				board.playerLayer.setCell(oldPos.getX(), oldPos.getY(), board.transparentCell);
			}

			board.playerLayer.setCell(newPos.getX(), newPos.getY(), board.playerCell);
		}

		if (!oldDir.equals(newDir)) {
			int rotation = orientationToCellRotation(newDir);
			board.playerCell.setRotation(rotation);
			board.playerDiedCell.setRotation(rotation);
			board.playerWonCell.setRotation(rotation);
		}

		showingAction = action;
		showingPlayer = player;
	}

	private void performActionActivePlayer(Action action) {
		performActionNow(activePlayer, action);
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return stage;
	}

	/**
	 * keyUp method that listens for keys released on the keyboard, and performs wanted action based on conditions.
	 */
	@SuppressWarnings({"checkstyle:Indentation", "checkstyle:WhitespaceAround"})
	@Override
	public boolean keyDown(int keycode) {
		boolean handled = switch (keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degrees to the right.
			case Keys.R -> {
				performActionActivePlayer(new RotateRight());
				out.println("R-Pressed: " + activePlayer.getName() + " is now facing " + activePlayer.getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				performActionActivePlayer(new MoveForward(1));
				out.println("E-Pressed: " + activePlayer.getName() + " moved forward to: " + activePlayer.getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				performActionActivePlayer(new MoveForward(-1));
				out.println("Q-Pressed: " + activePlayer.getName() + " moved backwards to: " + activePlayer.getPos());
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
			case Keys.F1 -> {
				switchToPlayer(1);
				yield true;
			}
			case Keys.F2 -> {
				switchToPlayer(2);
				yield true;
			}
			case Keys.F3 -> {
				switchToPlayer(3);
				yield true;
			}
			case Keys.F4 -> {
				switchToPlayer(4);
				yield true;
			}
			case Keys.F5 -> {
				switchToPlayer(5);
				yield true;
			}
			case Keys.F6 -> {
				switchToPlayer(6);
				yield true;
			}
			case Keys.F7 -> {
				switchToPlayer(7);
				yield true;
			}
			case Keys.F8 -> {
				switchToPlayer(8);
				yield true;
			}
			case Keys.F9 -> {
				switchToPlayer(9);
				yield true;
			}
			//Perform 1-5 actions from orderCards
			case Keys.C -> {
				if (orderCards != null) {
					for (CardType card : orderCards) {
						for (Action act : card.getActions()) {
							performActionActivePlayer(act);
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
				activePlayer.setDir(Orientation.NORTH);
				performActionActivePlayer(new MoveForward(1));
				out.println("W-Pressed: " + activePlayer.getName()
						+ " moved up. Current pos: " + activePlayer.getPos());
				yield true;
			}

			case Keys.A -> {
				activePlayer.setDir(Orientation.WEST);
				performActionActivePlayer(new MoveForward(1));
				out.println("A-Pressed: " + activePlayer.getName()
						+ " moved left. Current pos: " + activePlayer.getPos());
				yield true;
			}

			case Keys.S -> {
				activePlayer.setDir(Orientation.SOUTH);
				performActionActivePlayer(new MoveForward(1));
				out.println("s-Pressed: " + activePlayer.getName()
						+ " moved down. Current pos: " + activePlayer.getPos());
				yield true;
			}

			case Keys.D -> {
				activePlayer.setDir(Orientation.EAST);
				performActionActivePlayer(new MoveForward(1));
				out.println("D-Pressed: " + activePlayer.getName()
						+ " moved right. Current pos: " + activePlayer.getPos());
				yield true;
			}

			default -> false;
		};
		out.flush();

		return handled || stage.keyDown(keycode);
	}

	private void switchToPlayer(int playerNum) {
		System.out.println("Switching to player " + playerNum);

		if (playerNum > players.size()) {
			throw new IllegalStateException("Not enough player slots to add player " + playerNum);
		}

		PlayerImpl player = players.get(playerNum - 1);
		if (player == null) {
			player = new PlayerImpl(this, "Player" + playerNum, new Coordinate(0, 0), Orientation.NORTH);
			players.set(playerNum - 1, player);
		}

		activePlayer = player;
	}

	private int orientationToCellRotation(Orientation orientation) {
		return switch (orientation) {
			case NORTH -> 0;
			case WEST -> 1;
			case SOUTH -> 2;
			case EAST -> 3;
		};
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