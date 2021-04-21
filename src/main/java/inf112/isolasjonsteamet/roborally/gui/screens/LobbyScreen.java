package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.gui.ToggleButton;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.KickPlayerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.LobbyReadyUpdatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.RequestLobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.SetNewHostPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A lobby screen, where players can join and indicate they are ready before the game starts.
 */
public class LobbyScreen extends StageScreen {

	private final ScreenController screenController;

	private final Server server;
	private final Client client;

	private final ServerLobby serverLobby;
	private final ClientLobby clientLobby;

	private ToggleButton isReadyButton;
	private ToggleButton gameStartingButton;

	private Table playerList;
	private Map<String, Boolean> players = new TreeMap<>();
	private String currentHost;
	private @Nullable String serverPlayer;

	/**
	 * Creates a new lobby screen as the host player.
	 *
	 * @param server A server to send packets to the other players.
	 * @param hostPlayer Name of the host player.
	 * @param client The client to communicate to the server like any other player.
	 */
	@SuppressWarnings("NullableProblems")
	public LobbyScreen(ScreenController screenController, Server server, String hostPlayer, Client client) {
		this.screenController = screenController;
		this.server = server;
		this.client = client;

		serverPlayer = hostPlayer;
		serverLobby = new ServerLobby(hostPlayer);
		clientLobby = new ClientLobby();
	}

	/**
	 * Creates a new lobby screen as a client player.
	 *
	 * @param client The client to communicate with the server.
	 */
	public LobbyScreen(ScreenController screenController, Client client) {
		this.screenController = screenController;
		this.server = null;
		this.client = client;

		serverLobby = null;
		clientLobby = new ClientLobby();
	}

	@Override
	void create() {
		super.create();

		var table = createRootTable();
		table.add("Multiplayer lobby").colspan(3);
		table.row();

		table.add("Players:");
		table.row();

		playerList = new Table(skin);
		refreshPlayerList();
		table.add(playerList).colspan(2);
		table.row();

		isReadyButton = new ToggleButton("Not ready", "Ready", false, skin, this::setClientReady);
		table.add(isReadyButton.getButton());

		// We initialize the button here, but only add it to the GUI if we're actually on the server
		// We still use some of the state of if a game is starting on the client as well
		gameStartingButton = new ToggleButton("Cancel start game", "Start game", false, skin, gameStarting -> {
			if (!gameStarting) {
				broadcastCancelStartGame();
			} else {
				broadcastGameStarting();
			}
		});
		if (server != null) {
			table.add(gameStartingButton.getButton());
			server.addListener(serverLobby);
		}

		var leaveButton = new TextButton("Leave", skin);
		leaveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				client.disconnect("Leaving");
				if (server != null) {
					server.close("Leaving");
				}
				screenController.returnToMainMenu();
			}
		});
		table.add(leaveButton);

		table.row();

		client.addListener(clientLobby);
		client.sendToServer(RequestLobbyInfoPacket.INSTANCE);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (server != null) {
			server.close("Disposing");
		} else {
			client.disconnect("Disposing");
		}
	}

	private void refreshPlayerList() {
		playerList.clearChildren();

		players.forEach((player, ready) -> {
			if (player.equals(currentHost)) {
				playerList.add("Host").width(40);
			} else if (player.equals(serverPlayer)) {
				playerList.add("Server").width(40);
			} else {
				playerList.add().width(40);
			}
			playerList.add(player);

			if (ready) {
				playerList.add("Ready!");
			} else {
				playerList.add();
			}

			if (server != null && !player.equals(currentHost) && !player.equals(serverPlayer)) {
				var kickButton = new TextButton("Kick", skin);
				var kickReasonField = new TextField("", skin);

				kickButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						kickPlayer(player, kickReasonField.getText());
					}
				});

				playerList.add(kickButton).padLeft(10);
				playerList.add(kickReasonField).padLeft(5);
			}

			playerList.row();
		});
	}

	private void addPlayerToListing(String playerName) {
		players.put(playerName, false);
		refreshPlayerList();
	}

	private void removePlayerFromListing(String playerName) {
		players.remove(playerName);
		refreshPlayerList();
	}

	private void prepareStartGame() {
		gameStartingButton.setStateQuietly(true);
		setReadyCountdownState(3);
	}

	private void setReadyCountdownState(int secondsLeft) {
		// If the start is cancelled, then the delayed task is probably still running,
		// so we check here if we should continue
		if (gameStartingButton.getState()) {
			isReadyButton.getButton().setText(Integer.toString(secondsLeft));

			if (secondsLeft > 0) {
				CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
						.execute(() -> Gdx.app.postRunnable(() -> setReadyCountdownState(secondsLeft - 1)));
			} else {
				client.removeListener(clientLobby);
				if (server != null) {
					server.removeListener(serverLobby);
				}

				var playerInfos = ImmutableList.<PlayerInfo>builder();
				for (String playerName : players.keySet()) {
					Client playerClient = null;
					if (playerName.equals(client.getUsername())) {
						playerClient = client;
					}

					playerInfos.add(new PlayerInfo(playerName, playerClient, playerClient != null));
				}

				screenController.startGame("example.tmx", playerInfos.build(), currentHost, serverPlayer, server);
			}
		}
	}

	private void cancelStartGame() {
		gameStartingButton.setStateQuietly(false);
		isReadyButton.refreshText();
	}

	// ---------- Client methods ----------

	private void setClientReady(boolean isReady) {
		client.sendToServer(new LobbyReadyUpdatePacket(isReady));
	}

	// ---------- Server methods ----------

	private void kickPlayer(String player, String reason) {
		Objects.requireNonNull(server).kickPlayer(player, reason);
	}

	private void broadcastGameStarting() {
		Objects.requireNonNull(server).sendToAllPlayers(new GameStartingPacket(true));
	}

	private void broadcastCancelStartGame() {
		Objects.requireNonNull(server).sendToAllPlayers(new GameStartingPacket(false));
	}

	@SuppressWarnings("ConstantConditions")
	private class ServerLobby extends ServerPacketAdapter {

		private final Map<String, Boolean> isPlayerReady = new HashMap<>();
		private final String hostPlayer;

		public ServerLobby(String hostPlayer) {
			this.hostPlayer = hostPlayer;
			for (var player : server.getPlayers()) {
				isPlayerReady.put(player, false);
			}
		}

		@Override
		public void handle(String player, Client2ServerPacket packet) {
			if (player == null) {
				return;
			}

			if (!isPlayerReady.containsKey(player)) {
				isPlayerReady.put(player, false);
			}

			super.handle(player, packet);
		}

		@Override
		public void onLobbyReadyUpdate(String player, LobbyReadyUpdatePacket packet) {
			isPlayerReady.put(player, packet.isReady());
			server.sendToAllPlayers(new LobbyInfoPacket(isPlayerReady, hostPlayer, serverPlayer));
		}

		@Override
		public void onRequestLobbyInfo(String player, RequestLobbyInfoPacket packet) {
			server.sendToPlayer(player, new LobbyInfoPacket(isPlayerReady, hostPlayer, serverPlayer));
		}

		@Override
		public void onClientDisconnecting(String player, ClientDisconnectingPacket packet) {
			isPlayerReady.remove(player);
			server.sendToAllPlayers(new PlayerLeftGamePacket(player, packet.getReason()));
		}

		@Override
		public void onKickPlayer(String player, KickPlayerPacket packet) {
			if (player.equals(hostPlayer) && !packet.getPlayerName().equals(serverPlayer)) {
				LobbyScreen.this.kickPlayer(packet.getPlayerName(), packet.getReason());
			}
		}

		@Override
		public @Nullable Client2ServerPacket refine(Client2ServerPacket packet) {
			return packet;
		}
	}

	private class ClientLobby extends ClientPacketAdapter {

		@Override
		public void onGameStarting(GameStartingPacket packet) {
			if (packet.isGameStarting()) {
				prepareStartGame();
			} else {
				cancelStartGame();
			}
		}

		@Override
		public void onLobbyInfo(LobbyInfoPacket packet) {
			players = new HashMap<>(packet.getIsPlayerReady());
			currentHost = packet.getHost();
			serverPlayer = packet.getServerPlayer();
			refreshPlayerList();
		}

		@Override
		public void onSetNewHost(SetNewHostPacket packet) {
			currentHost = packet.getNewHost();
		}

		@Override
		public void onPlayerJoinedGame(PlayerJoinedGamePacket packet) {
			addPlayerToListing(packet.getPlayerName());
		}

		@Override
		public void onPlayerLeftGame(PlayerLeftGamePacket packet) {
			removePlayerFromListing(packet.getPlayerName());
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
			removePlayerFromListing(packet.getPlayerName());
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
