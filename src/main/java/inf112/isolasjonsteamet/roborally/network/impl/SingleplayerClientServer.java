package inf112.isolasjonsteamet.roborally.network.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A dummy server which just passes through packets sent on one side to the other side's listeners immediately.
 *
 * <p>
 * Only one player can send packets to the server at a time. The player doing this can be specified with {@link
 * #setActivePlayer(String)}. This client-server is handshake-less. There is no startup procedure. Shutdown happens as
 * usual. When all players have disconnected from the server, or the server has closed, this object will enter a state
 * where no more methods must be called on it. From this point forwards, all methods will throw, as the server has
 * closed.
 * </p>
 */
public class SingleplayerClientServer implements Client, Server {

	private final List<ServerPacketListener<?>> serverPacketListeners = new CopyOnWriteArrayList<>();
	private final Map<String, List<ClientPacketListener<?>>> clientPacketListeners = new ConcurrentHashMap<>();

	private final List<String> players;
	private String activePlayer;
	private boolean isClosed;

	/**
	 * Creates a new server-client with the given players connected. At least one player must be specified.
	 */
	public SingleplayerClientServer(Set<String> players) {
		checkArgument(!players.isEmpty(), "Can't construct a singleplayer client server with no players");
		this.players = new CopyOnWriteArrayList<>(players);
		this.activePlayer = this.players.get(0);

		for (String player : players) {
			clientPacketListeners.put(player, new CopyOnWriteArrayList<>());
		}
	}

	private void checkNotClosed() {
		checkState(!isClosed);
	}

	@Override
	public void sendToServer(Client2ServerPacket packet) {
		checkNotClosed();
		serverPacketListeners.forEach(listener -> listener.handleIfPossible(activePlayer, packet));
	}

	@Override
	public String getUsername() {
		checkNotClosed();
		return activePlayer;
	}

	public void setActivePlayer(String player) {
		this.activePlayer = player;
	}

	@Override
	public void sendToAllPlayers(Server2ClientPacket packet) {
		checkNotClosed();
		clientPacketListeners.values().forEach(
				listeners -> listeners.forEach(listener -> listener.handleIfPossible(packet))
		);
	}

	@Override
	public void sendToPlayer(String player, Server2ClientPacket packet) {
		checkNotClosed();
		if (players.contains(player)) {
			clientPacketListeners.get(player).forEach(listener -> listener.handleIfPossible(packet));
		}
	}

	@Override
	public void addListener(ClientPacketListener<?> listener) {
		checkNotClosed();
		clientPacketListeners.get(activePlayer).add(listener);
	}

	@Override
	public void addListener(ServerPacketListener<?> listener) {
		checkNotClosed();
		serverPacketListeners.add(listener);
	}

	@Override
	public void removeListener(ClientPacketListener<?> listener) {
		checkNotClosed();
		clientPacketListeners.get(activePlayer).remove(listener);
	}

	@Override
	public void removeListener(ServerPacketListener<?> listener) {
		checkNotClosed();
		serverPacketListeners.remove(listener);
	}

	@Override
	public CompletableFuture<Void> disconnect(@Nullable String reason) {
		if (!isClosed) {
			sendToServer(new ClientDisconnectingPacket(reason));

			// The server might decide to close itself in the above call if the disconnecting player was the "host"
			if (!isClosed) {
				players.remove(activePlayer);
				clientPacketListeners.remove(activePlayer);

				if (!players.isEmpty()) {
					activePlayer = players.get(0);
				} else {
					isClosed = true;
				}
			}
		}

		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> close(@Nullable String reason) {
		if (!isClosed) {
			sendToAllPlayers(new ServerClosingPacket(reason));
			players.clear();
			clientPacketListeners.clear();
			serverPacketListeners.clear();
			isClosed = true;
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void kickPlayer(String player, String reason) {
		if (players.contains(player)) {
			sendToPlayer(player, new KickedPacket(reason));
			players.remove(player);
			sendToAllPlayers(new OtherPlayerKickedPacket(player, reason));
		}
	}

	@Override
	public List<String> getPlayers() {
		return ImmutableList.copyOf(players);
	}
}
