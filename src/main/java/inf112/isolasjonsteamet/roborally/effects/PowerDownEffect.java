package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Class for showing effect when taking powering down.
 */
public class PowerDownEffect implements Effect {

	private Coordinate effPos;
	private Texture effTxt;

	public PowerDownEffect(Coordinate pos) {
		this.effTxt = new Texture("PowerDownEffect.png");
		this.effPos = pos;
	}

	public void setEffect(Coordinate pos, Robot robot) {
		//Kode for å presentere grafisk her
	}
}
