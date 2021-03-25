package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

public class PlayerLeftGamePacket implements Server2ClientPacket {

	private final String playerName;

	public PlayerLeftGamePacket(String playerName) {
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
		PlayerLeftGamePacket that = (PlayerLeftGamePacket) o;
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

	public enum PacketCodec implements Codec<PlayerLeftGamePacket> {
		INSTANCE;

		@Override
		public PlayerLeftGamePacket read(ByteBuf in) {
			return new PlayerLeftGamePacket(ByteBufHelper.readString(in, 1));
		}

		@Override
		public void write(PlayerLeftGamePacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.playerName, buf, 1);
		}
	}
}
