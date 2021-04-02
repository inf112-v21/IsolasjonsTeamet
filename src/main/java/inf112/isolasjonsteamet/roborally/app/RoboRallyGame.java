package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
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
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.players.RobotImpl;
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
	private final List<RobotImpl> robots = new ArrayList<>();
	private RobotImpl activeRobot;
	private int activeRobotNum;
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
		for (int i = 0; i < 9; i++) {
			robots.add(null);
			switchToRobot(i + 1);
		}

		//Create new robot
		switchToRobot(1);

		board = new BoardClientImpl(ImmutableList.copyOf(robots), "example.tmx");

		var viewport = new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		var table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);

		table.top();
		table.add(new MapRendererWidget(board, 100));
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
		for (CardType card : givenCards) {
			Button.ButtonStyle tbs = new Button.ButtonStyle();
			tbs.up = new TextureRegionDrawable(new TextureRegion(card.getTexture()));

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
							performActionActiveRobot(act);
						}
					}
				}
			}
		});
		textB.setPosition(Gdx.graphics.getWidth() - 118, 10);
		stage.addActor(textB);

		//Create a new robotCell
		board.updateRobotView();
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

		for (var robot : robots) {
			if (robot == null) {
				continue;
			}

			//Check if a win condition is met
			Coordinate robotPos = robot.getPos();
			if (robot.checkWinCondition(board)) {
				board.robotLayer.setCell(robotPos.getX(), robotPos.getY(), board.robotWonCell);
			}

			if (robot.checkLossCondition(board)) {
				board.robotLayer.setCell(robotPos.getX(), robotPos.getY(), board.robotDiedCell);
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
	public void performActionNow(Robot robot, Action action) {
		Coordinate oldPos = activeRobot.getPos();
		final Orientation oldDir = activeRobot.getDir();

		action.perform(this, board, activeRobot);
		board.checkValid();

		Coordinate newPos = activeRobot.getPos();
		final Orientation newDir = activeRobot.getDir();

		if (!oldPos.equals(newPos)) {
			if (board.getRobotAt(oldPos) == null) {
				//Only one player was standing on the old position, so we clear the cell
				board.robotLayer.setCell(oldPos.getX(), oldPos.getY(), board.transparentCell);
			}

			board.robotLayer.setCell(newPos.getX(), newPos.getY(), board.robotCell);
		}

		if (!oldDir.equals(newDir)) {
			int rotation = orientationToCellRotation(newDir);
			board.robotCell.setRotation(rotation);
			board.robotDiedCell.setRotation(rotation);
			board.robotWonCell.setRotation(rotation);
		}
	}

	private void performActionActiveRobot(Action action) {
		performActionNow(activeRobot, action);
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
				out.println("R-Pressed: " + activeRobotName() + " is now facing " + activeRobot.getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				performActionActiveRobot(new MoveForward(1));
				out.println("E-Pressed: " + activeRobotName() + " moved forward to: " + activeRobot.getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				performActionActiveRobot(new MoveForward(-1));
				out.println("Q-Pressed: " + activeRobotName() + " moved backwards to: " + activeRobot.getPos());
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
				switchToRobot(1);
				yield true;
			}
			case Keys.F2 -> {
				switchToRobot(2);
				yield true;
			}
			case Keys.F3 -> {
				switchToRobot(3);
				yield true;
			}
			case Keys.F4 -> {
				switchToRobot(4);
				yield true;
			}
			case Keys.F5 -> {
				switchToRobot(5);
				yield true;
			}
			case Keys.F6 -> {
				switchToRobot(6);
				yield true;
			}
			case Keys.F7 -> {
				switchToRobot(7);
				yield true;
			}
			case Keys.F8 -> {
				switchToRobot(8);
				yield true;
			}
			case Keys.F9 -> {
				switchToRobot(9);
				yield true;
			}
			//Perform 1-5 actions from orderCards
			case Keys.C -> {
				if (orderCards != null) {
					for (CardType card : orderCards) {
						for (Action act : card.getActions()) {
							performActionActiveRobot(act);
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
				activeRobot.setDir(Orientation.NORTH);
				performActionActiveRobot(new MoveForward(1));
				out.println("W-Pressed: " + activeRobotName()
							+ " moved up. Current pos: " + activeRobot.getPos());
				yield true;
			}

			case Keys.A -> {
				activeRobot.setDir(Orientation.WEST);
				performActionActiveRobot(new MoveForward(1));
				out.println("A-Pressed: " + activeRobotName()
							+ " moved left. Current pos: " + activeRobot.getPos());
				yield true;
			}

			case Keys.S -> {
				activeRobot.setDir(Orientation.SOUTH);
				performActionActiveRobot(new MoveForward(1));
				out.println("s-Pressed: " + activeRobotName()
							+ " moved down. Current pos: " + activeRobot.getPos());
				yield true;
			}

			case Keys.D -> {
				activeRobot.setDir(Orientation.EAST);
				performActionActiveRobot(new MoveForward(1));
				out.println("D-Pressed: " + activeRobotName()
							+ " moved right. Current pos: " + activeRobot.getPos());
				yield true;
			}

			default -> false;
		};
		out.flush();

		return handled || stage.keyDown(keycode);
	}

	private void switchToRobot(int robotNum) {
		System.out.println("Switching to player " + robotNum);

		if (robotNum > robots.size()) {
			throw new IllegalStateException("Not enough robot slots to add robot " + robotNum);
		}

		RobotImpl robot = robots.get(robotNum - 1);
		if (robot == null) {
			robot = new RobotImpl(this, new Coordinate(0, 0), Orientation.EAST);
			robots.set(robotNum - 1, robot);
		}

		activeRobot = robot;
		activeRobotNum = robotNum;
	}

	private String activeRobotName() {
		return "Robot" + activeRobotNum;
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