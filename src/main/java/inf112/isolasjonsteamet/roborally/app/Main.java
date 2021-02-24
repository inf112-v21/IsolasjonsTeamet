package inf112.isolasjonsteamet.roborally.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Main class to start a new application.
 */
public class Main {

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle("RoboRally");
		cfg.setWindowedMode(500, 500);

		//Creates a new instance of Game, our roborally game
		new Lwjgl3Application(new Game(), cfg);
	}
}