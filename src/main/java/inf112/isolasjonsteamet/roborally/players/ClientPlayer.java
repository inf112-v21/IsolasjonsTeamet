package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.CardRow;

/**
 * A {@link Player} which exposes more operations for use on the client side.
 */
public interface ClientPlayer extends Player {

	/**
	 * Swaps the position of two cards.
	 */
	void swapCards(CardRow fromRow, int fromCol, CardRow toRow, int toCol);
}
