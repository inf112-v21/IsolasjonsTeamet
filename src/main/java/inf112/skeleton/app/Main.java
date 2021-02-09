package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Entry class into the application.
 */
public class Main {

	/**
	 * Entrypoint into the application.
	 */
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		cfg.setTitle("hello-world");
		cfg.setWindowedMode(480, 320);

		new Lwjgl3Application(new HelloWorld(), cfg);
	}
}