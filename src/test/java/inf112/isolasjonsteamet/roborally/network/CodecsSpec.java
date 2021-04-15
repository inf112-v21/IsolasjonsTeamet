package inf112.isolasjonsteamet.roborally.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.LobbyReadyUpdatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.lobby.RequestLobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.network.impl.PacketProtocol;
import inf112.isolasjonsteamet.roborally.network.impl.PacketRegistration;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket.JoinResult;
import inf112.isolasjonsteamet.roborally.network.c2spackets.KickPlayerPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.KickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerJoinedGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.GameStartingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.lobby.LobbyInfoPacket;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class CodecsSpec {

	private final static String SURVIVE_ENCODE_DECODE = " should survive an encode-decode trip unchanged";

	public <A> void assertSurvivesRoundtrip(A packet, Codec<A> codec) {
		ByteBuf buf = Unpooled.buffer();

		codec.write(packet, buf);
		A readPacket = codec.read(buf);
		assertEquals(packet, readPacket);
	}

	@SuppressWarnings("unchecked")
	public <A extends Packet> void assertSurvivesRoundtrip(A packet) {
		Optional<PacketRegistration<A>> packetRegistration =
				Arrays.stream(PacketProtocol.REGISTRATIONS)
						.filter(p -> p.getClazz().equals(packet.getClass()))
						.map(p -> (PacketRegistration<A>) p)
						.findAny();

		if (packetRegistration.isPresent()) {
			assertSurvivesRoundtrip(packet, packetRegistration.get().getCodec());
		} else {
			fail("No packet codec for " + packet.getClass() + " found");
		}
	}

	//Client->Server packets

	//Game packets

	@DisplayName("UpdatePlayerStatePacket" + SURVIVE_ENCODE_DECODE)
	@Test
	public void updatePlayerState() {
		assertSurvivesRoundtrip(new UpdatePlayerStatePacket(new Coordinate(0, 0), Orientation.NORTH));
	}

	@Nested
	@DisplayName("UpdateRoundReadyPacket" + SURVIVE_ENCODE_DECODE)
	class UpdateRoundReady {
		ImmutableList<CardType> givenCards = ImmutableList.of(Cards.ROTATE_LEFT, Cards.ROTATE_RIGHT, Cards.BACK_UP);
		ImmutableList<CardType> chosenCards = ImmutableList.of(Cards.MOVE_ONE, Cards.ROTATE_LEFT, Cards.MOVE_ONE);

		@DisplayName("With not ready and no cards")
		@Test
		public void noCards() {
			assertSurvivesRoundtrip(new UpdateRoundReadyPacket(false, ImmutableList.of(), ImmutableList.of()));
		}

		@DisplayName("With not ready and no chosen cards")
		@Test
		public void noChosenCards() {
			assertSurvivesRoundtrip(new UpdateRoundReadyPacket(false, ImmutableList.of(), givenCards));
		}

		@DisplayName("With not ready and given no cards")
		@Test
		public void noGivenCards() {
			assertSurvivesRoundtrip(new UpdateRoundReadyPacket(false, chosenCards, ImmutableList.of()));
		}

		@DisplayName("With given and chosen cards, but not ready")
		@Test
		public void notReady() {
			assertSurvivesRoundtrip(new UpdateRoundReadyPacket(false, chosenCards, givenCards));
		}

		@DisplayName("With given and chosen cards, and ready")
		@Test
		public void ready() {
			assertSurvivesRoundtrip(new UpdateRoundReadyPacket(true, chosenCards, givenCards));
		}
	}

	//Lobby packets

	@DisplayName("LobbyReadyUpdate" + SURVIVE_ENCODE_DECODE)
	@Test
	public void lobbyReadyUpdate() {
		assertSurvivesRoundtrip(new LobbyReadyUpdatePacket(true));
	}

	@DisplayName("RequestLobbyInfoPacket" + SURVIVE_ENCODE_DECODE)
	@Test
	public void requestLobbyInfo() {
		assertSurvivesRoundtrip(RequestLobbyInfoPacket.INSTANCE);
	}

	//Misc packets

	@Nested
	@DisplayName("ClientDisconnectingPacket" + SURVIVE_ENCODE_DECODE)
	class ClientDisconnecting {

		@DisplayName("when using string reason")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket("some reason"));
		}

		@DisplayName("when using null reason")
		@Test
		public void withNull() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket(null));
		}

		@DisplayName("when using an empty reason")
		@Test
		public void withEmptyReason() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket(""));
		}
	}

	@Nested
	@DisplayName("GameJoinPacket" + SURVIVE_ENCODE_DECODE)
	class GameJoin {

		@DisplayName("when using normal name")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(new GameJoinPacket("foobar"));
		}
	}

	@DisplayName("KickPlayerPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class KickPlayer extends ReasonTests {

		@Override
		protected Packet makePacket(String reason) {
			return new KickPlayerPacket("foobar", reason);
		}
	}

	// Server->Client packets

	//Game packets

	@DisplayName("DealNewCardsPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class DealNewCards {

		@DisplayName("with no cards")
		@Test
		public void noCards() {
			assertSurvivesRoundtrip(new DealNewCardsPacket(ImmutableList.of()));
		}

		@DisplayName("with cards")
		@Test
		public void withCards() {
			ImmutableList<CardType> cards = ImmutableList.of(Cards.ROTATE_LEFT, Cards.ROTATE_RIGHT, Cards.BACK_UP);
			assertSurvivesRoundtrip(new DealNewCardsPacket(cards));
		}
	}

	@DisplayName("RunRoundPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class RunRound {
		ImmutableList<CardType> player1Cards = ImmutableList.of(Cards.ROTATE_LEFT, Cards.ROTATE_RIGHT, Cards.BACK_UP);
		ImmutableList<CardType> player2Cards = ImmutableList.of(Cards.MOVE_ONE, Cards.ROTATE_LEFT, Cards.MOVE_ONE);

		@DisplayName("with one player")
		@Test
		public void onePlayer() {
			assertSurvivesRoundtrip(new RunRoundPacket(ImmutableMap.of("foo", player1Cards)));
		}

		@DisplayName("with two players")
		@Test
		public void twoPlayers() {
			assertSurvivesRoundtrip(new RunRoundPacket(ImmutableMap.of("foo", player1Cards, "bar", player2Cards)));
		}
	}

	@DisplayName("UpdatePlayerStatesPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class UpdatePlayerStates {
		UpdatePlayerStatesPacket.State player1 =
				new UpdatePlayerStatesPacket.State("foo", new Coordinate(0, 0), Orientation.EAST);
		UpdatePlayerStatesPacket.State player2 =
				new UpdatePlayerStatesPacket.State("bar", new Coordinate(0, 1), Orientation.WEST);

		@DisplayName("with one player")
		@Test
		public void onePlayer() {
			assertSurvivesRoundtrip(new UpdatePlayerStatesPacket(ImmutableList.of(player1)));
		}

		@DisplayName("with two players")
		@Test
		public void twoPlayers() {
			assertSurvivesRoundtrip(new UpdatePlayerStatesPacket(ImmutableList.of(player1, player2)));
		}
	}

	//Lobby packets

	@DisplayName("GameStartingPacket" + SURVIVE_ENCODE_DECODE)
	@Test
	public void gameStarting() {
		assertSurvivesRoundtrip(new GameStartingPacket(true));
	}

	@DisplayName("LobbyInfoPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class LobbyInfo {

		@DisplayName("with one player not ready")
		@Test
		public void onePlayer() {
			assertSurvivesRoundtrip(new LobbyInfoPacket(ImmutableMap.of("foo", false), "foo"));
		}

		@DisplayName("with one player ready")
		@Test
		public void onePlayerReady() {
			assertSurvivesRoundtrip(new LobbyInfoPacket(ImmutableMap.of("foo", true), "foo"));
		}

		@DisplayName("with two players, one ready")
		@Test
		public void twoPlayers() {
			assertSurvivesRoundtrip(new LobbyInfoPacket(ImmutableMap.of("foo", false, "bar", true), "foo"));
		}

		@DisplayName("with two players, both ready")
		@Test
		public void twoPlayersReady() {
			assertSurvivesRoundtrip(new LobbyInfoPacket(ImmutableMap.of("foo", true, "bar", true), "foo"));
		}
	}

	//Misc packets

	@Nested
	@DisplayName("GameInfoPacket" + SURVIVE_ENCODE_DECODE)
	class GameInfo {

		@DisplayName("when sending no players")
		@Test
		public void noPlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of()));
		}

		@DisplayName("when sending one player")
		@Test
		public void onePlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of("bar")));
		}

		@DisplayName("when sending two players")
		@Test
		public void twoPlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of("bar", "baz")));
		}
	}

	@Nested
	@DisplayName("GameJoinResultPacket" + SURVIVE_ENCODE_DECODE)
	class GameJoinResult {

		@ParameterizedTest(name = "using reason {argumentsWithNames}")
		@EnumSource
		public void withResults(JoinResult result) {
			assertSurvivesRoundtrip(new GameJoinResultPacket(result));
		}
	}

	@DisplayName("KickedPacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class Kicked extends ReasonTests {

		@Override
		protected Packet makePacket(String reason) {
			return new KickedPacket(reason);
		}
	}

	@DisplayName("OtherPlayerKickedReason" + SURVIVE_ENCODE_DECODE)
	@Nested
	class OtherPlayerKicked extends ReasonTests {

		@Override
		protected Packet makePacket(String reason) {
			return new OtherPlayerKickedPacket("foobar", reason);
		}
	}

	@DisplayName("PlayerJoinedGamePacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class PlayerJoinedGame {

		@DisplayName("when using normal name")
		@Test
		public void withName() {
			assertSurvivesRoundtrip(new PlayerJoinedGamePacket("foobar"));
		}
	}

	@DisplayName("PlayerLeftGamePacket" + SURVIVE_ENCODE_DECODE)
	@Nested
	class PlayerLeftGame extends ReasonTests {

		@Override
		protected Packet makePacket(String reason) {
			return new PlayerLeftGamePacket("foobar", reason);
		}
	}

	@Nested
	@DisplayName("ServerClosingPacket" + SURVIVE_ENCODE_DECODE)
	class ServerClosing extends ReasonTests {

		@Override
		protected Packet makePacket(String reason) {
			return new ServerClosingPacket(reason);
		}
	}

	@SuppressWarnings("unused")
	abstract class ReasonTests {

		protected abstract Packet makePacket(String reason);

		@DisplayName("when using string reason")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(makePacket("some reason"));
		}

		@DisplayName("when using an empty reason")
		@Test
		public void withEmptyReason() {
			assertSurvivesRoundtrip(makePacket(""));
		}

		@DisplayName("when using null reason")
		@Test
		public void withNull() {
			assertSurvivesRoundtrip(makePacket(null));
		}
	}
}
