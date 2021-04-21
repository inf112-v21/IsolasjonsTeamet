package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import inf112.isolasjonsteamet.roborally.gui.ScreenController;
import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.impl.SingleplayerClientServer;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The root screen shown when the game starts, which wraps other main menu screens.
 */
public class MainMenuScreen extends StageScreen {

	private final ScreenController screenController;
	private final JoinMultiplayerScreen joinMultiplayerScreen;
	private final HostMultiplayerScreen hostMultiplayerScreen;
	private final InstructionsScreen instructionsScreen;

	/**
	 * Creates the main menu screen and all it's children.
	 */
	public MainMenuScreen(ScreenController screenController) {
		this.screenController = screenController;
		joinMultiplayerScreen = new JoinMultiplayerScreen(screenController);
		hostMultiplayerScreen = new HostMultiplayerScreen(screenController);
		instructionsScreen = new InstructionsScreen(screenController);
	}

	/**
	 * Instantiates this screen and it's children. Can not be called from the constructor, as libGDX has at that point
	 * not initialized yet.
	 */
	public void create() {
		super.create();
		joinMultiplayerScreen.create();
		hostMultiplayerScreen.create();
		instructionsScreen.create();

		var table = createRootTable();
		table.defaults().width(200).padBottom(3);

		var titleLabel = new Label("RoboRally", skin);
		titleLabel.setAlignment(Align.center);
		table.add(titleLabel);
		table.row();

		var startGameRow = new Table(skin);
		table.add(startGameRow);
		table.row();

		var startGameButton = new TextButton("Start game", skin);
		startGameRow.add(startGameButton).expandX();

		var playerNum = new TextField("4", skin);
		playerNum.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		startGameRow.add(playerNum).width(40F);
		startGameRow.add(" players");

		var joinMultiplayerButton = new TextButton("Join multiplayer", skin);
		table.add(joinMultiplayerButton);
		table.row();

		var hostMultiplayerButton = new TextButton("Host multiplayer", skin);
		table.add(hostMultiplayerButton);
		table.row();

		var instructionsButton = new TextButton("How to play", skin);
		table.add(instructionsButton);
		table.row();

		var exitButton = new TextButton("Exit game", skin);
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		table.add(exitButton);
		table.row();

		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				var playersNumStr = playerNum.getText();
				int playerNum;
				try {
					playerNum = Integer.parseInt(playersNumStr);
				} catch (NumberFormatException e) {
					playerNum = 1;
				}

				ImmutableList.Builder<PlayerInfo> playersBuilder = ImmutableList.builder();
				ImmutableSet.Builder<String> playerNamesBuilder = ImmutableSet.builder();
				for (int i = 1; i <= playerNum; i++) {
					playerNamesBuilder.add("Player" + i);
				}
				var playerNames = playerNamesBuilder.build();

				var localServerClient = new SingleplayerClientServer(playerNames);
				for (String playerName : playerNames) {
					var automaticClient = new Client() {

						@Override
						public String getUsername() {
							return localServerClient.getUsername();
						}

						@Override
						public void sendToServer(Client2ServerPacket packet) {
							localServerClient.setActivePlayer(playerName);
							localServerClient.sendToServer(packet);
						}

						@Override
						public void addListener(ClientPacketListener<?> listener) {
							localServerClient.setActivePlayer(playerName);
							localServerClient.addListener(listener);
						}

						@Override
						public void removeListener(ClientPacketListener<?> listener) {
							localServerClient.setActivePlayer(playerName);
							localServerClient.removeListener(listener);
						}

						@Override
						public CompletableFuture<Void> disconnect(@Nullable String reason) {
							localServerClient.setActivePlayer(playerName);
							return localServerClient.disconnect(reason);
						}

						@Override
						public void kickPlayer(String player, String reason) {
							localServerClient.kickPlayer(player, reason);
						}
					};

					playersBuilder.add(new PlayerInfo(playerName, automaticClient, true));
				}

				screenController.startGame(
						"example.tmx",
						playersBuilder.build(),
						playerNames.asList().get(0),
						playerNames.asList().get(0),
						localServerClient
				);
			}
		});

		joinMultiplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.pushInputScreen(joinMultiplayerScreen);
			}
		});

		hostMultiplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.pushInputScreen(hostMultiplayerScreen);
			}
		});

		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenController.pushInputScreen(instructionsScreen);
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();

		joinMultiplayerScreen.dispose();
		hostMultiplayerScreen.dispose();
		instructionsScreen.dispose();
	}
}
