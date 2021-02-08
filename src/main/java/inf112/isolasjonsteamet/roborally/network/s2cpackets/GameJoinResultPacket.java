package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

public class GameJoinResultPacket implements Server2ClientPacket {

	private final JoinResult result;

	public GameJoinResultPacket(JoinResult result) {
		this.result = result;
	}

	public JoinResult getResult() {
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameJoinResultPacket that = (GameJoinResultPacket) o;
		return result == that.result;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(result);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("result", result)
				.toString();
	}

	public enum JoinResult {
		SUCCESS, DENIED, NAME_IN_USE
	}

	public enum PacketCodec implements Codec<GameJoinResultPacket> {
		INSTANCE;

		@Override
		public GameJoinResultPacket read(ByteBuf in) {
			return new GameJoinResultPacket(ByteBufHelper.readEnum(JoinResult.class, in));
		}

		@Override
		public void write(GameJoinResultPacket msg, ByteBuf buf) {
			ByteBufHelper.writeEnum(msg.result, buf);
		}
	}
}
