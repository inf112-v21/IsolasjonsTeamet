package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import inf112.isolasjonsteamet.roborally.effects.Effect;
import inf112.isolasjonsteamet.roborally.players.Robot;

/**
 * Data and behavior about boards only found on the client side.
 */
public interface ClientBoard extends Board {

	/**
	 * The map which is responsible for rendering and showing the graphical representation of the board.
	 */
	TiledMap getMap();

	/**
	 * How large the tiles in the texture atlas are in pixels.
	 */
	int getTextureTileSize();

	/**
	 * How large the tiles are when they are rendered.
	 */
	int getTextureRenderSize();

	/**
	 * Updates the state of the board to how it should, before rendering it.
	 */
	void act();

	/**
	 * Show robot.
	 */
	void show(Robot robot);

	/**
	 * Hide robot.
	 */
	void hide(Robot robot);

	/**
	 * Add an effect.
	 */
	void addEffect(Effect effect);

	/**
	 * Remove an effect.
	 */
	void removeEffect(Effect effect);

	/**
	 * Render the effects.
	 */
	void renderEffects(Batch batch);
}
