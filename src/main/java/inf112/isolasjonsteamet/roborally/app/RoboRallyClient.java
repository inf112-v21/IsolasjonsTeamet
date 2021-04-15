package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.gui.CardArea;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.gui.MapRendererWidget;
import inf112.isolasjonsteamet.roborally.gui.PrintStreamLabel;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.gui.ToggleButton;
import inf112.isolasjonsteamet.roborally.gui.screens.NotificationScreen;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.players.ClientPlayer;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Game class that starts a new game.
 */
public class RoboRallyClient implements ApplicationListener, DelegatingInputProcessor, ActionProcessor {

	private final Client client;
	private final ClientBoard board;
	private final ClientPlayer clientPlayer;
	private final List<Player> players = new ArrayList<>();
	private final ScreenController screenController;

	private Action showingAction;
	private Robot showingRobot;
	private int framesSinceStartedShowingAction = 0;

	private Stage stage;
	private Skin skin;
	private CardArea cardArea;

	private PrintStream out;

	private ToggleButton roundReadyButton;

	public RoboRallyClient(
			Client client,
			ClientBoard board,
			String clientName,
			ScreenController screenController
	) {
		this.client = client;
		this.board = board;
		this.clientPlayer = new PlayerImpl(clientName, this, new Coordinate(0, 0), Orientation.NORTH);
		this.screenController = screenController;
	}

	/**
	 * Create method used to create new items and elements used in the game.
	 */
	@Override
	public void create() {
		var viewport = new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		var packetAdapter = new PacketAdapter();
		client.addListener(packetAdapter);

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

		var playerLabel = new Label(clientPlayer.getName(), skin);
		playerLabel.setFontScale(2F);
		leftGroup.add(playerLabel);
		leftGroup.row();

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

		//Create button for performing moves from cards
		roundReadyButton = new ToggleButton("Not ready", "Ready", false, skin, this::sendRoundReady);
		var textB = roundReadyButton.getButton();

		textB.setSize(100, 30);
		textB.setColor(Color.ROYAL);
		textB.setPosition(Gdx.graphics.getWidth() - 118, 10);
		stage.addActor(textB);
	}

	private void movePlayerCard(CardRow fromRow, int fromCol, CardRow toRow, int toCol) {
		clientPlayer.swapCards(fromRow, fromCol, toRow, toCol);
		refreshShownCards();
	}

	private void refreshShownCards() {
		//We're lazy for now
		cardArea.setChosenCards(clientPlayer.getCards(CardRow.CHOSEN));
		cardArea.setGivenCards(clientPlayer.getCards(CardRow.GIVEN));
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

		stage.act();

		if (showingAction != null) {
			if (showingAction.show(showingRobot, board, framesSinceStartedShowingAction++)) {
				showingAction = null;
				framesSinceStartedShowingAction = 0;
			}
		}

		stage.draw();
	}

	@Override
	public void performActionNow(Robot robot, Action action) {
		action.perform(this, board, robot);
		showingAction = action;
		showingRobot = robot;
	}

	private void performClientAction(Action action) {
		performActionNow(clientPlayer.getRobot(), action);
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
		if (clientPlayer == null) {
			return false;
		}

		boolean handled = switch (keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degrees to the right.
			case Keys.R -> {
				performClientAction(new RotateRight());
				sendPlayerStateToServer();
				out.println("R-Pressed: " + clientPlayer.getName()
							+ " is now facing " + clientPlayer.getRobot().getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				performClientAction(new MoveForward(1));
				sendPlayerStateToServer();
				out.println("E-Pressed: " + clientPlayer.getName()
							+ " moved forward to: " + clientPlayer.getRobot().getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				performClientAction(new MoveForward(-1));
				sendPlayerStateToServer();
				out.println("Q-Pressed: " + clientPlayer.getName()
							+ " moved backwards to: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.W -> {
				clientPlayer.getRobot().setDir(Orientation.NORTH);
				performClientAction(new MoveForward(1));
				sendPlayerStateToServer();
				out.println("W-Pressed: " + clientPlayer.getName()
							+ " moved up. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.A -> {
				clientPlayer.getRobot().setDir(Orientation.WEST);
				performClientAction(new MoveForward(1));
				sendPlayerStateToServer();
				out.println("A-Pressed: " + clientPlayer.getName()
							+ " moved left. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.S -> {
				clientPlayer.getRobot().setDir(Orientation.SOUTH);
				performClientAction(new MoveForward(1));
				sendPlayerStateToServer();
				out.println("s-Pressed: " + clientPlayer.getName()
							+ " moved down. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.D -> {
				clientPlayer.getRobot().setDir(Orientation.EAST);
				performClientAction(new MoveForward(1));
				sendPlayerStateToServer();
				out.println("D-Pressed: " + clientPlayer.getName()
							+ " moved right. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			default -> false;
		};
		out.flush();

		return handled || stage.keyDown(keycode);
	}

	private void sendPlayerStateToServer() {
		var robot = clientPlayer.getRobot();
		var packet = new UpdatePlayerStatePacket(robot.getPos(), robot.getDir());
		client.sendToServer(packet);
	}

	private void sendRoundReady(boolean ready) {
		var packet = new UpdateRoundReadyPacket(
				ready,
				clientPlayer.getCards(CardRow.CHOSEN),
				clientPlayer.getCards(CardRow.GIVEN)
		);
		client.sendToServer(packet);
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

	private class PacketAdapter extends ClientPacketAdapter {

		@Override
		public void onUpdatePlayerStates(UpdatePlayerStatesPacket packet) {
			Gdx.app.postRunnable(() -> {
				var unprocessed = players.stream().map(Player::getName).collect(Collectors.toSet());

				for (UpdatePlayerStatesPacket.State state : packet.getStates()) {
					if (unprocessed.contains(state.getName())) {
						//noinspection OptionalGetWithoutIsPresent
						var player = players.stream().filter(p -> p.getName().equals(state.getName())).findAny().get();
						Robot robot = player.getRobot();
						robot.setPos(state.getPos());
						robot.setDir(state.getDir());

						unprocessed.remove(state.getName());
					} else if (state.getName().equals(clientPlayer.getName())) {
						// This block of code will only ever run once, after the first time, the client player
						// will be a part of the unprocessed set, and that block will run instead

						Robot robot = clientPlayer.getRobot();
						robot.setPos(state.getPos());
						robot.setDir(state.getDir());

						players.add(clientPlayer); //This is safe as the block is only ever run once
						unprocessed.remove(state.getName());
					} else {
						players.add(
								new PlayerImpl(state.getName(), RoboRallyClient.this, state.getPos(), state.getDir())
						);
					}
				}

				players.removeIf(p -> unprocessed.contains(p.getName()));

				board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));
			});
		}

		@Override
		public void onRunRound(RunRoundPacket packet) {
			Gdx.app.postRunnable(() -> {
				var cards = packet.getPlayedCards();

				for (int i = 0; i < 8; i++) {
					for (Player player : players) {
						List<CardType> playerCards = cards.get(player.getName());
						if (i < playerCards.size()) {
							var chosenCard = playerCards.get(i);
							for (Action action : chosenCard.getActions()) {
								performActionNow(player.getRobot(), action);
							}
						}
					}
				}
			});
		}

		@Override
		public void onDealNewCards(DealNewCardsPacket packet) {
			Gdx.app.postRunnable(() -> {
				clientPlayer.takeNonStuckCardsBack();
				clientPlayer.giveCards(packet.getCards());
				refreshShownCards();

				roundReadyButton.setState(false);
			});
		}

		@Override
		public void onKicked(KickedPacket packet) {
			Gdx.app.postRunnable(() -> {
				screenController.returnToMainMenu();
				screenController.pushInputScreen(
						new NotificationScreen(screenController, "Kicked: " + packet.getReason())
				);
			});
		}

		@Override
		public void onOtherPlayerKicked(OtherPlayerKickedPacket packet) {
			Gdx.app.postRunnable(() -> {
				out.printf("%s was kicked for %s%n", packet.getPlayerName(), packet.getReason());
				out.flush();

				removePlayer(packet.getPlayerName());
			});
		}

		@Override
		public void onPlayerLeftGame(PlayerLeftGamePacket packet) {
			Gdx.app.postRunnable(() -> {
				out.printf("%s left the game. Reason: %s%n", packet.getPlayerName(), packet.getReason());
				out.flush();

				removePlayer(packet.getPlayerName());
			});
		}

		private void removePlayer(String playerName) {
			players.removeIf(player -> player.getName().equals(playerName));
			board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));
		}

		@Override
		public void onServerClosing(ServerClosingPacket packet) {
			Gdx.app.postRunnable(() -> {
				screenController.returnToMainMenu();
				screenController.pushInputScreen(new NotificationScreen(screenController, "Server closed"));
			});
		}
	}
}