package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.cards.DequeCardDeckImpl;
import inf112.isolasjonsteamet.roborally.gui.CardArea;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.gui.MapRendererWidget;
import inf112.isolasjonsteamet.roborally.gui.PrintStreamLabel;
import inf112.isolasjonsteamet.roborally.players.ClientPlayer;
import inf112.isolasjonsteamet.roborally.players.ClientPlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Game class that starts a new game.
 */
public class RoboRallyGame extends GameLoop
		implements ApplicationListener, DelegatingInputProcessor {

	private BoardClientImpl board;
	private final List<ClientPlayer> players = new ArrayList<>();
	private ClientPlayer activePlayer;
	private CardDeck deck;

	private Action showingAction;
	private Robot showingRobot;
	private int framesSinceStartedShowingAction = 0;

	private Stage stage;
	private Skin skin;
	private CardArea cardArea;

	private final Map<CardType, TextureRegionDrawable> drawableCards = new HashMap<>();
	private List<DragAndDrop> dragAndDrops = new ArrayList<>();

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

		var allCards = ImmutableList.of(
				Cards.BACK_UP,
				Cards.ROTATE_RIGHT,
				Cards.ROTATE_LEFT,
				Cards.MOVE_ONE,
				Cards.MOVE_ONE,
				Cards.MOVE_TWO,
				Cards.MOVE_THREE,
				Cards.U_TURN
		);

		var allCardsRepeated = ImmutableList.<CardType>builder();
		for (int i = 0; i < 20; i++) {
			allCardsRepeated.addAll(allCards);
		}

		for (CardType card : new HashSet<>(allCards)) {
			drawableCards.put(card, new TextureRegionDrawable(card.getTexture()));
		}
		//Create new random carddeck
		deck = new DequeCardDeckImpl(allCardsRepeated.build());

		board = new BoardClientImpl(players.stream().map(Player::getRobot).collect(Collectors.toList()), "example.tmx");

		var viewport = new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		var table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);
		table.top();

		var leftGroup = new Table(skin);

		table.add(leftGroup).top().left().expandY().padRight(10F);
		table.add(new MapRendererWidget(board, 100));
		table.row();

		var bottomConsole = new PrintStreamLabel(6, System.out, skin, "default-font", Color.WHITE);
		out = bottomConsole.getStream();
		bottomConsole.setColor(Color.ROYAL);
		leftGroup.add(bottomConsole).top().left().padBottom(10F);
		leftGroup.row();

		cardArea = new CardArea(skin, this::movePlayerCard);
		leftGroup.add(cardArea.getTable());

		//table.debug();
		//leftGroup.debug();
		//chosenCardsGroup.debug();
		//givenCardsGroup.debug();

		/*
		//Adds buttons with the graphic of the card
		int x = -50;
		for (CardType card : givenCards) {
			Button.ButtonStyle tbs = new Button.ButtonStyle();
			tbs.up = drawableCards.get(card);

			Button b = new Button(tbs);
			b.setSize(64, 89);
			b.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (!orderCards.contains(card) && orderCards.size() < 5) {
						orderCards.add(card);
						System.out.println(card.getName() + " added to order.");
					} else if (orderCards.contains(card))  {
						orderCards.remove(card);
						System.out.println(card.getName() + " removed from order.");
					}
				}
			});
			b.setPosition(x += 70, 10);

			stage.addActor(b);
		}
		*/
		//Create button for performing moves from cards
		TextButton textB = new TextButton("Start round", skin);

		textB.setSize(100, 30);
		textB.setColor(Color.ROYAL);
		textB.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				//Moves the robot for each card in list
				startRound();
				prepareRound();
			}
		});
		textB.setPosition(Gdx.graphics.getWidth() - 118, 10);
		stage.addActor(textB);

		prepareRound();
		switchToPlayer(1);
	}

	private void movePlayerCard(CardRow fromRow, int fromCol, CardRow toRow, int toCol) {
		activePlayer.swapCards(fromRow, fromCol, toRow, toCol);
		refreshShownCards();
	}

	private void refreshShownCards() {
		if (cardArea == null) {
			return;
		}

		//We're lazy for now
		cardArea.setChosenCards(activePlayer.getCards(CardRow.CHOSEN));
		cardArea.setGivenCards(activePlayer.getCards(CardRow.GIVEN));
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

		board.updateRobotView();

		if (showingAction != null) {
			if (showingAction.show(showingRobot, board, framesSinceStartedShowingAction++)) {
				showingAction = null;
				framesSinceStartedShowingAction = 0;
			}
		}

		stage.act();
		stage.draw();
	}

	@Override
	protected List<Player> players() {
		return ImmutableList.copyOf(players);
	}

	@Override
	protected CardDeck deck() {
		return deck;
	}

	@Override
	protected Board board() {
		return board;
	}

	@Override
	public void performActionNow(Robot robot, Action action) {
		super.performActionNow(robot, action);
		showingAction = action;
		showingRobot = robot;
	}

	private void performActionActiveRobot(Action action) {
		performActionNow(activePlayer.getRobot(), action);
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
				performActionActiveRobot(new RotateRight());
				out.println("R-Pressed: " + activePlayer.getName()
							+ " is now facing " + activePlayer.getRobot().getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				performActionActiveRobot(new MoveForward(1));
				out.println("E-Pressed: " + activePlayer.getName()
							+ " moved forward to: " + activePlayer.getRobot().getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				performActionActiveRobot(new MoveForward(-1));
				out.println("Q-Pressed: " + activePlayer.getName()
							+ " moved backwards to: " + activePlayer.getRobot().getPos());
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

			case Keys.W -> {
				activePlayer.getRobot().setDir(Orientation.NORTH);
				performActionActiveRobot(new MoveForward(1));
				out.println("W-Pressed: " + activePlayer.getName()
							+ " moved up. Current pos: " + activePlayer.getRobot().getPos());
				yield true;
			}

			case Keys.A -> {
				activePlayer.getRobot().setDir(Orientation.WEST);
				performActionActiveRobot(new MoveForward(1));
				out.println("A-Pressed: " + activePlayer.getName()
							+ " moved left. Current pos: " + activePlayer.getRobot().getPos());
				yield true;
			}

			case Keys.S -> {
				activePlayer.getRobot().setDir(Orientation.SOUTH);
				performActionActiveRobot(new MoveForward(1));
				out.println("s-Pressed: " + activePlayer.getName()
							+ " moved down. Current pos: " + activePlayer.getRobot().getPos());
				yield true;
			}

			case Keys.D -> {
				activePlayer.getRobot().setDir(Orientation.EAST);
				performActionActiveRobot(new MoveForward(1));
				out.println("D-Pressed: " + activePlayer.getName()
							+ " moved right. Current pos: " + activePlayer.getRobot().getPos());
				yield true;
			}

			default -> false;
		};
		out.flush();

		return handled || stage.keyDown(keycode);
	}

	@Override
	public void prepareRound() {
		super.prepareRound();
		refreshShownCards();
	}

	private void switchToPlayer(int playerNum) {
		System.out.println("Switching to player " + playerNum);

		if (playerNum > players.size()) {
			throw new IllegalStateException("Not enough player slots to add player " + playerNum);
		}

		ClientPlayer player = players.get(playerNum - 1);
		if (player == null) {
			player = new ClientPlayerImpl("Player" + playerNum, this, new Coordinate(0, 0), Orientation.NORTH);
			players.set(playerNum - 1, player);
		}

		activePlayer = player;
		refreshShownCards();
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