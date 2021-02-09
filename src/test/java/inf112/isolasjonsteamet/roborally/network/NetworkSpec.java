package inf112.isolasjonsteamet.roborally.network;

import static org.junit.jupiter.api.Assertions.*;

import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.impl.NettyServerImpl;
import inf112.isolasjonsteamet.roborally.network.impl.NettyClientImpl;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket.JoinResult;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NetworkSpec {

	private final NettyServerImpl server = new NettyServerImpl("localhost", 8000, "testgame");

	public <T> T await(CompletableFuture<T> future, int seconds) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(seconds, TimeUnit.SECONDS);
	}

	public <T> T await(CompletableFuture<T> future) throws InterruptedException, ExecutionException, TimeoutException {
		return await(future, 10);
	}

	private <T extends Server2ClientPacket> CompletableFuture<T> nextClientPacket(Client client, Class<T> clazz) {
		var promise = new CompletableFuture<T>();
		client.addListener(ClientPacketListener.of(clazz, promise::complete));
		return promise;
	}

	private <T extends Client2ServerPacket> CompletableFuture<T> nextServerPacket(Class<T> clazz) {
		var promise = new CompletableFuture<T>();
		server.addListener(ServerPacketListener.of(clazz, o -> promise.complete(o.getPacket())));
		return promise;
	}

	private <T extends Server2ClientPacket> T awaitNextClientPacket(Client client, Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
		return await(nextClientPacket(client, clazz));
	}

	private <T extends Client2ServerPacket> T awaitNextServerPacket(Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
		return await(nextServerPacket(clazz));
	}

	@SuppressWarnings("unchecked")
	private <T extends Client2ServerPacket> T awaitSendServerPacket(Client client, T packet) throws InterruptedException, ExecutionException, TimeoutException {
		var promise = nextServerPacket((Class<T>) packet.getClass());
		client.sendToServer(packet);
		return await(promise);
	}

	private JoinResult awaitJoinGame(Client client, String username) throws InterruptedException, ExecutionException, TimeoutException {
		var joinPacket = new GameJoinPacket(username);
		var resultPromise = nextClientPacket(client, GameJoinResultPacket.class);
		var receivedJoinPacket = awaitSendServerPacket(client, joinPacket);
		assertEquals(joinPacket, receivedJoinPacket);
		return await(resultPromise).getResult();
	}

	private void awaitJoinGameSuccessfull(Client client, String username) throws InterruptedException, ExecutionException, TimeoutException {
		assertEquals(awaitJoinGame(client, username), JoinResult.SUCCESS);
	}

	@BeforeEach
	public void startServer() throws InterruptedException, ExecutionException, TimeoutException {
		server.start();
		await(server.ready(), 15);
	}

	@AfterEach
	public void stopServer() throws InterruptedException, ExecutionException, TimeoutException {
		await(server.close(null), 15);
	}

	public NettyClientImpl createClient() {
		return new NettyClientImpl("localhost", 8000);
	}

	public void runClient(NettyClientImpl client) throws InterruptedException, ExecutionException, TimeoutException {
		client.start();
		await(client.ready(), 15);
	}

	public Client startClient() throws InterruptedException, ExecutionException, TimeoutException {
		var client = createClient();
		runClient(client);
		return client;
	}

	public Client startClientAfterInfo() throws InterruptedException, ExecutionException, TimeoutException {
		var client = createClient();
		var gameInfoPromise = nextClientPacket(client, GameInfoPacket.class);
		runClient(client);
		await(gameInfoPromise);
		return client;
	}

	@DisplayName("When a client connects, it should receive a GameInfo packet")
	@Test
	public void gameInfoSent() throws InterruptedException, ExecutionException, TimeoutException {
		var client = createClient();
		var gameInfoPromise = nextClientPacket(client, GameInfoPacket.class);
		runClient(client);
		var gameInfo = await(gameInfoPromise);
		assertEquals(gameInfo.getGameName(), "testgame");
		assertTrue(gameInfo.getPlayers().isEmpty());
	}

	@Nested
	@DisplayName("Given that one player named foobar has already joined the game")
	class OnePlayerJoined {

		private Client startFoobar() throws InterruptedException, ExecutionException, TimeoutException {
			var client = startClientAfterInfo();
			awaitJoinGameSuccessfull(client, "foobar");
			return client;
		}

		@DisplayName("When another client connects, it should see the previous player in the player list")
		@Test
		public void joinGameNextGameInfo() throws InterruptedException, ExecutionException, TimeoutException {
			startFoobar();

			var client2 = createClient();
			var gameInfoPromise = nextClientPacket(client2, GameInfoPacket.class);
			runClient(client2);
			var gameInfo = await(gameInfoPromise);
			assertEquals(gameInfo.getPlayers(), List.of("foobar"));
		}

		@DisplayName("When another client tries to connect named foobar, it should be denied because the name is in use")
		@Test
		public void denySameName() throws InterruptedException, ExecutionException, TimeoutException {
			startFoobar();

			var client2 = startClientAfterInfo();
			var result = awaitJoinGame(client2, "foobar");

			assertEquals(result, JoinResult.NAME_IN_USE);
		}

		@DisplayName("When another player joins a game, existing players should be notified of this")
		@Test
		public void notifyExistingPlayers() throws InterruptedException, ExecutionException, TimeoutException {
			var foobar = startFoobar();

			var client2 = startClientAfterInfo();
			var nextPlayerJoinedGame = nextClientPacket(foobar, PlayerJoinedGamePacket.class);
			awaitJoinGameSuccessfull(client2, "foobaz");

			assertEquals(await(nextPlayerJoinedGame).getPlayerName(), "foobaz");
		}
	}

	@Nested
	@DisplayName("Given that one client has already connected")
	class OnePlayerConnected {

		@DisplayName("When another client connects and joins the game, the original client should receive an updated GameInfo packet")
		@Test
		public void twoSimultaneousJoinsUpdatedGameInfo() throws InterruptedException, ExecutionException, TimeoutException {
			var client1 = startClientAfterInfo();
			var client2 = startClientAfterInfo();

			var updatedGameInfoPromise = nextClientPacket(client1, GameInfoPacket.class);
			awaitJoinGameSuccessfull(client2, "foobar");
			var updatedGameInfo = await(updatedGameInfoPromise);

			assertEquals(updatedGameInfo.getPlayers(), List.of("foobar"));
		}
	}
}
