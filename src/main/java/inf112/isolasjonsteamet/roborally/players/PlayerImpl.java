package inf112.isolasjonsteamet.roborally.players;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerImpl implements Player {

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
		List<CardType> givenCards = this.cards.get(CardRow.GIVEN);

		givenCards.clear();
		givenCards.addAll(cards);
	}

	@Override
	public List<CardType> takeNonStuckCardsBack() {
		List<CardType> givenCards = this.cards.get(CardRow.GIVEN);
		List<CardType> chosenCards = this.cards.get(CardRow.CHOSEN);

		ImmutableList.Builder<CardType> builder = ImmutableList.builder();
		builder.addAll(givenCards);
		builder.addAll(chosenCards);
		givenCards.clear();
		chosenCards.clear();

		return builder.build();
	}

	@Override
	public List<CardType> getCards(CardRow row) {
		return ImmutableList.copyOf(cards.get(row));
	}
}
