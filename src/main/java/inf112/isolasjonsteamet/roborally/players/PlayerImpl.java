package inf112.isolasjonsteamet.roborally.players;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple implementation of {@link Player}, {@link ClientPlayer} and {@link ServerPlayer} all in one.
 */
public class PlayerImpl implements ClientPlayer, ServerPlayer {

	private final Robot robot;
	private final String name;

	protected final Map<CardRow, List<Card>> cards = new HashMap<>();

	/**
	 * Creates a new player, with a name and position.
	 */
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
	public int getGivenCardsReduction() {
		return robot.getDamageTokens();
	}

	@Override
	public void giveCards(List<Card> cards) {
		this.cards.put(CardRow.GIVEN, ImmutableList.copyOf(cards));
	}

	@Override
	public List<Card> takeNonStuckCardsBack() {
		List<Card> givenCards = this.cards.get(CardRow.GIVEN);
		List<Card> chosenCards = this.cards.get(CardRow.CHOSEN);

		ImmutableList.Builder<Card> builder = ImmutableList.builder();

		var damage = robot.getDamageTokens();
		int lockedCards = damage - 4;
		ImmutableList.Builder<Card> newChosenCards = ImmutableList.builder();

		Iterator<Card> chosenIt = chosenCards.iterator();

		int i = 5;
		while (chosenIt.hasNext() && i > lockedCards) {
			builder.add(chosenIt.next());
			newChosenCards.add(Cards.NO_CARD);
			i--;
		}

		boolean hasAddedNewChosenCards = false;
		while (chosenIt.hasNext()) {
			Card card = chosenIt.next();
			if (!card.equals(Cards.NO_CARD)) {
				hasAddedNewChosenCards = true;
				newChosenCards.add(card);
			}
		}

		for (Card card : givenCards) {
			if (!card.equals(Cards.NO_CARD)) {
				builder.add(card);
			}
		}

		this.cards.put(CardRow.GIVEN, ImmutableList.of());
		if (hasAddedNewChosenCards) {
			this.cards.put(CardRow.CHOSEN, newChosenCards.build());
		} else {
			this.cards.put(CardRow.CHOSEN, ImmutableList.of());
		}

		return builder.build();
	}

	@Override
	public List<Card> getCards(CardRow row) {
		return ImmutableList.copyOf(cards.get(row));
	}

	@Override
	public void swapCards(CardRow fromRow, int fromCol, CardRow toRow, int toCol) {
		List<Card> fromCards = new ArrayList<>(cards.get(fromRow));
		List<Card> toCards = new ArrayList<>(cards.get(toRow));

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
	public void setCards(CardRow row, List<Card> cards) {
		this.cards.put(row, ImmutableList.copyOf(cards));
	}
}
