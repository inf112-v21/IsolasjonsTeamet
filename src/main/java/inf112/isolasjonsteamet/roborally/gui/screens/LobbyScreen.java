package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.LobbyReadyUpdatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.RequestLobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LobbyScreen extends StageScreen {

	private final ScreenController screenController;

	private final Server server;
	private final Client client;

	private final ServerLobby serverLobby;
	private final ClientLobby clientLobby;

	private boolean isReady;
	private boolean gameStarting;

	private TextButton readyButton;
	private TextButton startGameButton;

	private Table playerList;
	private Map<String, Boolean> players = new HashMap<>();
	private String currentHost;

	public LobbyScreen(ScreenController screenController, Server server, String hostPlayer, Client client) {
		this.screenController = screenController;
		this.server = server;
		this.client = client;

		serverLobby = new ServerLobby(hostPlayer);
		clientLobby = new ClientLobby();
	}

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
		table.add("Multiplayer lobby").colspan(2);
		table.row();

		table.add("Players:");
		table.row();

		playerList = new Table(skin);
		refreshPlayerList();
		table.add(playerList);
		table.row();

		readyButton = new TextButton("Ready", skin);
		readyButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				toggleClientReady();

				if (isReady) {
					readyButton.setText("Not ready");
				} else {
					readyButton.setText("Ready");
				}
			}
		});

		table.add(readyButton);

		if (server != null) {
			startGameButton = new TextButton("Start game", skin);
			startGameButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (gameStarting) {
						broadcastCancelStartGame();
					} else {
						broadcastGameStarting();
					}
				}
			});
			table.add(startGameButton);

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
		client.sendToServer(new RequestLobbyInfoPacket());
	}

	@Override
	public void hide() {
		super.hide();
	}

	private void refreshPlayerList() {
		playerList.clearChildren();

		players.forEach((player, ready) -> {
			if (player.equals(currentHost)) {
				playerList.add("Host").width(40);
			} else {
				playerList.add().width(40);
			}
			playerList.add(player);

			if (ready) {
				playerList.add("Ready!");
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
		gameStarting = true;
		if (startGameButton != null) {
			startGameButton.setText("Cancel start game");
		}
		setReadyCountdownState(9);
	}

	private void setReadyCountdownState(int secondsLeft) {
		// If the start is cancelled, then the delayed task is probably still running,
		// so we check here if we should continue
		if (gameStarting) {
			readyButton.setText(Integer.toString(secondsLeft));

			if (secondsLeft > 0) {
				CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
						.execute(() -> Gdx.app.postRunnable(() -> setReadyCountdownState(secondsLeft - 1)));
			} else {
				client.removeListener(clientLobby);
				if (server != null) {
					server.removeListener(serverLobby);
				}

				//TODO: Pass client and server in here as well
				screenController.setInputScreen(new GameScreen());
			}
		}
	}

	private void cancelStartGame() {
		gameStarting = false;
		if (startGameButton != null) {
			startGameButton.setText("Start game");
		}

		if (isReady) {
			readyButton.setText("Not ready");
		} else {
			readyButton.setText("Ready");
		}
	}

	// ---------- Client methods ----------

	private void toggleClientReady() {
		isReady = !isReady;

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

	private class ServerLobby implements ServerPacketListener<Client2ServerPacket> {

		private final Map<String, Boolean> isPlayerReady = new HashMap<>();
		private final String hostPlayer;

		public ServerLobby(String hostPlayer) {
			this.hostPlayer = hostPlayer;
			for (var player : server.getPlayers()) {
				isPlayerReady.put(player, false);
			}
		}

		@Override
		public void handle(@Nullable String player, Client2ServerPacket packet) {
			if (player == null) {
				return;
			}

			if (!isPlayerReady.containsKey(player)) {
				isPlayerReady.put(player, false);
			}

			if (packet instanceof LobbyReadyUpdatePacket) {
				isPlayerReady.put(player, ((LobbyReadyUpdatePacket) packet).isReady());
				server.sendToAllPlayers(new LobbyInfoPacket(isPlayerReady, hostPlayer));
			} else if (packet instanceof RequestLobbyInfoPacket) {
				server.sendToPlayer(player, new LobbyInfoPacket(isPlayerReady, hostPlayer));
			} else if (packet instanceof ClientDisconnectingPacket) {
				isPlayerReady.remove(player);
				server.sendToAllPlayers(new PlayerLeftGamePacket(player));
			}
		}

		@Override
		public @Nullable Client2ServerPacket refine(Client2ServerPacket packet) {
			return packet;
		}
	}

	private class ClientLobby implements ClientPacketListener<Server2ClientPacket> {

		@Override
		public void handle(Server2ClientPacket packet) {
			if (packet instanceof GameStartingPacket) {
				if (((GameStartingPacket) packet).isGameStarting()) {
					prepareStartGame();
				} else {
					cancelStartGame();
				}
			} else if (packet instanceof LobbyInfoPacket) {
				players = new HashMap<>(((LobbyInfoPacket) packet).getIsPlayerReady());
				System.out.println(players);
				currentHost = ((LobbyInfoPacket) packet).getHost();
				refreshPlayerList();
			} else if (packet instanceof PlayerJoinedGamePacket) {
				addPlayerToListing(((PlayerJoinedGamePacket) packet).getPlayerName());
			} else if (packet instanceof PlayerLeftGamePacket) {
				removePlayerFromListing(((PlayerLeftGamePacket) packet).getPlayerName());
			} else if (packet instanceof KickedPacket) {
				screenController.returnToMainMenu();
				screenController.pushInputScreen(
						new NotificationScreen(screenController, "Kicked: " + ((KickedPacket) packet).getReason())
				);
			} else if (packet instanceof OtherPlayerKickedPacket) {
				removePlayerFromListing(((OtherPlayerKickedPacket) packet).getPlayerName());
			} else if (packet instanceof ServerClosingPacket) {
				screenController.returnToMainMenu();
				screenController.pushInputScreen(new NotificationScreen(screenController, "Server closed"));
			}
		}

		@Override
		public @Nullable Server2ClientPacket refine(Server2ClientPacket packet) {
			return packet;
		}
	}
}
