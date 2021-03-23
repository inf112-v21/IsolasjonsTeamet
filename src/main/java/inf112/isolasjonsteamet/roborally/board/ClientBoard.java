package inf112.isolasjonsteamet.roborally.board;

import com.badlogic.gdx.maps.tiled.TiledMap;

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
}
