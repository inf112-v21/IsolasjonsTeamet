package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;

/**
 * A screen showing users how to play the game.
 */
public class InstructionsScreen extends StageScreen {

	private final ScreenController screenController;

	public InstructionsScreen(ScreenController screenController) {
		this.screenController = screenController;
	}

	@Override
	void create() {
		super.create();

		var table = createRootTable();
		table.add("How to play");
		table.row().row();

		table.add("Just play the game");
		table.row();

		var backButton = new TextButton("Back", skin);
		table.add(backButton);

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.popInputScreen();
			}
		});
	}
}
