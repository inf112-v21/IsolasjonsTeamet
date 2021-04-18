package inf112.isolasjonsteamet.roborally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlayerSpec {

	PlayerImpl player = new PlayerImpl("foo", null, new Coordinate(0, 0), Orientation.NORTH);

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

	@DisplayName("Given a player, take back non stuck cards should clear all non stuck cards")
	@Test
	public void takeNonStuckClears() {
		var cards1 = ImmutableList.of(Cards.MOVE_ONE, Cards.ROTATE_RIGHT, Cards.MOVE_THREE);
		var cards2 = ImmutableList.of(Cards.ROTATE_RIGHT, Cards.U_TURN, Cards.MOVE_THREE);
		player.setCards(CardRow.CHOSEN, cards1);
		player.setCards(CardRow.GIVEN, cards2);

		player.takeNonStuckCardsBack();
		assertTrue(player.getCards(CardRow.CHOSEN).isEmpty());
		assertTrue(player.getCards(CardRow.GIVEN).isEmpty());
	}

	@DisplayName("Given a player, taking back cards should not include NO_CARD")
	@Test
	public void noNoCard() {
		for (int i = 0; i < 5; i++) {
			player.getRobot().damageRobot();
		}

		var chosenCards = ImmutableList.of(
				Cards.MOVE_ONE, Cards.ROTATE_RIGHT, Cards.NO_CARD, Cards.BACK_UP, Cards.MOVE_TWO
		);
		player.setCards(CardRow.CHOSEN, chosenCards);

		var givenBackCards = player.takeNonStuckCardsBack();
		assertEquals(4, givenBackCards.size());
	}

	@DisplayName("Given a player, with a robot damaged 5 times, taking back the cards should leave 1 card as stuck")
	@Test
	public void damageStuckCards() {
		for (int i = 0; i < 5; i++) {
			player.getRobot().damageRobot();
		}

		var cards = ImmutableList.of(
				Cards.MOVE_ONE, Cards.ROTATE_RIGHT, Cards.MOVE_THREE, Cards.BACK_UP, Cards.MOVE_TWO
		);
		player.setCards(CardRow.CHOSEN, cards);

		player.takeNonStuckCardsBack();

		assertEquals(player.getCards(CardRow.CHOSEN).get(4), Cards.MOVE_TWO);
	}

}
