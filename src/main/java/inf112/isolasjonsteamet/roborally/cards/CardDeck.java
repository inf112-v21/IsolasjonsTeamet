package inf112.isolasjonsteamet.roborally.cards;

import java.util.List;

/**
 * A deck of cards containing potentially multiple instances of different card types.
 */
public interface CardDeck {

	/**
	 * Grabs the given amount of cards from the card deck.
	 *
	 * @param amount The amount of cards to grab.
	 * @return A list with the grabbed cards, with size equal to the amount requested.
	 * @throws java.util.NoSuchElementException If the desired amount of cards to return is greater than the remaining
	 * 		cards in the deck.
	 */
	List<CardType> grabCards(int amount);

	/**
	 * Adds the given cards to the discard pile, such that when the deck is shuffled next time, they will be a part of
	 * it.
	 */
	void discardCards(List<CardType> cards);

	/**
	 * Adds all cards in the discard pile back to the main deck, and shuffles it.
	 */
	void shuffle();

	/**
	 * Resets this card deck to it's initial state, returning all the cards that have been dealt, and shuffles it. The
	 * discard pile is also cleared.
	 */
	void reset();
}
