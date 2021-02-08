package inf112.isolasjonsteamet.roborally.network;

import static org.junit.jupiter.api.Assertions.*;

import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket.JoinResult;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
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

	@Nested
	@DisplayName("ClientDisconnectingPacket" + SURVIVE_ENCODE_DECODE)
	class ClientDisconnecting {

		@DisplayName("when using string reason")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket("some reason"), ClientDisconnectingPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when using null reason")
		@Test
		public void withNull() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket(null), ClientDisconnectingPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when using an empty reason")
		@Test
		public void withEmptyReason() {
			assertSurvivesRoundtrip(new ClientDisconnectingPacket(""), ClientDisconnectingPacket.PacketCodec.INSTANCE);
		}
	}

	@Nested
	@DisplayName("GameJoinPacket" + SURVIVE_ENCODE_DECODE)
	class GameJoin {

		@DisplayName("when using string reason")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(new GameJoinPacket("some reason"), GameJoinPacket.PacketCodec.INSTANCE);
		}
	}

	@Nested
	@DisplayName("GameInfoPacket" + SURVIVE_ENCODE_DECODE)
	class GameInfo {

		@DisplayName("when sending no players")
		@Test
		public void noPlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of()), GameInfoPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when sending one player")
		@Test
		public void onePlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of("bar")), GameInfoPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when sending two players")
		@Test
		public void twoPlayers() {
			assertSurvivesRoundtrip(GameInfoPacket.ofThisVersion("foo", List.of("bar", "baz")), GameInfoPacket.PacketCodec.INSTANCE);
		}
	}

	@Nested
	@DisplayName("GameJoinResultPacket" + SURVIVE_ENCODE_DECODE)
	class GameJoinResult {

		@ParameterizedTest(name = "using reason {argumentsWithNames}")
		@EnumSource
		public void withResults(JoinResult result) {
			assertSurvivesRoundtrip(new GameJoinResultPacket(result), GameJoinResultPacket.PacketCodec.INSTANCE);
		}
	}

	@Nested
	@DisplayName("ServerClosingPacket" + SURVIVE_ENCODE_DECODE)
	class ServerClosing {

		@DisplayName("when using string reason")
		@Test
		public void withReason() {
			assertSurvivesRoundtrip(new ServerClosingPacket("some reason"), ServerClosingPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when using null reason")
		@Test
		public void withNull() {
			assertSurvivesRoundtrip(new ServerClosingPacket(null), ServerClosingPacket.PacketCodec.INSTANCE);
		}

		@DisplayName("when using an empty reason")
		@Test
		public void withEmptyReason() {
			assertSurvivesRoundtrip(new ServerClosingPacket(""), ServerClosingPacket.PacketCodec.INSTANCE);
		}
	}
}
