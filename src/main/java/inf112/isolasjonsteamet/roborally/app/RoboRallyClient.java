package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.DequeActionProcessor;
import inf112.isolasjonsteamet.roborally.actions.MoveForward;
import inf112.isolasjonsteamet.roborally.actions.RotateLeft;
import inf112.isolasjonsteamet.roborally.actions.RotateRight;
import inf112.isolasjonsteamet.roborally.actions.ScheduledAction;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.gui.CardArea;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.gui.MapRendererWidget;
import inf112.isolasjonsteamet.roborally.gui.PrintStreamLabel;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.gui.ToggleButton;
import inf112.isolasjonsteamet.roborally.gui.screens.GameScreen;
import inf112.isolasjonsteamet.roborally.gui.screens.NotificationScreen;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientChatPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerChatPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.players.ClientPlayer;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Game class that starts a new game.
 */
public class RoboRallyClient
		extends RoboRallyShared
		implements ApplicationListener, DelegatingInputProcessor {

	private final Client client;
	private final ClientBoard board;
	private final ClientPlayer clientPlayer;
	private final List<Player> players = new ArrayList<>();
	private final String host;
	private final ScreenController screenController;
	private final GameScreen gameScreen;

	private Action showingAction;
	private Robot showingRobot;
	private int framesSinceStartedShowingAction = 0;
	private final DequeActionProcessor actionProcessor = new DequeActionProcessor() {
		@Override
		protected boolean isExecutingAction() {
			return showingAction != null;
		}

		@Override
		public void performActionNow(Robot robot, Action action, Phase phase) {
			boolean hasWork = false;
			//This do loop will only every loop if isShown == false
			do {
				action.initialize(board, robot);
				if (isShown) {
					action.initializeShow(robot, board);
				}

				showingAction = action;
				showingRobot = robot;
				action.perform(this, board, robot, phase);

				if (!isShown) {
					showingAction = null;
					showingRobot = null;

					hasWork = hasNext();
					if (hasWork) {
						ScheduledAction nextActionEntry = next();
						robot = nextActionEntry.getRobot();
						action = nextActionEntry.getAction();
						phase = nextActionEntry.getPhase();
					}
				}
			} while (hasWork);
		}
	};
	private boolean sendPlayerStateEventually;
	private boolean isShown;

	private Stage stage;
	private Skin skin;
	private CardArea cardArea;
	private Table playerList;

	private PrintStream out;
	private TextField chatField;

	private ToggleButton roundReadyButton;

	/**
	 * Creates a new RoboRally client, which will run and display the game from one player's perspective.
	 *
	 * @param client The client to use for sending packets to the server.
	 * @param board The board to use.
	 * @param clientName The name of the client player.
	 * @param host The name of the host player.
	 */
	public RoboRallyClient(
			Client client,
			ClientBoard board,
			String clientName,
			String host, ScreenController screenController,
			GameScreen gameScreen) {
		this.client = client;
		this.board = board;
		this.clientPlayer = new PlayerImpl(clientName, actionProcessor, new Coordinate(0, 0), Orientation.NORTH);
		this.host = host;
		this.screenController = screenController;
		this.gameScreen = gameScreen;
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
		table.add(new MapRendererWidget(board));
		table.row();

		var playerLabel = new Label(clientPlayer.getName(), skin);
		playerLabel.setFontScale(2F);
		leftGroup.add(playerLabel);
		leftGroup.row();

		playerList = new Table(skin);
		leftGroup.add(playerList);
		leftGroup.row();

		var bottomConsole = new PrintStreamLabel(6, System.out, skin, "default-font", Color.WHITE);
		out = bottomConsole.getStream();
		bottomConsole.setColor(Color.ROYAL);
		leftGroup.add(bottomConsole).top().left().padBottom(10F);
		leftGroup.row();

		cardArea = new CardArea(skin, this::movePlayerCard);
		leftGroup.add(cardArea.getTable());
		leftGroup.row();

		chatField = new TextField("", skin);
		leftGroup.add(chatField).width(500);
		var chatButton = new TextButton("Chat", skin);
		leftGroup.add(chatButton);
		chatButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				chat(chatField.getText());
			}
		});
		leftGroup.row();

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

		var leaveButton = new TextButton("Leave", skin);
		leaveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameScreen.removeClient(RoboRallyClient.this, null);
			}
		});

		leaveButton.setSize(100, 30);
		leaveButton.setColor(Color.FIREBRICK);
		leaveButton.setPosition(Gdx.graphics.getWidth() - 118, Gdx.graphics.getHeight() - 40);
		stage.addActor(leaveButton);
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

	private void refreshPlayerList() {
		playerList.clearChildren();

		players.forEach(player -> {
			String playerName = player.getName();
			playerList.add(playerName);

			/*
			TODO: Implement round ready notice
			if (ready) {
				playerList.add("Ready!");
			}
			else {
				playerList.add();
			}
			 */

			if (clientPlayer.getName().equals(host) && !playerName.equals(host)) {
				var kickButton = new TextButton("Kick", skin);
				var kickReasonField = new TextField("", skin);

				kickButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						client.kickPlayer(playerName, kickReasonField.getText());
					}
				});

				playerList.add(kickButton).padLeft(10);
				playerList.add(kickReasonField).padLeft(5);
			}

			playerList.row();
		});
	}

	/**
	 * Method for disposal of items.
	 */
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();

		client.disconnect("Disposing");
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

				if (actionProcessor.hasNext()) {
					ScheduledAction nextAction = actionProcessor.next();
					actionProcessor.performActionNow(nextAction.getRobot(), nextAction.getAction(), nextAction.getPhase());
				} else if (sendPlayerStateEventually) {
					sendPlayerStateToServer();
					sendPlayerStateEventually = false;
				}
			}
		}

		stage.draw();
	}

	private void scheduleClientAction(Action action) {
		actionProcessor.scheduleActionLast(clientPlayer.getRobot(), action, Phase.MISC);
	}

	private void scheduleClientRotateToDirection(Orientation dir) {
		Orientation robotDir = clientPlayer.getRobot().getDir();
		if (dir.equals(robotDir.getOpposingDir())) {
			scheduleClientAction(new RotateRight());
			scheduleClientAction(new RotateRight());
		} else if (robotDir.rotateLeft().equals(dir)) {
			scheduleClientAction(new RotateLeft());
		} else if (robotDir.rotateRight().equals(dir)) {
			scheduleClientAction(new RotateRight());
		}
	}

	@Override
	protected void foreachPlayerTile(BiConsumer<Player, Tile> handler) {
		for (Player player : players) {
			for (Tile tile : board.getTilesAt(player.getRobot().getPos())) {
				handler.accept(player, tile);
			}
		}
	}

	@Override
	protected Board board() {
		return board;
	}

	@Override
	protected ActionProcessor actionProcessor() {
		return actionProcessor;
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return stage;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean handled = stage.touchDown(screenX, screenY, pointer, button);
		if (!handled) {
			stage.unfocus(chatField);
		}
		return handled;
	}

	/**
	 * keyUp method that listens for keys released on the keyboard, and performs wanted action based on conditions.
	 */
	@SuppressWarnings({"checkstyle:Indentation", "checkstyle:WhitespaceAround"})
	@Override
	public boolean keyDown(int keycode) {
		if (clientPlayer == null || showingAction != null || chatField.hasKeyboardFocus()) {
			return false;
		}

		boolean handled = switch (keycode) {
			// If R on the keyboard is pressed, the robot rotates 90 degrees to the right.
			case Keys.R -> {
				scheduleClientAction(new RotateRight());
				sendPlayerStateEventually = true;
				out.println("R-Pressed: " + clientPlayer.getName()
							+ " is now facing " + clientPlayer.getRobot().getDir());
				yield true;
			}
			// If E on the keyboard is pressed, the robot moves 1 step forward in the direction it is facing
			case Keys.E -> {
				scheduleClientAction(new MoveForward(1));
				sendPlayerStateEventually = true;
				out.println("E-Pressed: " + clientPlayer.getName()
							+ " moved forward to: " + clientPlayer.getRobot().getPos());
				yield true;
			}
			// If Q on the keyboard is pressed, the robot moves 1 step backwards in the direction it is facing
			case Keys.Q -> {
				scheduleClientAction(new MoveForward(-1));
				sendPlayerStateEventually = true;
				out.println("Q-Pressed: " + clientPlayer.getName()
							+ " moved backwards to: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.W -> {
				scheduleClientRotateToDirection(Orientation.NORTH);
				scheduleClientAction(new MoveForward(1));
				sendPlayerStateEventually = true;
				out.println("W-Pressed: " + clientPlayer.getName()
							+ " moved up. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.A -> {
				scheduleClientRotateToDirection(Orientation.WEST);
				scheduleClientAction(new MoveForward(1));
				sendPlayerStateEventually = true;
				out.println("A-Pressed: " + clientPlayer.getName()
							+ " moved left. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.S -> {
				scheduleClientRotateToDirection(Orientation.SOUTH);
				scheduleClientAction(new MoveForward(1));
				sendPlayerStateEventually = true;
				out.println("s-Pressed: " + clientPlayer.getName()
							+ " moved down. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			case Keys.D -> {
				scheduleClientRotateToDirection(Orientation.EAST);
				scheduleClientAction(new MoveForward(1));
				sendPlayerStateEventually = true;
				out.println("D-Pressed: " + clientPlayer.getName()
							+ " moved right. Current pos: " + clientPlayer.getRobot().getPos());
				yield true;
			}

			default -> false;
		};
		out.flush();

		return handled || stage.keyDown(keycode);
	}

	private void chat(String message) {
		client.sendToServer(new ClientChatPacket(message));
		stage.unfocus(chatField);
		chatField.setText("");
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
		isShown = false;
	}

	/**
	 * Resume method to resume paused game.
	 */
	@Override
	public void resume() {
		isShown = true;
	}

	private class PacketAdapter extends ClientPacketAdapter {

		@Override
		public void handle(Server2ClientPacket packet) {
			//Reduce the need to add this to each handler
			Gdx.app.postRunnable(() -> super.handle(packet));
		}

		@Override
		public void onUpdatePlayerStates(UpdatePlayerStatesPacket packet) {
			var unprocessed = players.stream().map(Player::getName).collect(Collectors.toSet());
			boolean playersChanged = false;

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
					playersChanged = true;
				} else {
					players.add(
							new PlayerImpl(state.getName(), actionProcessor, state.getPos(), state.getDir())
					);
					playersChanged = true;
				}
			}

			players.removeIf(p -> unprocessed.contains(p.getName()));

			board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));

			if (playersChanged) {
				refreshPlayerList();
			}
		}

		private void processPlayerCard(Map<String, List<Card>> cards, Player player, int cardNum) {
			List<Card> playerCards = cards.get(player.getName());
			Card chosenCard = Cards.NO_CARD;

			if (cardNum < playerCards.size()) {
				chosenCard = playerCards.get(cardNum);
			}

			for (Action action : chosenCard.createActions()) {
				actionProcessor.scheduleActionLast(player.getRobot(), action, Phase.CARDS);
			}
		}

		@Override
		public void onRunRound(RunRoundPacket packet) {
			var cards = packet.getPlayedCards();

			for (int i = 0; i < CardRow.CHOSEN.getSize(); i++) {
				for (Player player : players) {
					processPlayerCard(cards, player, i);
				}

				processBoardElements();
			}

			processCleanup();
		}

		@Override
		public void onDealNewCards(DealNewCardsPacket packet) {
			clientPlayer.takeNonStuckCardsBack();
			clientPlayer.giveCards(packet.getCards());
			refreshShownCards();

			roundReadyButton.setState(false);
		}

		@Override
		public void onServerChat(ServerChatPacket packet) {
			out.println(packet.getPlayer() + ": " + packet.getMessage());
			out.flush();
		}

		@Override
		public void onKicked(KickedPacket packet) {
			gameScreen.removeClient(RoboRallyClient.this, packet.getReason());
		}

		@Override
		public void onOtherPlayerKicked(OtherPlayerKickedPacket packet) {
			out.printf("%s was kicked for %s%n", packet.getPlayerName(), packet.getReason());
			out.flush();

			removePlayer(packet.getPlayerName());
		}

		@Override
		public void onPlayerLeftGame(PlayerLeftGamePacket packet) {
			out.printf("%s left the game. Reason: %s%n", packet.getPlayerName(), packet.getReason());
			out.flush();

			removePlayer(packet.getPlayerName());
		}

		private void removePlayer(String playerName) {
			players.removeIf(player -> player.getName().equals(playerName));
			board.updateActiveRobots(players.stream().map(Player::getRobot).collect(Collectors.toList()));
			refreshPlayerList();
		}

		@Override
		public void onServerClosing(ServerClosingPacket packet) {
			screenController.returnToMainMenu();
			screenController.pushInputScreen(new NotificationScreen(screenController, "Server closed"));
		}
	}
}