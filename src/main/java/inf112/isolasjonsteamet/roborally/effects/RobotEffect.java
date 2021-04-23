package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;
import inf112.isolasjonsteamet.roborally.players.Robot;
import inf112.isolasjonsteamet.roborally.util.Coordinate;
import inf112.isolasjonsteamet.roborally.util.Orientation;

/**
 * Player effect class.
 */
public class RobotEffect implements Effect {

	private static final TextureRegion PLAYER_TEXTURE;

	static {
		Texture playerTx = new Texture("player.png");
		final TextureRegion[][] tReg = TextureRegion.split(playerTx, 300, 300);
		PLAYER_TEXTURE = tReg[0][0];
	}

	private final Sprite sprite;
	private final float tileSize;

	/**
	 * New intance of PlayerEffect.
	 */
	@SuppressWarnings("checkstyle:Indentation")
	public RobotEffect(ClientBoard board, Robot robot) {
		sprite = new Sprite(PLAYER_TEXTURE);

		Coordinate pos = robot.getPos();
		int x = pos.getX();
		int y = pos.getY();

		tileSize = board.getTextureRenderSize();

		sprite.setPosition((x + 2) * tileSize + 366, (y + 1) * tileSize + -100);
		sprite.setScale(tileSize / board.getTextureTileSize());

		float rotateDegrees = switch (robot.getDir()) {
			case NORTH -> 0F;
			case WEST -> 90F;
			case SOUTH -> 180F;
			case EAST -> 270F;
		};
		sprite.setRotation(rotateDegrees);
	}

	@Override
	public void render(Batch batch) {
		sprite.draw(batch);
	}

	/**
	 * Move the player effect.
	 */
	public void move(float dx, float dy) {
		sprite.setPosition(sprite.getX() + dx * tileSize, sprite.getY() + dy * tileSize);
	}

	public void rotate(float amount) {
		sprite.rotate(amount);
	}
}
