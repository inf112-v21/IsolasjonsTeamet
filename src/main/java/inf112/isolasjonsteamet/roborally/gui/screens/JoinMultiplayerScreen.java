package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.network.Client;

/**
 * A screen allowing users to join a multiplayer game.
 */
public class JoinMultiplayerScreen extends StageScreen {

	private final ScreenController screenController;

	private TextField hostField;
	private TextField portField;
	private TextField usernameField;
	private Label statusLabel;

	public JoinMultiplayerScreen(ScreenController screenController) {
		this.screenController = screenController;
	}

	void create() {
		super.create();

		hostField = new TextField("", skin);
		portField = new TextField("21313", skin);
		portField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		usernameField = new TextField("", skin);
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

		table.add("Username:");
		table.add(usernameField).width(100).spaceLeft(10);
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

				joinMultiplayer(port);
			}
		});
	}

	@Override
	public void hide() {
		hostField.setText("");
		portField.setText("21313");
		usernameField.setText("");
		statusLabel.setText("");
	}

	private void joinMultiplayer(int port) {
		statusLabel.setText("Connecting to server");
		statusLabel.setColor(Color.YELLOW);
		var username = usernameField.getText();

		Client.connectAndVerify(hostField.getText(), port, username).whenComplete((t, e) -> {
			if (e != null) {
				Gdx.app.postRunnable(() -> {
					statusLabel.setText("Failed to connect to server: " + e.getMessage());
					statusLabel.setColor(Color.RED);
				});
			} else {
				Gdx.app.postRunnable(() -> statusLabel.setText("Connected..."));
				Client client = t.getKey();

				client.joinGame(username).thenAccept(result -> {
					switch (result) {
						case DENIED -> {
							Gdx.app.postRunnable(() -> {
								statusLabel.setText("Can't join server. Denied");
								statusLabel.setColor(Color.RED);
							});
							client.disconnect("Denied access to the server");
						}
						case NAME_IN_USE -> {
							Gdx.app.postRunnable(() -> {
								statusLabel.setText("Can't join server. Name in use");
								statusLabel.setColor(Color.RED);
							});
							client.disconnect("Desired name was in use");
						}
						case SUCCESS -> Gdx.app.postRunnable(() ->
								screenController.setInputScreen(new LobbyScreen(screenController, client))
						);
						default -> throw new IllegalArgumentException("Unknown join result " + result);
					}
				});
			}
		});
	}
}
