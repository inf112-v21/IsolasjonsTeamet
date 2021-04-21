package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Player effect class.
 */
public class PlayerEffect implements Effect {

	private static final TextureRegion PLAYER_TEXTURE;

	static {
		Texture playerTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(playerTx, 300, 300);
		PLAYER_TEXTURE = tReg[0][0];
	}

	Orientation dir;
	private final Sprite sprite;

	/**
	 * New intance of PlayerEffect.
	 */
	public PlayerEffect(Player player) {
		sprite = new Sprite(PLAYER_TEXTURE);

		Coordinate pos = player.getPos();
		int x = pos.getX();
		int y = pos.getY();
		Orientation dir = player.getDir();

		sprite.setPosition((x + 2) * 100 + 50, (y + 1) * 100);
		sprite.setScale(0.33F);
	}

	@Override
	public void render(Batch batch) {
		sprite.draw(batch);
	}

	/**
	 * Move the player effect.
	 */
	public void move(float dx, float dy) {
		float currentX = sprite.getX() + dx * 100;
		float currentY = sprite.getY() + dy * 100;
		System.out.println(currentX);
		System.out.println(currentY);
		
		//Warning: this out of bounds check works only for the example map.
		if ((currentX >= 250 && currentX <= 650) && (currentY >= 100 && currentY <= 500)) {
			sprite.setPosition(sprite.getX() + dx * 100, sprite.getY() + dy * 100);
		}
	}

	public void rotate() {
		sprite.rotate(90);
	}
}
