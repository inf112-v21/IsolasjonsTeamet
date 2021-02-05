package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;

public class GameInfoPacket implements Server2ClientPacket {

	private final int protocol;
	private final String requiredVersion;
	private final String gameName;
	private final List<String> players;

	public GameInfoPacket(int protocol, String requiredVersion, String gameName, List<String> players) {
		this.protocol = protocol;
		this.requiredVersion = requiredVersion;
		this.gameName = gameName;
		this.players = players;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getRequiredVersion() {
		return requiredVersion;
	}

	public String getGameName() {
		return gameName;
	}

	public List<String> getPlayers() {
		return players;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameInfoPacket that = (GameInfoPacket) o;
		return protocol == that.protocol
				&& Objects.equal(requiredVersion, that.requiredVersion)
				&& Objects.equal(gameName, that.gameName)
				&& Objects.equal(players, that.players);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(protocol, requiredVersion, gameName, players);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("protocol", protocol)
				.add("requiredVersion", requiredVersion)
				.add("gameName", gameName)
				.add("players", players)
				.toString();
	}

	public static class PacketCodec implements Codec<GameInfoPacket> {

		@Override
		public GameInfoPacket read(ByteBuf in) {
			return null;
		}

		@Override
		public void write(GameInfoPacket msg, ByteBuf buf) {

		}
	}
}
