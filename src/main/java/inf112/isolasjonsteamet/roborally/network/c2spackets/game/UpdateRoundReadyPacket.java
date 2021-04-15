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
	private final List<CardType> chosenCards;
	private final List<CardType> givenCards;

	public UpdateRoundReadyPacket(boolean isReady, List<CardType> chosenCards, List<CardType> givenCards) {
		this.isReady = isReady;
		this.chosenCards = ImmutableList.copyOf(chosenCards);
		this.givenCards = ImmutableList.copyOf(givenCards);
	}

	public boolean isReady() {
		return isReady;
	}

	public List<CardType> getChosenCards() {
		return chosenCards;
	}

	public List<CardType> getGivenCards() {
		return givenCards;
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
		return isReady == that.isReady && Objects.equal(chosenCards, that.chosenCards) && Objects.equal(givenCards, that.givenCards);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(isReady, chosenCards, givenCards);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("isReady", isReady)
				.add("chosenCards", chosenCards)
				.add("givenCards", givenCards)
				.toString();
	}

	public enum PacketCodec implements Codec<UpdateRoundReadyPacket> {
		INSTANCE;

		@Override
		public UpdateRoundReadyPacket read(ByteBuf in) {
			boolean isReady = in.readBoolean();

			byte countChosenCards = in.readByte();
			var chosenBuilder = ImmutableList.<CardType>builder();
			for (int i = 0; i < countChosenCards; i++) {
				chosenBuilder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			byte countGivenCards = in.readByte();
			var givenBuilder = ImmutableList.<CardType>builder();
			for (int i = 0; i < countGivenCards; i++) {
				givenBuilder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}


			return new UpdateRoundReadyPacket(isReady, chosenBuilder.build(), givenBuilder.build());
		}

		@Override
		public void write(UpdateRoundReadyPacket msg, ByteBuf buf) {
			buf.writeBoolean(msg.isReady);

			buf.writeByte(msg.chosenCards.size());
			for (CardType card : msg.chosenCards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}

			buf.writeByte(msg.givenCards.size());
			for (CardType card : msg.givenCards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}
		}
	}
}
