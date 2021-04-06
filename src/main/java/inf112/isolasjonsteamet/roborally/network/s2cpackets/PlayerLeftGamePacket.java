package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

public class PlayerLeftGamePacket implements Server2ClientPacket {

	private final String playerName;
	private final String reason;

	public PlayerLeftGamePacket(String playerName, String reason) {
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
		PlayerLeftGamePacket that = (PlayerLeftGamePacket) o;
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

	public enum PacketCodec implements Codec<PlayerLeftGamePacket> {
		INSTANCE;

		@Override
		public PlayerLeftGamePacket read(ByteBuf in) {
			return new PlayerLeftGamePacket(ByteBufHelper.readString(in, 1), ByteBufHelper.readString(in));
		}

		@Override
		public void write(PlayerLeftGamePacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
