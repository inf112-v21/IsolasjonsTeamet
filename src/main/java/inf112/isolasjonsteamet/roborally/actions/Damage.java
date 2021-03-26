package inf112.isolasjonsteamet.roborally.actions;

import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.isolasjonsteamet.roborally.app.RoboRallyGame;
import inf112.isolasjonsteamet.roborally.board.Board;
import inf112.isolasjonsteamet.roborally.board.BoardClientImpl;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.players.PlayerImpl;
import inf112.isolasjonsteamet.roborally.players.Player;

import java.awt.*;

/**
 * Damage is an action that damages a robot x much according to the game obstacles.
 */
public class Damage implements Action {

	private Player[] players;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(ActionProcessor processor, Board board, Player player) {
		player.damageRobot();
	}


	/**
	 * Create a method for x amount of damage and then implement it in the laser method
	 * add a method that implements multiple lasers
	 *
	 * @param x the x position
	 * @param y the y position
	 * @return true if laser didn't hit anything
	 */
	public boolean Laser(int x, int y) {
		TiledMapTileLayer laserLayer = (TiledMapTileLayer) BoardClientImpl.getMap().getLayers().get("Laser");

		if (laserLayer.getCell(x, y) != null) {
			for (Player player : players) {
				if (player.getPos().getX() == x && player.getPos().getY() == y) {
					player.damageRobot();
					return false;
				}
			}
		}
		return true;
	}

}
