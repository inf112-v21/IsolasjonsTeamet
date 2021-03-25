package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

/**
 * Class for showing effect when taking reaching flag.
 */
public class FlagEffect implements Effect {

	private Coordinate effPos;
	private Texture effTxt;

	public FlagEffect(Coordinate pos) {
		this.effTxt = new Texture("FlagEffect.png");
		this.effPos = pos;
	}

	@Override
	public void setEffect(Coordinate pos) {
		//Kode for å presentere grafisk her
	}
}
