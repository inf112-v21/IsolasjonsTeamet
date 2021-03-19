package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a server which clients can connect to.
 */
public interface Server {

	/**
	 * Send a packet to all active players of the game.
	 *
	 * @param packet The packet to send to all the players.
	 */
	void sendToAllPlayers(Server2ClientPacket packet);

	/**
	 * Send a packet to a single player in a game.
	 *
	 * @param player The player to send the packet to
	 * @param packet The packet to send
	 */
	void sendToPlayer(String player, Server2ClientPacket packet);

	/** Add a listener for packets sent from the clients. */
	void addListener(ServerPacketListener<?> listener);

	/** Remove a packet listener. */
	void removeListener(ServerPacketListener<?> listener);

	/**
	 * Gracefully disconnect everyone connected to this server, and then shuts down the server, optionally with a
	 * reason.
	 */
	CompletableFuture<Void> close(@Nullable String reason);
}
