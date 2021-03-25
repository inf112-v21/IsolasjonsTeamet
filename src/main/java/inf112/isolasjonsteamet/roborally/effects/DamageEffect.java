package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Class for showing effect when taking damage.
 */
public class DamageEffect implements Effect {

	private Coordinate effPos;
	private Texture effTxt;

	public DamageEffect(Coordinate pos) {
		this.effTxt = new Texture("DamageEffect.png");
		this.effPos = pos;
	}

	@Override
	public void setEffect(Coordinate pos) {
		//Kode for å presentere grafisk her
	}
}
