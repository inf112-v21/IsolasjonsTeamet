package inf112.isolasjonsteamet.roborally.cards;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CardDeckSpec {

	private final CardDeck deck =
			new DequeCardDeckImpl(
					ImmutableList.of(Cards.MOVE_ONE, Cards.MOVE_ONE, Cards.MOVE_TWO, Cards.MOVE_THREE, Cards.U_TURN),
					new Random(14) //Chosen randomly, by a set of dice
			);

	@DisplayName("Given a CardDeck with 5 cards")
	@Nested
	public class GivenCardDeck {

		@DisplayName("Grabbing 3 cards should")
		@Nested
		public class Grab3Cards {

			private final List<Card> grabbed = deck.grabCards(3);

			@DisplayName("Return a list of size 3")
			@Test
			public void grabCorrectSize() {
				assertEquals(3, grabbed.size());
			}

			@DisplayName("Return random cards each time the list is shuffled")
			@Test
			public void resetShuffles() {
				deck.reset();
				List<Card> newGrabbed = deck.grabCards(3);
				assertNotEquals(grabbed, newGrabbed);
			}
		}

		@DisplayName("Grabbing 6 cards should throw")
		@Test
		public void grab6Cards() {
			assertThrows(NoSuchElementException.class, () -> deck.grabCards(6));
		}

		@DisplayName("Grabbing 3 cards, followed by 3 more cards should throw")
		@Test
		public void grab3Then3Cards() {
			deck.grabCards(3);
			assertThrows(NoSuchElementException.class, () -> deck.grabCards(3));
		}

		@DisplayName("Grabbing 3 cards, resetting, then grabbing 3 more cards should not throw")
		@Test
		public void grab3ThenResetThen3Cards() {
			deck.grabCards(3);
			deck.reset();
			deck.grabCards(3);
		}

		@DisplayName("Grabbing 3 cards, discarding them, and grabbing 3 cards should throw")
		@Test
		public void grab3ThenDiscardThenGrab3() {
			var cards = deck.grabCards(3);
			deck.discardCards(cards);
			assertThrows(NoSuchElementException.class, () -> deck.grabCards(3));
		}

		@DisplayName("Grabbing 3 cards, discarding them, shuffling and then and grabbing 3 cards should")
		@Nested
		class Grab3DiscardShuffleGrab3 {

			@DisplayName("Not throw")
			@Test
			public void notThrow() {
				var cards = deck.grabCards(3);
				deck.discardCards(cards);
				deck.shuffle();
				deck.grabCards(3);
			}

			@DisplayName("Return different cards")
			@Test
			public void unique() {
				var oldCards = deck.grabCards(3);
				deck.discardCards(oldCards);
				deck.shuffle();
				var newCards = deck.grabCards(3);
				assertNotEquals(oldCards, newCards);
			}
		}

		@DisplayName("Grabbing 3 cards and discarding them, and then resetting, then grabbing 6 cards should throw")
		@Test
		public void grab3DiscardResetGrab6CardThrow() {
			var cards = deck.grabCards(3);
			deck.discardCards(cards);
			deck.reset();
			assertThrows(NoSuchElementException.class, () -> deck.grabCards(6));
		}
	}
}
