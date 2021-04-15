package inf112.isolasjonsteamet.roborally.network.c2spackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.OtherPlayerKickedPacket;
import io.netty.buffer.ByteBuf;

/**
 * A packet sent from the host to the server, indicating that the player with the given name should be kicked. If the
 * kick goes through, also the host will receive a {@link OtherPlayerKickedPacket}.
 */
public class KickPlayerPacket implements Client2ServerPacket {

	private final String playerName;
	private final String reason;

	public KickPlayerPacket(String playerName, String reason) {
		this.playerName = playerName;
		this.reason = reason;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		KickPlayerPacket that = (KickPlayerPacket) o;
		return Objects.equal(playerName, that.playerName) && Objects.equal(reason, that.reason);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(playerName, reason);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("playerName", playerName)
				.add("reason", reason)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<KickPlayerPacket> {
		INSTANCE;

		@Override
		public KickPlayerPacket read(ByteBuf in) {
			var playerName = ByteBufHelper.readString(in, 1);
			var reason = ByteBufHelper.readString(in);

			return new KickPlayerPacket(playerName, reason);
		}

		@Override
		public void write(KickPlayerPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
