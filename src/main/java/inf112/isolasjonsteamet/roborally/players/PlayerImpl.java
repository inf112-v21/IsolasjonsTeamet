package inf112.isolasjonsteamet.roborally.players;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;

public class PlayerImpl implements Player {

	private final Robot robot;
	private final String name;

	private final List<CardType> givenCards = new ArrayList<>();
	private final List<CardType> chosenCards = new ArrayList<>();

	public PlayerImpl(String name, ActionProcessor actionProcessor, Coordinate robotPos, Orientation robotDir) {
		this.name = name;
		this.robot = new RobotImpl(actionProcessor, robotPos, robotDir);

		for (int i = 0; i < 8; i++) {
			chosenCards.add(Cards.NO_CARD);
		}
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
	public void giveCards(List<CardType> cards) {
		givenCards.clear();
		givenCards.addAll(cards);
	}

	@Override
	public List<CardType> takeNonStuckCardsBack() {
		ImmutableList.Builder<CardType> builder = ImmutableList.builder();
		builder.addAll(givenCards);
		builder.addAll(chosenCards);

		return builder.build();
	}

	@Override
	public CardType getPickedCard(int cardNum) {
		if (chosenCards.size() > cardNum + 1) {
			return Cards.NO_CARD;
		}

		return chosenCards.get(cardNum);
	}
}
