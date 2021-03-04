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
 * A screen allowing users to join a multiplayer game.
 */
public class JoinMultiplayerScreen extends StageScreen {

	private final ScreenController screenController;

	private TextField hostField;
	private TextField portField;
	private Label statusLabel;

	public JoinMultiplayerScreen(ScreenController screenController) {
		this.screenController = screenController;
	}

	void create() {
		super.create();

		hostField = new TextField("", skin);
		portField = new TextField("", skin);
		portField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		statusLabel = new Label("", skin);

		var table = createRootTable();
		table.add("Multiplayer join").colspan(2);
		table.row();

		table.add("Host:");
		table.add(hostField).width(100).spaceLeft(10);
		table.row();

		table.add("Port:");
		table.add(portField).width(100).spaceLeft(10);
		table.row();

		table.add(statusLabel).colspan(2);
		table.row();

		var connectButton = new TextButton("Connect!", skin);
		var backButton = new TextButton("Back", skin);
		table.add(backButton);
		table.add(connectButton);

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.popInputScreen();
			}
		});

		connectButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (hostField.getText().isBlank()) {
					statusLabel.setText("Host can not be empty");
					statusLabel.setColor(Color.RED);
					return;
				}

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

				//TODO: Try to actually connect to the server
				statusLabel.setText("Connecting to server");
				statusLabel.setColor(Color.YELLOW);

				Executor delayedExecutor = CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS);
				var serverConnected = CompletableFuture.supplyAsync(() -> null, delayedExecutor);

				serverConnected.whenComplete((v, e) -> {
					if (e != null) {
						statusLabel.setText("Failed to connect to server: " + e.getMessage());
						statusLabel.setColor(Color.RED);
					} else {
						//Pass client to game somehow
						Gdx.app.postRunnable(screenController::startGame);
					}
				});
			}
		});
	}

	@Override
	public void hide() {
		hostField.setText("");
		portField.setText("");
		statusLabel.setText("");
	}
}
