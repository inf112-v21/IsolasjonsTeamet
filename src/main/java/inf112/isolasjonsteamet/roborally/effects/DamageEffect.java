package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Class for showing effect when taking damage.
 */
public class DamageEffect implements Effect {

	private Coordinate effPos;
	private Texture effTxt;

	/**
	 * Constructor for damage effect.
	 */
	public DamageEffect(Coordinate pos) {
		this.effTxt = new Texture("DamageEffect.png");
		//playerDamaged = new TiledMapTileLayer.Cell();
		this.effPos = pos;
	}

	@Override
	public void setEffect(Coordinate pos, Player player) {
		/*
		Texture texture = new Texture("player.png");
		TextureRegion textureRegion = new TextureRegion(texture);
		TextureRegion[][] pictures = textureRegion.split(texture,300, 300);
		playerDamaged.setTile(new StaticTiledMapTile(pictures[0][1]));
		System.out.println("Test");
		 */
	}
}
