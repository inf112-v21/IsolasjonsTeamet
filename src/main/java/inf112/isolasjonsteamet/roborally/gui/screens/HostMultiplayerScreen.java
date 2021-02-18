package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * A screen allowing users to host a multiplayer game.
 */
public class HostMultiplayerScreen extends StageScreen {

	private final ScreenController screenController;

	private TextField portField;
	private Label statusLabel;

	public HostMultiplayerScreen(ScreenController screenController) {
		this.screenController = screenController;
	}

	void create() {
		super.create();

		portField = new TextField("", skin);
		portField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		statusLabel = new Label("", skin);

		var table = createRootTable();
		table.add("Multiplayer host").colspan(2);
		table.row();

		table.add("Port:");
		table.add(portField).width(100).spaceLeft(10);
		table.row();

		table.add(statusLabel).colspan(2);
		table.row();

		var hostButton = new TextButton("Host!", skin);
		var backButton = new TextButton("Back", skin);
		table.add(backButton);
		table.add(hostButton);

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//TODO: Shutdown server here first
				screenController.popInputScreen();
			}
		});

		hostButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (portField.getText().isBlank()) {
					statusLabel.setText("Port can not be empty");
					statusLabel.setColor(Color.RED);
					return;
				}

				int port;
				try {
					port = Integer.parseInt(portField.getText());
				} catch (NumberFormatException e) {
					statusLabel.setText("Port must be an valid integer");
					statusLabel.setColor(Color.RED);
					return;
				}

				//TODO: Try to actually start the server
				statusLabel.setText("Starting server");
				statusLabel.setColor(Color.YELLOW);

				Executor delayedExecutor = CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS);
				var serverConnected = CompletableFuture.supplyAsync(() -> null, delayedExecutor);

				serverConnected.whenComplete((v, e) -> {
					if (e != null) {
						statusLabel.setText("Failed to start server: " + e.getMessage());
						statusLabel.setColor(Color.RED);
					} else {
						//Pass server to game somehow
						Gdx.app.postRunnable(screenController::startGame);
					}
				});
			}
		});
	}
}
