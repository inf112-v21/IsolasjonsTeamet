package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.List;

public class ClientPlayerImpl extends PlayerImpl implements ClientPlayer {

	public ClientPlayerImpl(String name, ActionProcessor actionProcessor, Coordinate robotPos, Orientation robotDir) {
		super(name, actionProcessor, robotPos, robotDir);
	}

	@Override
	public void swapCards(CardRow fromRow, int fromCol, CardRow toRow, int toCol) {
		List<CardType> fromCards = cards.get(fromRow);
		List<CardType> toCards = cards.get(toRow);

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
	}
}
