package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

/**
 * Sent to existing players of a game, when a new player joins the game.
 */
public class PlayerJoinedGamePacket implements Server2ClientPacket {

	private final String playerName;

	public PlayerJoinedGamePacket(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PlayerJoinedGamePacket that = (PlayerJoinedGamePacket) o;
		return Objects.equal(playerName, that.playerName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(playerName);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("playerName", playerName)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<PlayerJoinedGamePacket> {
		INSTANCE;

		@Override
		public PlayerJoinedGamePacket read(ByteBuf in) {
			return new PlayerJoinedGamePacket(ByteBufHelper.readString(in, 1));
		}

		@Override
		public void write(PlayerJoinedGamePacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
		}
	}
}
