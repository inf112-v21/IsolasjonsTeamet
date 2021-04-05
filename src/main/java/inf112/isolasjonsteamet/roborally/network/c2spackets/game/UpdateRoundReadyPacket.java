package inf112.isolasjonsteamet.roborally.network.c2spackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.buffer.ByteBuf;
import java.util.List;

public class UpdateRoundReadyPacket implements Client2ServerPacket {

	private final boolean isReady;
	private final List<CardType> cards;

	public UpdateRoundReadyPacket(boolean isReady, List<CardType> cards) {
		this.isReady = isReady;
		this.cards = ImmutableList.copyOf(cards);
	}

	public boolean isReady() {
		return isReady;
	}

	public List<CardType> getCards() {
		return cards;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UpdateRoundReadyPacket that = (UpdateRoundReadyPacket) o;
		return isReady == that.isReady && Objects.equal(cards, that.cards);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(isReady, cards);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("isReady", isReady)
				.add("cards", cards)
				.toString();
	}

	public enum PacketCodec implements Codec<UpdateRoundReadyPacket> {
		INSTANCE;

		@Override
		public UpdateRoundReadyPacket read(ByteBuf in) {
			boolean isReady = in.readBoolean();
			byte countChosenCards = in.readByte();

			var builder = ImmutableList.<CardType>builder();

			for (int i = 0; i < countChosenCards; i++) {
				builder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			return new UpdateRoundReadyPacket(isReady, builder.build());
		}

		@Override
		public void write(UpdateRoundReadyPacket msg, ByteBuf buf) {
			buf.writeBoolean(msg.isReady);

			buf.writeByte(msg.cards.size());
			for (CardType card : msg.cards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}
		}
	}
}
