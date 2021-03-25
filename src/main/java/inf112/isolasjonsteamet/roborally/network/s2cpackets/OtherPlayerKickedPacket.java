package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

public class OtherPlayerKickedPacket implements Server2ClientPacket {

	private final String playerName;
	private final String reason;

	public OtherPlayerKickedPacket(String playerName, String reason) {
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
		OtherPlayerKickedPacket that = (OtherPlayerKickedPacket) o;
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

	public enum PacketCodec implements Codec<OtherPlayerKickedPacket> {
		INSTANCE;

		@Override
		public OtherPlayerKickedPacket read(ByteBuf in) {
			var playerName = ByteBufHelper.readString(in, 1);
			var reason = ByteBufHelper.readString(in);

			return new OtherPlayerKickedPacket(playerName, reason);
		}

		@Override
		public void write(OtherPlayerKickedPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
