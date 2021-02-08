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
import org.junit.jupiter.api.Test;

public class NetworkSpec {
	private final NettyServerImpl server = new NettyServerImpl("localhost", 8000, "testgame");

	public <T> T await(CompletableFuture<T> future, int seconds) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(seconds, TimeUnit.SECONDS);
	}

	public <T> T await(CompletableFuture<T> future) throws InterruptedException, ExecutionException, TimeoutException {
		return await(future, 5);
	}

	private <T extends Server2ClientPacket> CompletableFuture<T> nextClientPacket(Client client, Class<T> clazz) {
		var promise = new CompletableFuture<T>();
		client.addListener(ClientPacketListener.of(clazz, promise::complete));
		return promise;
	}

	private <T extends Client2ServerPacket> CompletableFuture<T> nextServerPacket(Server server, Class<T> clazz) {
		var promise = new CompletableFuture<T>();
		server.addListener(ServerPacketListener.of(clazz, o -> promise.complete(o.getPacket())));
		return promise;
	}

	private <T extends Server2ClientPacket> T awaitNextClientPacket(Client client, Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
		return await(nextClientPacket(client, clazz));
	}

	private <T extends Client2ServerPacket> T awaitNextServerPacket(Server server, Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException {
		return await(nextServerPacket(server, clazz));
	}

	@BeforeEach
	public void startServer() throws InterruptedException, ExecutionException, TimeoutException {
		server.start();
		await(server.ready());
	}

	@AfterEach
	public void stopServer() throws InterruptedException, ExecutionException, TimeoutException {
		await(server.close(null));
	}

	public Client startClient() throws InterruptedException, ExecutionException, TimeoutException {
		var client = new NettyClientImpl("localhost", 8000);
		client.start();
		await(client.ready());
		return client;
	}

	public Client startClientAfterInfo() throws InterruptedException, ExecutionException, TimeoutException {
		var client = startClient();
		awaitNextClientPacket(client, GameInfoPacket.class);
		return client;
	}

	@DisplayName("A GameInfo packet is sent to all clients that connect")
	@Test
	public void gameInfoSent() throws InterruptedException, ExecutionException, TimeoutException {
		Client client = startClient();
		var gameInfo = awaitNextClientPacket(client, GameInfoPacket.class);
		assertEquals(gameInfo.getGameName(), "testgame");
		assertTrue(gameInfo.getPlayers().isEmpty());
	}

	@DisplayName("A client which connects after another client has joined the game, should see the previous player in the player list")
	@Test
	public void joinGameNextGameInfo() throws InterruptedException, ExecutionException, TimeoutException {
		var packet = new GameJoinPacket("foobar");
		var nextJoinPacket = nextServerPacket(server, GameJoinPacket.class);
		var client1 = startClientAfterInfo();
		client1.sendToServer(packet);
		assertEquals(packet, await(nextJoinPacket));

		var client2 = startClient();
		var gameInfo = awaitNextClientPacket(client2, GameInfoPacket.class);
		assertEquals(gameInfo.getPlayers(), List.of("foobar"));
	}

	@DisplayName("When two clients connect at the same time, and one joins, the other should receive an updated GameInfo packet")
	@Test
	public void twoSimultaneousJoinsUpdatedGameInfo() throws InterruptedException, ExecutionException, TimeoutException {
		var client1 = startClientAfterInfo();
		var client2 = startClientAfterInfo();

		client1.sendToServer(new GameJoinPacket("foobar"));
		var nextResult = nextClientPacket(client1, GameJoinResultPacket.class);
		var nextUpdatedGameInfo = nextClientPacket(client2, GameInfoPacket.class);

		assertEquals(await(nextResult).getResult(), JoinResult.SUCCESS);
		assertEquals(await(nextUpdatedGameInfo).getPlayers(), List.of("foobar"));
	}

	@DisplayName("When a player joins a game, existing players should be notified of this")
	@Test
	public void notifyExistingPlayers() throws InterruptedException, ExecutionException, TimeoutException {
		var client1 = startClientAfterInfo();
		client1.sendToServer(new GameJoinPacket("foobar"));
		assertEquals(awaitNextClientPacket(client1, GameJoinResultPacket.class).getResult(), JoinResult.SUCCESS);

		var client2 = startClientAfterInfo();
		client2.sendToServer(new GameJoinPacket("foobaz"));
		var nextPlayerJoinedGame = nextClientPacket(client1, PlayerJoinedGamePacket.class);
		var nextResult = nextClientPacket(client2, GameJoinResultPacket.class);

		assertEquals(await(nextPlayerJoinedGame).getPlayerName(), "foobaz");
		assertEquals(await(nextResult).getResult(), JoinResult.SUCCESS);
	}

	@DisplayName("Two players should not be able to join with the same name")
	@Test
	public void denySameName() throws InterruptedException, ExecutionException, TimeoutException {
		var client1 = startClientAfterInfo();
		client1.sendToServer(new GameJoinPacket("foobar"));
		assertEquals(awaitNextClientPacket(client1, GameJoinResultPacket.class).getResult(), JoinResult.SUCCESS);

		var client2 = startClientAfterInfo();
		client2.sendToServer(new GameJoinPacket("foobar"));
		var nextResult = nextClientPacket(client2, GameJoinResultPacket.class);

		assertEquals(await(nextResult).getResult(), JoinResult.NAME_IN_USE);
	}
}
