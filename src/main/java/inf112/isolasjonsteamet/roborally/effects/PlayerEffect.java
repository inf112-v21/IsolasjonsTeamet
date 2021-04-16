package inf112.isolasjonsteamet.roborally.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import inf112.isolasjonsteamet.roborally.players.Player;
import inf112.isolasjonsteamet.roborally.util.Coordinate;

public class PlayerEffect implements Effect {

    private static final TextureRegion PLAYER_TEXTURE;

    static {
        Texture playerTx = new Texture("player.png");
        final TextureRegion[][] tReg = TextureRegion.split(playerTx, 300, 300);
        PLAYER_TEXTURE = tReg[0][0];
    }

    private final Sprite sprite;

    public PlayerEffect(Player player) {
        sprite = new Sprite(PLAYER_TEXTURE);

        Coordinate pos = player.getPos();
        int x = pos.getX();
        int y = pos.getY();

        sprite.setPosition((x + 2) * 100 + 50, (y + 1) * 100);
        sprite.setScale(0.33F);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void move(float dx, float dy) {
        sprite.setPosition(sprite.getX() + dx * 100, sprite.getY() + dy * 100);
    }
}
