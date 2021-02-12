package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Main class to start a new application.
 */
public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("First Tile Map");
        cfg.setWindowedMode(500, 500);

        //Creates a new instance of HelloWorld, our game
        new Lwjgl3Application(new HelloWorld(), cfg);
    }
}