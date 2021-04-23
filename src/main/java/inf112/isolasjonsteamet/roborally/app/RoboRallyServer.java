package inf112.isolasjonsteamet.roborally.app;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.Phase;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.CardDeck;
import inf112.isolasjonsteamet.roborally.cards.CardRow;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketAdapter;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientChatPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.KickPlayerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdatePlayerStatePacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.game.UpdateRoundReadyPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.PlayerLeftGamePacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerChatPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.DealNewCardsPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.RunRoundPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.game.UpdatePlayerStatesPacket;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.players.ServerPlayer;
import inf112.isolasjonsteamet.roborally.tiles.Tile;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The server for a RoboRally game. Handles passing communication between players, deals out cards, and runs the
 * rounds.
 */
public class RoboRallyServer extends RoboRallyShared {

	private final Server server;

	private final Map<String, ServerStatePlayer> players;
	private final String host;
	private final CardDeck deck;
	private final Board board;

	private boolean boardValidationChecks = true;

	private final Queue<Map.Entry<Action, Robot>> scheduledActions = new ArrayDeque<>();
	private Phase currentPhase;
	private boolean performingAction = false;

	/**
	 * Initializes a new server, and sends a state packet to all players when everything is ready. You still need to
	 * call {@link #prepareRound()} to kick the game off.
	 *
	 * @param server The server to use for sending packets.
	 * @param players Info about the players that will participate.
	 * @param host The player which has been designated host.
	 * @param deck The deck to use.
	 * @param board The board to use.
	 */
	public RoboRallyServer(Server server, List<PlayerInfo> players, String host, CardDeck deck, Board board) {
		this.server = server;
		this.host = host;

		//We need to keep the player order, so we use a tree map
		var playersMap = new TreeMap<String, ServerStatePlayer>();
		for (PlayerInfo playerInfo : players) {
			var player = new PlayerImpl(playerInfo.getName(), this, new Coordinate(0, 0), Orientation.NORTH);
			playersMap.put(player.getName(), new ServerStatePlayer(player));
		}
		board.updateActiveRobots(
				playersMap.values().stream().map(ServerStatePlayer::getRobot).collect(Collectors.toList())
		);

		this.players = playersMap;
		this.deck = deck;
		this.board = board;

		server.addListener(new ServerAdapter());
		server.sendToAllPlayers(UpdatePlayerStatesPacket.fromPlayers(ImmutableList.copyOf(playersMap.values())));
	}

	public void dispose() {
		server.close("Disposing");
	}

	/**
	 * {@inheritDoc}.
	 */
	public void performActionNow(Robot robot, Action action, Phase phase) {
		boolean hasWork;
		do {
			action.initialize(board, robot);

			performingAction = true;
			action.perform(this, board, robot, phase);
			performingAction = false;

			if (boardValidationChecks) {
				board.checkValid();
			}

			Map.Entry<Action, Robot> nextActionEntry = scheduledActions.poll();
			hasWork = nextActionEntry != null;
			if (nextActionEntry != null) {
				robot = nextActionEntry.getValue();
				action = nextActionEntry.getKey();
			}
		} while (hasWork);
	}

	@Override
	public void scheduleAction(Robot robot, Action action) {
		if (scheduledActions.isEmpty() && !performingAction) {
			performActionNow(robot, action, currentPhase);
		} else {
			scheduledActions.add(Map.entry(action, robot));
		}
	}

	private void dealCards() {
		for (ServerStatePlayer player : players.values()) {
			var newCards = deck.grabCards(CardRow.GIVEN.getSize() - player.getGivenCardsReduction());
			player.giveCards(newCards);
			server.sendToPlayer(player.getName(), new DealNewCardsPacket(newCards));
		}
	}

	private void takeCardsBack() {
		for (ServerStatePlayer player : players.values()) {
			deck.discardCards(player.takeNonStuckCardsBack());
		}
	}

	/**
	 * Prepares a new round for all the players. Takes back used cards, shuffles the deck, and deals out new cards.
	 */
	public void prepareRound() {
		takeCardsBack();
		deck.shuffle();
		dealCards();

		for (ServerStatePlayer player : players.values()) {
			player.isReady = false;
		}
	}

	private void sendRoundPacket() {
		var cards = new HashMap<String, List<Card>>();

		for (int i = 0; i < 8; i++) {
			for (ServerStatePlayer player : players.values()) {
				cards.put(player.getName(), player.getCards(CardRow.CHOSEN));
			}
		}

		server.sendToAllPlayers(new RunRoundPacket(cards));
	}

	protected void processPlayerCard(Player player, int cardNum) {
		List<Card> chosenCards = player.getCards(CardRow.CHOSEN);
		Card card = Cards.NO_CARD;

		if (chosenCards.size() > cardNum) {
			card = chosenCards.get(cardNum);
		}

		for (Action action : card.getActions()) {
			performActionNow(player.getRobot(), action, Phase.CARDS);
		}
	}

	@Override
	protected void foreachPlayerTile(BiConsumer<Player, Tile> handler) {
		for (ServerStatePlayer player : players.values()) {
			for (Tile tile : board.getTilesAt(player.getRobot().getPos())) {
				handler.accept(player, tile);
			}
		}
	}

	@Override
	protected Board board() {
		return board;
	}

	@Override
	protected void setCurrentPhase(Phase phase) {
		currentPhase = phase;
	}

	@Override
	protected void skipBoardValidChecks() {
		boardValidationChecks = false;
	}

	@Override
	protected void enableBoardValidChecks() {
		boardValidationChecks = true;
	}

	/**
	 * Starts a round with the cards all players have chosen.
	 */
	public void startRound() {
		sendRoundPacket();

		for (int i = 0; i < 5; i++) {
			currentPhase = Phase.CARDS;
			for (ServerStatePlayer player : players.values()) {
				processPlayerCard(player, i);
			}

			processBoardElements();
			fireLasers();
			processCheckpoints();
		}

		processCleanup();
	}

	@SuppressWarnings("ConstantConditions")
	private class ServerAdapter extends ServerPacketAdapter {

		@Override
		public void onUpdateRoundReady(@Nullable String player, UpdateRoundReadyPacket packet) {
			var updatedServerPlayer = players.get(player);

			updatedServerPlayer.isReady = packet.isReady();
			updatedServerPlayer.setCards(CardRow.CHOSEN, packet.getChosenCards());
			updatedServerPlayer.setCards(CardRow.GIVEN, packet.getGivenCards());

			boolean allReady = true;
			for (ServerStatePlayer serverPlayer : players.values()) {
				allReady = allReady && serverPlayer.isReady;
			}

			if (allReady) {
				startRound();
				prepareRound();
			}
		}

		@Override
		public void onUpdatePlayerState(String player, UpdatePlayerStatePacket packet) {
			Robot robot = players.get(player).getRobot();
			robot.setPos(packet.getPosition());
			robot.setDir(packet.getRotation());

			server.sendToAllPlayers(UpdatePlayerStatesPacket.fromPlayers(ImmutableList.copyOf(players.values())));
		}

		@Override
		public void onClientChat(String player, ClientChatPacket packet) {
			server.sendToAllPlayers(new ServerChatPacket(player, packet.getMessage()));
		}

		@Override
		public void onClientDisconnecting(String player, ClientDisconnectingPacket packet) {
			if (player.equals(host)) {
				server.close("Host left");
			} else {
				players.remove(player);
				server.sendToAllPlayers(new PlayerLeftGamePacket(player, packet.getReason()));
			}
		}

		@Override
		public void onKickPlayer(String player, KickPlayerPacket packet) {
			if (player.equals(host)) {
				server.kickPlayer(packet.getPlayerName(), packet.getReason());
			}
		}

		@Override
		public void handle(String player, Client2ServerPacket packet) {
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
		public int getGivenCardsReduction() {
			return player.getGivenCardsReduction();
		}

		@Override
		public void giveCards(List<Card> cards) {
			player.giveCards(cards);
		}

		@Override
		public List<Card> takeNonStuckCardsBack() {
			return player.takeNonStuckCardsBack();
		}

		@Override
		public List<Card> getCards(CardRow row) {
			return player.getCards(row);
		}

		@Override
		public void setCards(CardRow row, List<Card> cards) {
			player.setCards(row, cards);
		}
	}
}
