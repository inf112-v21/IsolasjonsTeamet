package inf112.isolasjonsteamet.roborally.network.s2cpackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;

public class DealNewCardsPacket {

	private final List<CardType> cards;

	public DealNewCardsPacket(List<CardType> cards) {
		this.cards = ImmutableList.copyOf(cards);
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
		DealNewCardsPacket that = (DealNewCardsPacket) o;
		return Objects.equal(cards, that.cards);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(cards);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("cards", cards)
				.toString();
	}

	public enum PacketCodec implements Codec<DealNewCardsPacket> {
		INSTANCE;

		@Override
		public DealNewCardsPacket read(ByteBuf in) {
			var size = in.readUnsignedByte();
			var builder = ImmutableList.<CardType>builder();

			for (int i = 0; i < size; i++) {
				builder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			return new DealNewCardsPacket(builder.build());
		}

		@Override
		public void write(DealNewCardsPacket msg, ByteBuf buf) {
			ByteBufHelper.writeUnsignedByte((short) msg.cards.size(), buf);
			for (CardType card : msg.cards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}
		}
	}
}
