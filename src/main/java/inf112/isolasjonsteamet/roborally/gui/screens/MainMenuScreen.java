package inf112.isolasjonsteamet.roborally.gui.screens;

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
import inf112.isolasjonsteamet.roborally.network.impl.SingleplayerClientServer;
import inf112.isolasjonsteamet.roborally.players.PlayerInfo;

/**
 * The root screen shown when the game starts, which wraps other main menu screens.
 */
public class MainMenuScreen extends StageScreen {

	private final ScreenController screenController;
	private final JoinMultiplayerScreen joinMultiplayerScreen;
	private final HostMultiplayerScreen hostMultiplayerScreen;
	private final InstructionsScreen instructionsScreen;

	/**
	 * Creates the main meny screen and all it's children.
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
		startGameRow.add(startGameButton);

		var playerNum = new TextField("4", skin);
		playerNum.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		startGameRow.add(playerNum);
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
					playersBuilder.add(new PlayerInfo(playerName, localServerClient, true));
				}

				screenController.startGame("example.tmx", playersBuilder.build(), localServerClient);
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
}
