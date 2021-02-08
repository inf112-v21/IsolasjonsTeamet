package inf112.isolasjonsteamet.roborally.network;

import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.impl.NettyClientImpl;
import inf112.isolasjonsteamet.roborally.network.impl.PacketProtocol;
import inf112.isolasjonsteamet.roborally.network.impl.ProtocolException;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket.JoinResult;
import java.util.Map;
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

	/**
	 * Connects to the given address, and verifies that the server and client are compatible with each other.
	 *
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @return A "tuple" containing the client, and the current game info.
	 */
	static CompletableFuture<Map.Entry<Client, GameInfoPacket>> connectAndVerify(String host, int port) {
		var client = new NettyClientImpl(host, port);
		var promise = new CompletableFuture<Map.Entry<Client, GameInfoPacket>>();

		ClientPacketListener.next(client, GameInfoPacket.class).thenAccept(info -> {
			if (info.getProtocol() == PacketProtocol.PROTOCOL) {
				client.ready().whenComplete((v, e) -> {
					if (e != null) {
						promise.completeExceptionally(e);
					} else {
						promise.complete(Map.entry(client, info));
					}
				});
			} else {
				client.disconnect("Incompatible client protocol");
				promise.completeExceptionally(new ProtocolException(info.getProtocol(), info.getRequiredVersion()));
			}
		});

		return promise;
	}

	/**
	 * Joins the game hosted on the server this client is connected to.
	 *
	 * @param playerName The playername to use within the game.
	 * @return The result of joining the game.
	 */
	default CompletableFuture<JoinResult> joinGame(String playerName) {
		var response = ClientPacketListener.next(this, GameJoinResultPacket.class).thenApply(GameJoinResultPacket::getResult);
		sendToServer(new GameJoinPacket(playerName));
		return response;
	}
}
