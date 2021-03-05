package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.InputProcessor;

/**
 * An {@link InputProcessor} which delegates the events it receives to another processor.
 */
public interface DelegatingInputProcessor extends InputProcessor {

	InputProcessor delegateInputsTo();

	@Override
	default boolean keyDown(int keycode) {
		return delegateInputsTo().keyDown(keycode);
	}

	@Override
	default boolean keyUp(int keycode) {
		return delegateInputsTo().keyUp(keycode);
	}

	@Override
	default boolean keyTyped(char character) {
		return delegateInputsTo().keyTyped(character);
	}

	@Override
	default boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return delegateInputsTo().touchDown(screenX, screenY, pointer, button);
	}

	@Override
	default boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return delegateInputsTo().touchUp(screenX, screenY, pointer, button);
	}

	@Override
	default boolean touchDragged(int screenX, int screenY, int pointer) {
		return delegateInputsTo().touchDragged(screenX, screenY, pointer);
	}

	@Override
	default boolean mouseMoved(int screenX, int screenY) {
		return delegateInputsTo().mouseMoved(screenX, screenY);
	}

	@Override
	default boolean scrolled(float amountX, float amountY) {
		return delegateInputsTo().scrolled(amountX, amountY);
	}
}
