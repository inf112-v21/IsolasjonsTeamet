package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ToggleButton {

	private final String trueState;
	private final String falseState;

	private boolean state;

	private final TextButton button;

	private final @Nullable Consumer<Boolean> stateListener;

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

	public void setState(boolean state) {
		this.state = state;
		button.setText(textForState());

		if (stateListener != null) {
			stateListener.accept(state);
		}
	}

	public void flip() {
		state = !state;
		button.setText(textForState());

		if (stateListener != null) {
			stateListener.accept(state);
		}
	}

	public void refreshText() {
		button.setText(textForState());
	}
}
