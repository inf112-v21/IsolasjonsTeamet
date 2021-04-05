package inf112.isolasjonsteamet.roborally.network.s2cpackets.game;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.ByteBufHelper;
import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunRoundPacket implements Server2ClientPacket {

	private final Map<String, List<CardType>> playedCards;

	public RunRoundPacket(Map<String, List<CardType>> playedCards) {
		var builder = ImmutableMap.<String, List<CardType>>builder();
		playedCards.forEach((player, cards) -> builder.put(player, ImmutableList.copyOf(cards)));
		this.playedCards = builder.build();
	}

	public Map<String, List<CardType>> getPlayedCards() {
		return playedCards;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RunRoundPacket that = (RunRoundPacket) o;
		return Objects.equal(playedCards, that.playedCards);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(playedCards);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("playedCards", playedCards)
				.toString();
	}

	public enum PacketCodec implements Codec<RunRoundPacket> {
		INSTANCE;

		@Override
		public RunRoundPacket read(ByteBuf in) {
			var playerCount = in.readUnsignedByte();

			var acc = new HashMap<String, List<CardType>>(playerCount);
			for (int i = 0; i < playerCount; i++) {
				var player = ByteBufHelper.readString(in, 1);
				var size = in.readUnsignedByte();

				var builder = ImmutableList.<CardType>builder();

				for (int j = 0; j < size; j++) {
					builder.add(Cards.getCardFromRegistry(in.readUnsignedByte()));
				}

				acc.put(player, builder.build());
			}


			return new RunRoundPacket(acc);
		}

		@Override
		public void write(RunRoundPacket msg, ByteBuf buf) {
			ByteBufHelper.writeUnsignedByte((short) msg.playedCards.size(), buf);

			msg.playedCards.forEach((player, cards) -> {
				ByteBufHelper.writeString(player, buf, 1);

				ByteBufHelper.writeUnsignedByte((short) cards.size(), buf);
				for (CardType card : cards) {
					ByteBufHelper.writeUnsignedByte((short) Cards.getIdForCard(card), buf);
				}
			});
		}
	}
}
