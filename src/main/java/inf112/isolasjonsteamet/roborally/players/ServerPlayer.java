package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import java.util.List;

/**
 * A {@link Player} which exposes more operations for use on the server side.
 */
public interface ServerPlayer extends Player {

	/**
	 * Sets all the cards on the given row.
	 */
	void setCards(CardRow row, List<Card> cards);
}
