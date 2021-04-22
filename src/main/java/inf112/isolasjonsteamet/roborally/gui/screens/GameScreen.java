package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import inf112.isolasjonsteamet.roborally.app.RoboRallyGame;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;

/**
 * A screen which wraps the game itself.
 */
public class GameScreen implements Screen, DelegatingInputProcessor {

	private final RoboRallyGame roboRallyGame = new RoboRallyGame();

	public GameScreen() {
		roboRallyGame.create();
	}

	@Override
	public void render(float delta) {
		roboRallyGame.render();
	}

	@Override
	public void resize(int width, int height) {
		roboRallyGame.resize(width, height);
	}

	@Override
	public void pause() {
		roboRallyGame.pause();
	}

	@Override
	public void resume() {
		roboRallyGame.resume();
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		roboRallyGame.dispose();
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return roboRallyGame;
	}
}
