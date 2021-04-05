package inf112.isolasjonsteamet.roborally.app;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.CardType;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStateDebugPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.players.ServerPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RoboRallyServer implements ActionProcessor {

	private final Server server;

	private final List<ServerStatePlayer> players;
	private final Map<String, ServerStatePlayer> playersAsMap;
	private final CardDeck deck;
	private final Board board;

	public RoboRallyServer(Server server, List<ServerPlayer> players, CardDeck deck, Board board) {
		this.server = server;

		var playersBuilder = ImmutableList.<ServerStatePlayer>builder();
		var playersMapBuilder = ImmutableMap.<String, ServerStatePlayer>builder();
		for (ServerPlayer player : players) {
			var serverPlayer = new ServerStatePlayer(player);
			playersBuilder.add(serverPlayer);
			playersMapBuilder.put(serverPlayer.getName(), serverPlayer);
		}

		this.players = playersBuilder.build();
		this.playersAsMap = playersMapBuilder.build();
		this.deck = deck;
		this.board = board;

		server.addListener(new ServerAdapter());
	}

	public void performActionNow(Robot robot, Action action) {
		action.perform(this, board, robot);
		board.checkValid();
	}

	private void dealCards() {
		for (ServerStatePlayer player : players) {
			var newCards = deck.grabCards(8 - player.getStuckCardAmount());
			player.giveCards(newCards);
			server.sendToPlayer(player.getName(), new DealNewCardsPacket(newCards));
		}
	}

	private void takeCardsBack() {
		for (ServerStatePlayer player : players) {
			deck.discardCards(player.takeNonStuckCardsBack());
		}
	}

	private void processCards() {
		var cards = new HashMap<String, List<CardType>>();

		for (int i = 0; i < 8; i++) {
			for (ServerStatePlayer player : players) {
				processPlayerCard(player, i);
				cards.put(player.getName(), player.getCards(CardRow.CHOSEN));
			}
		}

		server.sendToAllPlayers(new RunRoundPacket(cards));
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
		deck.shuffle();
		dealCards();

		for (ServerStatePlayer player : players) {
			player.isReady = false;
		}
	}

	public void startRound() {
		processCards();
	}

	private class ServerAdapter extends ServerPacketAdapter {

		@Override
		public void onUpdateRoundReady(@Nullable String player, UpdateRoundReadyPacket packet) {
			var updatedServerPlayer = playersAsMap.get(player);

			updatedServerPlayer.isReady = packet.isReady();
			updatedServerPlayer.setCards(CardRow.CHOSEN, packet.getChosenCards());
			updatedServerPlayer.setCards(CardRow.GIVEN, packet.getGivenCards());

			boolean allReady = true;
			for (ServerStatePlayer serverPlayer : players) {
				allReady = allReady && serverPlayer.isReady;
			}

			if (allReady) {
				startRound();
				prepareRound();
			}
		}

		@Override
		public void onUpdatePlayerState(@Nullable String player, UpdatePlayerStateDebugPacket packet) {
			Robot robot = playersAsMap.get(player).getRobot();
			robot.setPos(packet.getPosition());
			robot.setDir(packet.getRotation());
		}

		@Override
		public void handle(@Nullable String player, Client2ServerPacket packet) {
			if (player == null) {
				return;
			}

			super.handle(player, packet);
		}
	}

	private static class ServerStatePlayer implements ServerPlayer {

		private final ServerPlayer player;
		private boolean isReady = false;

		private ServerStatePlayer(ServerPlayer player) {
			this.player = player;
		}

		@Override
		public Robot getRobot() {
			return player.getRobot();
		}

		@Override
		public String getName() {
			return player.getName();
		}

		@Override
		public int getStuckCardAmount() {
			return player.getStuckCardAmount();
		}

		@Override
		public void giveCards(List<CardType> cards) {
			player.giveCards(cards);
		}

		@Override
		public List<CardType> takeNonStuckCardsBack() {
			return player.takeNonStuckCardsBack();
		}

		@Override
		public List<CardType> getCards(CardRow row) {
			return player.getCards(row);
		}

		@Override
		public void setCards(CardRow row, List<CardType> cards) {
			player.setCards(row, cards);
		}
	}
}