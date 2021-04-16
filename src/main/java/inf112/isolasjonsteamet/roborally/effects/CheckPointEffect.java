package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Effect for reaching checkpoint.
 */
public class CheckPointEffect implements Effect {

	private Coordinate effPos;
	private Texture effTxt;

	public CheckPointEffect(Coordinate pos) {
		this.effTxt = new Texture("CheckPointEffect.png");
		this.effPos = pos;
	}

	public void setEffect(Coordinate pos, Player player) {
		//Kode for å presentere grafisk her
	}
}
