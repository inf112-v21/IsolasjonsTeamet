package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlayerSpec {

	Player player = new PlayerImpl("foo", null, new Coordinate(0, 0), Orientation.NORTH);

	@DisplayName("Given a player, giving a player cards should make those cards show up in the given row")
	@Test
	public void giveCards() {
		var cards = ImmutableList.of(Cards.MOVE_ONE, Cards.ROTATE_RIGHT, Cards.MOVE_THREE);
		player.giveCards(cards);
		assertEquals(cards, player.getCards(CardRow.GIVEN));
	}

	@DisplayName("Given a player, giving a player cards should overwrite their existing cards")
	@Test
	public void giveCardsOverwrites() {
		var cards1 = ImmutableList.of(Cards.MOVE_ONE, Cards.ROTATE_RIGHT, Cards.MOVE_THREE);
		var cards2 = ImmutableList.of(Cards.ROTATE_RIGHT, Cards.U_TURN, Cards.MOVE_THREE);
		player.giveCards(cards1);
		player.giveCards(cards2);
		assertEquals(cards2, player.getCards(CardRow.GIVEN));
	}

}
