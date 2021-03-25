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
import inf112.isolasjonsteamet.roborally.network.impl.NettyServerImpl;

/**
 * A screen allowing users to host a multiplayer game.
 */
public class HostMultiplayerScreen extends StageScreen {

	private final ScreenController screenController;

	private TextField portField;
	private TextField gameNameField;
	private TextField usernameField;
	private Label statusLabel;

	public HostMultiplayerScreen(ScreenController screenController) {
		this.screenController = screenController;
	}

	void create() {
		super.create();

		portField = new TextField("", skin);
		portField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		gameNameField = new TextField("", skin);
		usernameField = new TextField("", skin);
		statusLabel = new Label("", skin);

		var table = createRootTable();
		table.add("Multiplayer host").colspan(2);
		table.row();

		table.add("Port:");
		table.add(portField).width(100).spaceLeft(10);
		table.row();

		table.add("Game name:");
		table.add(gameNameField).width(100).spaceLeft(10);
		table.row();

		table.add("Username:");
		table.add(usernameField).width(100).spaceLeft(10);
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

				startServer(port);
			}
		});
	}

	@Override
	public void hide() {
		portField.setText("");
		statusLabel.setText("");
	}

	private void startServer(int port) {
		statusLabel.setText("Starting server...");
		statusLabel.setColor(Color.YELLOW);

		NettyServerImpl server = new NettyServerImpl("localhost", port, gameNameField.getText());

		server.ready().whenComplete((v, e) -> {
			if (e != null) {
				Gdx.app.postRunnable(() -> {
					statusLabel.setText("Failed to start server: " + e.getMessage());
					statusLabel.setColor(Color.RED);
				});
			} else {
				Gdx.app.postRunnable(() -> statusLabel.setText("Server started. Starting client..."));

				Client.connectAndVerify("localhost", port).whenComplete((tuple, e2) -> {
					if (e2 != null) {
						Gdx.app.postRunnable(() -> {
							statusLabel.setText("Failed to start and connect with client: " + e2.getMessage());
							statusLabel.setColor(Color.RED);
						});
						server.close("Client host connection failed");
					} else {
						Client client = tuple.getKey();
						var username = usernameField.getText();
						Gdx.app.postRunnable(() -> statusLabel.setText("Joining the game"));
						client.joinGame(username).thenAccept(result -> {
							switch (result) {
								case DENIED -> {
									Gdx.app.postRunnable(() -> {
										statusLabel.setText("Was denied joining the game");
										statusLabel.setColor(Color.RED);
									});
									server.close("Client host connection failed");
								}
								case SUCCESS -> Gdx.app.postRunnable(() ->
										screenController.setInputScreen(
												new LobbyScreen(screenController, server, username, client)
										));
								case NAME_IN_USE -> {
									Gdx.app.postRunnable(() -> {
										statusLabel.setText("Someone joined before the host. Unhandled error");
										statusLabel.setColor(Color.RED);
									});
									server.close("Client host connection failed");
								}
								default -> throw new IllegalArgumentException("Unknown join result " + result);
							}
						});
					}
				});
			}
		});
		server.start();
	}
}
