package inf112.isolasjonsteamet.roborally.network.s2cpackets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;

/**
 * Sent to a player when they are kicked. Might also contain a reason specifying why they were kicked.
 */
public class KickedPacket implements Server2ClientPacket {

	private final String reason;

	public KickedPacket(String reason) {
		this.reason = reason;
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
		KickedPacket that = (KickedPacket) o;
		return Objects.equal(reason, that.reason);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(reason);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("reason", reason)
				.toString();
	}

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<KickedPacket> {
		INSTANCE;

		@Override
		public KickedPacket read(ByteBuf in) {
			return new KickedPacket(ByteBufHelper.readString(in));
		}

		@Override
		public void write(KickedPacket msg, ByteBuf buf) {
			ByteBufHelper.writeString(msg.reason, buf);
		}
	}
}
