package inf112.isolasjonsteamet.roborally.network.c2spackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.buffer.ByteBuf;
import java.util.List;

/**
 * Updates the preparation step of a round for a player. Contains the chosen card for the player, and if it is ready or
 * not.
 */
public class UpdateRoundReadyPacket implements Client2ServerPacket {

	private final boolean isReady;
	private final List<Card> chosenCards;
	private final List<Card> givenCards;

	/**
	 * Constructs a new {@link UpdateRoundReadyPacket}. Copies the list passed in as immutable lists.
	 */
	public UpdateRoundReadyPacket(boolean isReady, List<Card> chosenCards, List<Card> givenCards) {
		this.isReady = isReady;
		this.chosenCards = ImmutableList.copyOf(chosenCards);
		this.givenCards = ImmutableList.copyOf(givenCards);
	}

	public boolean isReady() {
		return isReady;
	}

	public List<Card> getChosenCards() {
		return chosenCards;
	}

	public List<Card> getGivenCards() {
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
		//@formatter:off
		return isReady == that.isReady
			&& Objects.equal(chosenCards, that.chosenCards)
			&& Objects.equal(givenCards, that.givenCards);
		//@formatter:on
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

	@SuppressWarnings("checkstyle:MissingJavadocType")
	public enum PacketCodec implements Codec<UpdateRoundReadyPacket> {
		INSTANCE;

		@Override
		public UpdateRoundReadyPacket read(ByteBuf in) {
			boolean isReady = in.readBoolean();

			byte countChosenCards = in.readByte();
			var chosenBuilder = ImmutableList.<Card>builder();
			for (int i = 0; i < countChosenCards; i++) {
				chosenBuilder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			byte countGivenCards = in.readByte();
			var givenBuilder = ImmutableList.<Card>builder();
			for (int i = 0; i < countGivenCards; i++) {
				givenBuilder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
			}

			return new UpdateRoundReadyPacket(isReady, chosenBuilder.build(), givenBuilder.build());
		}

		@Override
		public void write(UpdateRoundReadyPacket msg, ByteBuf buf) {
			buf.writeBoolean(msg.isReady);

			buf.writeByte(msg.chosenCards.size());
			for (Card card : msg.chosenCards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}

			buf.writeByte(msg.givenCards.size());
			for (Card card : msg.givenCards) {
				ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
			}
		}
	}
}
