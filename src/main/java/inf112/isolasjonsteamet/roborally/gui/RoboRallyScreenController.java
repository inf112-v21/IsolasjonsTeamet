package inf112.isolasjonsteamet.roborally.gui;

import static com.google.common.base.Preconditions.checkState;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import inf112.isolasjonsteamet.roborally.gui.screens.GameScreen;
import inf112.isolasjonsteamet.roborally.gui.screens.MainMenuScreen;
import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main controller of the game logic. Routes the active screen around using a stack.
 */
public class RoboRallyScreenController extends Game implements DelegatingInputProcessor, ScreenController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoboRallyScreenController.class);

	private final MainMenuScreen mainMenuScreen = new MainMenuScreen(this);

	private final Deque<Screen> screenStack = new ArrayDeque<>();
	private final Deque<InputProcessor> inputProcessorStack = new ArrayDeque<>();

	@Override
	public void create() {
		mainMenuScreen.create();

		screenStack.push(mainMenuScreen);
		inputProcessorStack.push(mainMenuScreen);
		super.setScreen(mainMenuScreen);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		for (Screen screen : screenStack) {
			screen.hide();
			screen.dispose();
		}
	}

	@Override
	public void returnToMainMenu() {
		while (true) {
			if (!popInputScreen()) {
				return;
			}
		}
	}

	@Override
	public void startGame(String boardFileName, List<PlayerInfo> players, String host, @Nullable Server server) {
		pushInputScreen(new GameScreen(boardFileName, players, host, this, server));
	}

	@Override
	public <T extends Screen & InputProcessor> void setInputScreen(T inputScreen) {
		popInputScreen();
		pushInputScreen(inputScreen);
	}

	@Override
	public <T extends Screen & InputProcessor> void pushInputScreen(T inputScreen) {
		LOGGER.debug("Pushing screen " + inputScreen);
		screenStack.push(inputScreen);
		inputProcessorStack.push(inputScreen);
		super.setScreen(inputScreen);
	}

	@Override
	public boolean popInputScreen() {
		checkState(screenStack.size() > 1, "Can't pop main menu screen");
		LOGGER.debug("Popping screen");

		screenStack.pop().hide();
		inputProcessorStack.pop();
		super.setScreen(screenStack.peek());

		return screenStack.size() > 1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link #setInputScreen(Screen)} or {@link #pushInputScreen(Screen)} instead.
	 */
	@Deprecated
	@Override
	public void setScreen(Screen screen) {
		LOGGER.warn("Using setScreen directly");
		super.setScreen(screen);
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return inputProcessorStack.peek();
	}
}
