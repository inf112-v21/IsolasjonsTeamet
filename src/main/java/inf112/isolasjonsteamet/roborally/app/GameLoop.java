package inf112.isolasjonsteamet.roborally.app;

import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
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
			player.giveCards(deck().grabCards(9));
		}
	}

	private void takeCardsPack() {
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
		for (Action action : player.getPickedCard(cardNum).getActions()) {
			performActionNow(player.getRobot(), action);
		}
	}

	public void prepareRound() {
		takeCardsPack();
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
