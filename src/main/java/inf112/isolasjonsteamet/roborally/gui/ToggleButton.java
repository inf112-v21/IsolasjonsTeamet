package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A button which can be toggled, either programmatically, or from the player pressing it.
 */
public class ToggleButton {

	private final String trueState;
	private final String falseState;

	private boolean state;

	private final TextButton button;

	private final @Nullable Consumer<Boolean> stateListener;

	/**
	 * Construct a new toggle button.
	 *
	 * @param trueState The text to display in the true state.
	 * @param falseState The text to display in the false state.
	 * @param startingState The starting state.
	 * @param button A button to wrap the logic around.
	 * @param stateListener A listener for when the state changes.
	 */
	public ToggleButton(
			String trueState,
			String falseState,
			boolean startingState,
			TextButton button,
			@Nullable Consumer<Boolean> stateListener
	) {
		this.trueState = trueState;
		this.falseState = falseState;
		this.state = startingState;

		this.stateListener = stateListener;
		this.button = button;
		button.setText(textForState());

		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				flip();
			}
		});
	}

	/**
	 * Construct a new toggle button.
	 *
	 * @param trueState The text to display in the true state.
	 * @param falseState The text to display in the false state.
	 * @param startingState The starting state.
	 * @param skin The skin to create the button with.
	 * @param stateListener A listener for when the state changes.
	 */
	public ToggleButton(
			String trueState,
			String falseState,
			boolean startingState,
			Skin skin,
			@Nullable Consumer<Boolean> stateListener
	) {
		this(trueState, falseState, startingState, new TextButton("", skin), stateListener);
	}

	private String textForState() {
		return state ? trueState : falseState;
	}

	public TextButton getButton() {
		return button;
	}

	public boolean getState() {
		return state;
	}

	/**
	 * Sets the state of the button, updating the text it displays, and runs the listener.
	 */
	public void setState(boolean state) {
		setStateQuietly(state);

		if (stateListener != null) {
			stateListener.accept(state);
		}
	}

	/**
	 * Sets the state of the button, updating the text it displays without running the listener.
	 */
	public void setStateQuietly(boolean state) {
		this.state = state;
		button.setText(textForState());
	}

	/**
	 * Flips or toggles the state of the button.
	 */
	public void flip() {
		state = !state;
		button.setText(textForState());

		if (stateListener != null) {
			stateListener.accept(state);
		}
	}

	/**
	 * Sets the text of the button to what it should be for the current state. Useful if external code has modified the
	 * text of the button.
	 */
	public void refreshText() {
		button.setText(textForState());
	}
}
