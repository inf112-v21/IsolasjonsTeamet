package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

/**
 * An object which allows control over the active screen being shown by libGDX. Has a stack structure where active
 * screens can be pushed and popped.
 */
public interface ScreenController {

	/**
	 * Pops all the screens on the stack until only the main menu screen remains.
	 */
	void returnToMainMenu();

	/**
	 * Replaces the current screen with a new one.
	 */
	<T extends Screen & InputProcessor> void setInputScreen(T inputScreen);

	/**
	 * Pushes the given screen to the top of the screen stack and sets it as the active screen.
	 */
	<T extends Screen & InputProcessor> void pushInputScreen(T inputScreen);

	/**
	 * Goes back to the previous screen, popping the currently active one.
	 *
	 * @return If there are more screens than the bottom screen left.
	 */
	boolean popInputScreen();

	/**
	 * Starts a game and pushes that screen to the stack.
	 */
	void startGame();
}
