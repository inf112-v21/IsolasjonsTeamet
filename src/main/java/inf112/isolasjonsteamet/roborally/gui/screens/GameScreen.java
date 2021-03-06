package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.app.RoboRallyClient;
import inf112.isolasjonsteamet.roborally.app.RoboRallyServer;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.cards.Card;
import inf112.isolasjonsteamet.roborally.cards.Cards;
import inf112.isolasjonsteamet.roborally.cards.DequeCardDeckImpl;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A screen which wraps the game itself.
 */
public class GameScreen implements Screen, DelegatingInputProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameScreen.class);

	private final List<RoboRallyClient> games = new ArrayList<>();
	private RoboRallyClient activeGame;
	private RoboRallyServer gameServer = null;

	private final ScreenController screenController;

	/**
	 * Creates and starts a game for the player. If this player is also the host, then it also starts the server.
	 *
	 * @param boardFileName The name of the board to play.
	 * @param playerInfos Info about the players that will participate.
	 * @param host The name of the designated host.
	 * @param server The server, if this player is also the server.
	 */
	public GameScreen(
			String boardFileName,
			List<PlayerInfo> playerInfos,
			String host,
			ScreenController screenController,
			@Nullable Server server
	) {
		this.screenController = screenController;

		for (PlayerInfo playerInfo : playerInfos) {
			if (!playerInfo.isLocallyManuallyControlled()) {
				games.add(null);
				// Not aware of any cases where a non-locally player-controlled player
				// can have a client associated with it, but if that's something we use later,
				// we should handle it here
				continue;
			}

			var board = new BoardClientImpl(boardFileName);
			var game = new RoboRallyClient(
					playerInfo.getClient(), board, playerInfo.getName(), host, screenController, this
			);

			game.create();
			games.add(game);

			if (activeGame == null) {
				activeGame = game;
			}
		}

		if (server != null) {
			var allCards = ImmutableList.of(
					Cards.MOVE_ONE, Cards.MOVE_ONE, Cards.MOVE_TWO, Cards.MOVE_THREE, Cards.BACK_UP, Cards.ROTATE_LEFT,
					Cards.ROTATE_RIGHT, Cards.U_TURN
			);

			var allCardsRepeated = ImmutableList.<Card>builder();
			for (int i = 0; i < 10; i++) {
				allCardsRepeated.addAll(allCards);
			}
			var deck = new DequeCardDeckImpl(allCardsRepeated.build());

			var board = new BoardClientImpl(boardFileName);
			gameServer = new RoboRallyServer(server, playerInfos, host, deck, board);
			gameServer.prepareRound();
		}

		if (activeGame == null) {
			throw new IllegalStateException("Game started with no locally controlled players");
		}
	}

	@SuppressWarnings("checkstyle:Indentation")
	@Override
	public boolean keyDown(int keycode) {
		int playerToSwitchTo = switch (keycode) {
			case Keys.F1 -> 1;
			case Keys.F2 -> 2;
			case Keys.F3 -> 3;
			case Keys.F4 -> 4;
			case Keys.F5 -> 5;
			case Keys.F6 -> 6;
			case Keys.F7 -> 7;
			case Keys.F8 -> 8;
			case Keys.F9 -> 9;
			default -> -1;
		};

		if (playerToSwitchTo != -1) {
			switchToPlayer(playerToSwitchTo);
		}

		boolean handled = playerToSwitchTo != -1;
		return handled || activeGame.keyDown(keycode);
	}

	private void switchToPlayer(int playerNum) {
		if (playerNum > games.size()) {
			return;
		}

		LOGGER.info("Switching to player " + playerNum);
		var triedNewActiveGame = games.get(playerNum - 1);
		if (triedNewActiveGame != null) {
			activeGame = triedNewActiveGame;
		}
	}

	/**
	 * Removes a client from the player-controlled games being managed here. If there are no valid games left, this
	 * screen closes itself
	 *
	 * @param client The client to remove.
	 * @param kickedReason If the client is being removed because they were kicked, then the kick reason. Otherwise
	 * 		null.
	 */
	public void removeClient(RoboRallyClient client, @Nullable String kickedReason) {
		int idx = games.indexOf(client);
		if (idx != -1) {
			games.set(idx, null);
			client.dispose();
		}

		boolean replaceActiveGame = activeGame.equals(client);

		boolean hasActiveGame = false;
		for (RoboRallyClient game : games) {
			if (game != null) {
				if (replaceActiveGame) {
					activeGame = game;
				}

				hasActiveGame = true;
				break;
			}
		}

		if (!hasActiveGame) {
			screenController.returnToMainMenu();

			if (kickedReason != null) {
				screenController.pushInputScreen(new NotificationScreen(screenController, "Kicked: " + kickedReason));
			}
		}
	}

	@Override
	public void render(float delta) {
		activeGame.render();
	}

	@Override
	public void resize(int width, int height) {
		activeGame.resize(width, height);
	}

	@Override
	public void pause() {
		activeGame.pause();
	}

	@Override
	public void resume() {
		activeGame.resume();
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		for (RoboRallyClient game : games) {
			if (game != null) {
				game.dispose();
			}
		}

		if (gameServer != null) {
			gameServer.dispose();
		}
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return activeGame;
	}
}
