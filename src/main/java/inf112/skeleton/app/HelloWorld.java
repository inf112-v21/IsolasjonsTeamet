package inf112.skeleton.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class HelloWorld extends InputAdapter implements ApplicationListener {
    private SpriteBatch batch;
    private BitmapFont font;

    public TiledMap map;
    public TiledMapTileLayer boardLayer;
    public TiledMapTileLayer playerLayer;
    public TiledMapTileLayer holeLayer;
    public TiledMapTileLayer flagLayer;
    public OrthogonalTiledMapRenderer mapRenderer;
    public OrthographicCamera camera;

    public TiledMapTileLayer.Cell playerWonCell;
    public TiledMapTileLayer.Cell playerDiedCell;
    public TiledMapTileLayer.Cell playerCell;
    public TiledMapTileLayer.Cell defaultTileCell;
    public Vector2 playerVec;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        map = new TmxMapLoader().load("example.tmx");

        boardLayer = (TiledMapTileLayer) map.getLayers().get("Board");
        playerLayer = (TiledMapTileLayer) map.getLayers().get("Player");
        holeLayer = (TiledMapTileLayer) map.getLayers().get("Hole");
        flagLayer = (TiledMapTileLayer) map.getLayers().get("Flag");

        Texture playerTx = new Texture("player.png");
        TextureRegion[][] tReg = new TextureRegion().split(playerTx,300,300);

        Texture defTx = new Texture("tiles.png");
        TextureRegion[][] tReg2 = new TextureRegion().split(defTx,300,300);

        //Static tiles for playing, dead and winning player cells
        StaticTiledMapTile staticDefTile = new StaticTiledMapTile(tReg2[0][4]);
        StaticTiledMapTile staticPlayTile = new StaticTiledMapTile(tReg[0][0]);
        StaticTiledMapTile staticWonTile = new StaticTiledMapTile(tReg[0][2]);
        StaticTiledMapTile staticDiedTile = new StaticTiledMapTile(tReg[0][1]);

        defaultTileCell = new TiledMapTileLayer.Cell().setTile(staticDefTile);
        playerWonCell = new TiledMapTileLayer.Cell().setTile(staticWonTile);
        playerDiedCell = new TiledMapTileLayer.Cell().setTile(staticDiedTile);
        playerCell = new TiledMapTileLayer.Cell().setTile(staticPlayTile);
        playerVec = new Vector2(0,0);

        Gdx.input.setInputProcessor(this);

        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(map, (float) 1/300);
        camera.setToOrtho(false, (float) 5, 5);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2, 0);
        camera.update();

        playerLayer.setCell(0,0, playerCell);

        mapRenderer.setView(camera);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //playerLayer.setCell(1,2, playerCell);
        //playerLayer.setCell(0,2, playerWonCell);
        //playerLayer.setCell(0,4, playerDiedCell);

        mapRenderer.render();
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Input.Keys.W:
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y+1, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x,playerVec.y+1);
                return true;
            case Input.Keys.A:
                playerLayer.setCell((int) playerVec.x-1, (int) playerVec.y, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x-1,playerVec.y);
                return true;
            case Input.Keys.S:
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y-1, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x,playerVec.y-1);
                return true;
            case Input.Keys.D:
                playerLayer.setCell((int) playerVec.x+1, (int) playerVec.y, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x+1,playerVec.y);
                return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
