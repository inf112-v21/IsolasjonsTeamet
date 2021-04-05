package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.actions.Action;
import inf112.isolasjonsteamet.roborally.actions.ActionProcessor;
import inf112.isolasjonsteamet.roborally.app.RoboRallyClient;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A screen which wraps the game itself.
 */
public class GameScreen implements Screen, DelegatingInputProcessor {

	private final List<RoboRallyClient> games = new ArrayList<>();
	private RoboRallyClient activeGame;

	public GameScreen(String boardFileName, int localPlayers) {
		var playersBuilder = ImmutableList.<Player>builder();
		//playersBuilder.addAll(remotePlayers);
		var localPlayersOnly = new ArrayList<PlayerImpl>();

		// We need to give an action processor to the player when we construct it,
		// but we need to game for that
		// We solve the recursive dependency problem with a late binding
		class LateConstructedActionProcessor implements ActionProcessor {

			ActionProcessor delegateTo;

			@Override
			public void performActionNow(Robot robot, Action action) {
				if (delegateTo != null) {
					delegateTo.performActionNow(robot, action);
				}
			}
		}
		var lateActionProcessors = new ArrayList<LateConstructedActionProcessor>();

		for (int i = 0; i < localPlayers; i++) {
			var actionProcessor = new LateConstructedActionProcessor();
			lateActionProcessors.add(actionProcessor);
			var player = new PlayerImpl("Player" + i, actionProcessor, new Coordinate(0, 0), Orientation.NORTH);
			playersBuilder.add(player);
			localPlayersOnly.add(player);
		}
		var players = playersBuilder.build();

		for (int i = 0; i < localPlayers; i++) {
			var board = new BoardClientImpl(
					players.stream().map(Player::getRobot).collect(Collectors.toList()), boardFileName
			);
			var game = new RoboRallyClient(board, localPlayersOnly.get(i));
			lateActionProcessors.get(i).delegateTo = game;

			game.create();
			games.add(game);
		}
		activeGame = games.get(0);
	}

	@SuppressWarnings("CheckStyle")
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
		System.out.println("Switching to player " + playerNum);

		if (playerNum > games.size()) {
			throw new IllegalStateException("Not enough player slots to add player " + playerNum);
		}

		activeGame = games.get(playerNum - 1);
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
		activeGame.dispose();
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return activeGame;
	}
}
