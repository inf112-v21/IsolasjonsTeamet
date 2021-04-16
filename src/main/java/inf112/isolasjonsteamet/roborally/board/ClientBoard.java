package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import inf112.isolasjonsteamet.roborally.effects.Effect;
import inf112.isolasjonsteamet.roborally.players.Player;

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

    void show(Player player);

    void hide(Player player);

    void addEffect(Effect effect);

    void removeEffect(Effect effect);

    void renderEffects(Batch batch);
}
