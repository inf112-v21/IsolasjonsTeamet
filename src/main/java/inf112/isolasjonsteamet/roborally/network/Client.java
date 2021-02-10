package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a client connected to a server.
 */
public interface Client {

	/** Send a packet to the server. */
	void sendToServer(Client2ServerPacket packet);

	/** Add a listener for packets sent from the server. */
	void addListener(ClientPacketListener<?> listener);

	/** Remove a packet listener. */
	void removeListener(ClientPacketListener<?> listener);

	/** Disconnects gracefully from the server, optionally with a reason. */
	CompletableFuture<Void> disconnect(@Nullable String reason);
}
