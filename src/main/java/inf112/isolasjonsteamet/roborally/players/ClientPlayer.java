package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.CardRow;

public interface ClientPlayer extends Player {

	void swapCards(CardRow fromRow, int fromCol, CardRow toRow, int toCol);
}
