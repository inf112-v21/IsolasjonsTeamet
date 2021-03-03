package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import inf112.isolasjonsteamet.roborally.app.HelloWorld;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;

/**
 * A screen which wraps the game itself.
 */
public class GameScreen implements Screen, DelegatingInputProcessor {

	private final HelloWorld helloWorld = new HelloWorld();

	public GameScreen() {
		helloWorld.create();
	}

	@Override
	public void render(float delta) {
		helloWorld.render();
	}

	@Override
	public void resize(int width, int height) {
		helloWorld.resize(width, height);
	}

	@Override
	public void pause() {
		helloWorld.pause();
	}

	@Override
	public void resume() {
		helloWorld.resume();
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		helloWorld.dispose();
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return helloWorld;
	}
}
