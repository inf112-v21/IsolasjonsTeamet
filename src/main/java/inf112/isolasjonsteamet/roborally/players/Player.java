package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.CardType;
import java.util.List;

public interface Player {

	Robot getRobot();

	String getName();

	void giveCards(List<CardType> cards);

	List<CardType> takeNonStuckCardsBack();

	CardType getPickedCard(int cardNum);
}
