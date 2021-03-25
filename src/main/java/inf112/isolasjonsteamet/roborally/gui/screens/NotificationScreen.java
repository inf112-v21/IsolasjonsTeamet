package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;

public class NotificationScreen extends StageScreen {

	private final ScreenController screenController;
	private final String message;

	public NotificationScreen(ScreenController screenController, String message) {
		this.screenController = screenController;
		this.message = message;
	}

	@Override
	void create() {
		super.create();

		var table = createRootTable();
		table.add(message).colspan(2);
		table.row();

		var okButton = new TextButton("Ok", skin);
		okButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.popInputScreen();
			}
		});
		table.add(okButton).colspan(2);
	}
}
