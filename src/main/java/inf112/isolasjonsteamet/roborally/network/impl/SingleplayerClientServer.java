package inf112.isolasjonsteamet.roborally.network.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
	private final List<ClientPacketListener<?>> clientPacketListeners = new CopyOnWriteArrayList<>();

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
	}

	private void checkNotClosed() {
		checkState(!isClosed);
	}

	@Override
	public void sendToServer(Client2ServerPacket packet) {
		checkNotClosed();
		serverPacketListeners.forEach(listener -> listener.handleIfPossible(activePlayer, packet));
	}

	public void setActivePlayer(String player) {
		checkNotClosed();
		this.activePlayer = activePlayer;
	}

	@Override
	public void sendToAllPlayers(Server2ClientPacket packet) {
		checkNotClosed();
		clientPacketListeners.forEach(listener -> listener.handleIfPossible(packet));
	}

	@Override
	public void sendToPlayer(String player, Server2ClientPacket packet) {
		checkNotClosed();
		if (players.contains(player)) {
			clientPacketListeners.forEach(listener -> listener.handleIfPossible(packet));
		}
	}

	@Override
	public void addListener(ClientPacketListener<?> listener) {
		checkNotClosed();
		clientPacketListeners.add(listener);
	}

	@Override
	public void addListener(ServerPacketListener<?> listener) {
		checkNotClosed();
		serverPacketListeners.add(listener);
	}

	@Override
	public void removeListener(ClientPacketListener<?> listener) {
		checkNotClosed();
		clientPacketListeners.remove(listener);
	}

	@Override
	public void removeListener(ServerPacketListener<?> listener) {
		checkNotClosed();
		serverPacketListeners.remove(listener);
	}

	@Override
	public CompletableFuture<Void> disconnect(@Nullable String reason) {
		sendToServer(new ClientDisconnectingPacket(reason));
		players.remove(activePlayer);
		if (!players.isEmpty()) {
			activePlayer = players.get(0);
		} else {
			isClosed = true;
		}

		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> close(@Nullable String reason) {
		sendToAllPlayers(new ServerClosingPacket(reason));
		players.clear();
		isClosed = true;
		return CompletableFuture.completedFuture(null);
	}
}
