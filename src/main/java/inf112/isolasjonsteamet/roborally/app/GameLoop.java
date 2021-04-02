package inf112.isolasjonsteamet.roborally.app;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.Robot;
import java.util.List;

public abstract class GameLoop implements ActionProcessor {

	public void performActionNow(Robot robot, Action action) {
		action.perform(this, board(), robot);
		board().checkValid();
	}

	private void dealCards() {
		for (Player player : players()) {
			player.giveCards(deck().grabCards(8 - player.getStuckCardAmount()));
		}
	}

	private void takeCardsBack() {
		for (Player player : players()) {
			deck().discardCards(player.takeNonStuckCardsBack());
		}
	}

	private void processCards() {
		for (int i = 0; i < 8; i++) {
			for (Player player : players()) {
				processPlayerCard(player, i);
			}
		}
	}

	protected void processPlayerCard(Player player, int cardNum) {
		List<CardType> chosenCards = player.getCards(CardRow.CHOSEN);
		CardType card = Cards.NO_CARD;

		if (chosenCards.size() > cardNum) {
			card = chosenCards.get(cardNum);
		}

		for (Action action : card.getActions()) {
			performActionNow(player.getRobot(), action);
		}
	}

	public void prepareRound() {
		takeCardsBack();
		deck().shuffle();
		dealCards();
	}

	public void startRound() {
		processCards();
	}

	protected abstract List<Player> players();

	protected abstract CardDeck deck();

	protected abstract Board board();
}
