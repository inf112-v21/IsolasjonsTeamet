package inf112.isolasjonsteamet.roborally.players;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerImpl implements ClientPlayer, ServerPlayer {

	private final Robot robot;
	private final String name;

	protected final Map<CardRow, List<CardType>> cards = new HashMap<>();

	public PlayerImpl(String name, ActionProcessor actionProcessor, Coordinate robotPos, Orientation robotDir) {
		this.name = name;
		this.robot = new RobotImpl(actionProcessor, robotPos, robotDir);

		cards.put(CardRow.CHOSEN, new ArrayList<>());
		cards.put(CardRow.GIVEN, new ArrayList<>());
	}

	@Override
	public Robot getRobot() {
		return robot;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getStuckCardAmount() {
		return 0;
	}

	@Override
	public void giveCards(List<CardType> cards) {
		this.cards.put(CardRow.GIVEN, ImmutableList.copyOf(cards));
	}

	@Override
	public List<CardType> takeNonStuckCardsBack() {
		List<CardType> givenCards = this.cards.get(CardRow.GIVEN);
		List<CardType> chosenCards = this.cards.get(CardRow.CHOSEN);

		ImmutableList.Builder<CardType> builder = ImmutableList.builder();
		builder.addAll(givenCards);
		builder.addAll(chosenCards);
		this.cards.put(CardRow.GIVEN, ImmutableList.of());
		this.cards.put(CardRow.CHOSEN, ImmutableList.of());

		return builder.build();
	}

	@Override
	public List<CardType> getCards(CardRow row) {
		return ImmutableList.copyOf(cards.get(row));
	}

	@Override
	public void swapCards(CardRow fromRow, int fromCol, CardRow toRow, int toCol) {
		List<CardType> fromCards = new ArrayList<>(cards.get(fromRow));
		List<CardType> toCards = new ArrayList<>(cards.get(toRow));

		while (fromCards.size() <= fromCol) {
			fromCards.add(Cards.NO_CARD);
		}
		var from = fromCards.get(fromCol);

		while (toCards.size() <= toCol) {
			toCards.add(Cards.NO_CARD);
		}
		var to = toCards.get(toCol);

		toCards.set(toCol, from);
		fromCards.set(fromCol, to);

		this.cards.put(fromRow, ImmutableList.copyOf(fromCards));
		this.cards.put(toRow, ImmutableList.copyOf(toCards));
	}

	@Override
	public void setCards(CardRow row, List<CardType> cards) {
		this.cards.put(row, ImmutableList.copyOf(cards));
	}
}
