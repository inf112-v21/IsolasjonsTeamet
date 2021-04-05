package inf112.isolasjonsteamet.roborally.players;

import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import java.util.List;

public interface ServerPlayer extends Player {

	void setCards(CardRow row, List<CardType> cards);
}
