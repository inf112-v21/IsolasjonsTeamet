package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
		table.add("How to play").colspan(2);
		table.row();

		var instruction =
				new Label("This is the board. Your goal is to get to the flag, "
						+ "and avoid the hole. You move with WASD.", skin);
		instruction.setWrap(true);
		table.add(instruction).width(200);

		var texture = new Texture(Gdx.files.internal("guis/instructions/board.png"));
		var boardImage = new Image(texture);
		table.add(boardImage).width(texture.getWidth() / 2F).height(texture.getHeight() / 2F).pad(15);
		table.row();

		var backButton = new TextButton("Back", skin);
		table.add(backButton).colspan(2);

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.popInputScreen();
			}
		});
	}
}
