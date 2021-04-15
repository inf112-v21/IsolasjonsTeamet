package inf112.isolasjonsteamet.roborally.network.s2cpackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.buffer.ByteBuf;
import java.util.List;

/**
 * Sent to all players at the preparation phase of a new round. Contains all the cards the player can choose from this
 * round.
 */
public class DealNewCardsPacket implements Server2ClientPacket {

	private final List<Card> cards;

	public DealNewCardsPacket(List<Card> cards) {
		this.cards = ImmutableList.copyOf(cards);
	}

	public List<Card> getCards() {
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

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<DealNewCardsPacket> {
		INSTANCE;

		@Override
		public DealNewCardsPacket read(ByteBuf in) {
			var size = in.readUnsignedByte();
			var builder = ImmutableList.<Card>builder();

			for (int i = 0; i < size; i++) {
				builder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			return new DealNewCardsPacket(builder.build());
		}

		@Override
		public void write(DealNewCardsPacket msg, ByteBuf buf) {
			ByteBufHelper.writeUnsignedByte((short) msg.cards.size(), buf);
			for (Card card : msg.cards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}
		}
	}
}
