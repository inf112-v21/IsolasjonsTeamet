package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import java.util.List;

/**
 * An active player in a game.
 */
public interface Player {

	/**
	 * The robot associated with this player.
	 */
	Robot getRobot();

	/**
	 * The name of the player.
	 */
	String getName();

	/**
	 * How many cards the player has stuck, and can as such not remove.
	 */
	int getStuckCardAmount();

	/**
	 * Give the player new cards.
	 */
	void giveCards(List<Card> cards);

	/**
	 * Take all the non stuck cards the player has back.
	 */
	List<Card> takeNonStuckCardsBack();

	/**
	 * Gets all the cards of the player in the specific row.
	 */
	List<Card> getCards(CardRow row);
}
