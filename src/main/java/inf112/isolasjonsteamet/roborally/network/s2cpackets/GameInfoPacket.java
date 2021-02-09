package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.impl.PacketProtocol;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

/**
 * Sent to a client whenever they connect to a server, or a new player joins the game.
 */
public class GameInfoPacket implements Server2ClientPacket {

	private final int protocol;
	private final String requiredVersion;
	private final String gameName;
	private final List<String> players;

	private GameInfoPacket(int protocol, String requiredVersion, String gameName, List<String> players) {
		this.protocol = protocol;
		this.requiredVersion = requiredVersion;
		this.gameName = gameName;
		this.players = ImmutableList.copyOf(players);
	}

	public static GameInfoPacket ofThisVersion(String gameName, List<String> players) {
		return new GameInfoPacket(PacketProtocol.PROTOCOL, PacketProtocol.REQUIRED_VERSION, gameName, players);
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

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<GameInfoPacket> {
		INSTANCE;

		@Override
		public GameInfoPacket read(ByteBuf in) {
			int protocol = in.readInt();
			String requiredVersion = ByteBufHelper.readString(in);
			String gameName = ByteBufHelper.readString(in);
			int playerCount = in.readUnsignedByte();
			var players = new ArrayList<String>(playerCount);
			for (int i = 0; i < playerCount; i++) {
				players.add(ByteBufHelper.readString(in, 1));
			}

			return new GameInfoPacket(protocol, requiredVersion, gameName, players);
		}

		@Override
		public void write(GameInfoPacket msg, ByteBuf buf) {
			buf.writeInt(msg.protocol);
			ByteBufHelper.writeString(msg.requiredVersion, buf);
			ByteBufHelper.writeString(msg.gameName, buf);
			ByteBufHelper.writeUnsignedByte((short) msg.players.size(), buf);
			msg.players.forEach(player -> ByteBufHelper.writeString(player, buf, 1));
		}
	}
}
