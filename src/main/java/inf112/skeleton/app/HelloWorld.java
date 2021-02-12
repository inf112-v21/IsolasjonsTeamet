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

/**
 * Game class that starts a new game.
 */
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
	/**
	 * Create method used to create new items and elements used in the game.
	 */
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
        StaticTiledMapTile defaultTileTexture = new StaticTiledMapTile(tReg2[15][4]);
        StaticTiledMapTile staticPlayTile = new StaticTiledMapTile(tReg[0][0]);
        StaticTiledMapTile staticWonTile = new StaticTiledMapTile(tReg[0][2]);
        StaticTiledMapTile staticDiedTile = new StaticTiledMapTile(tReg[0][1]);

        defaultTileCell = new TiledMapTileLayer.Cell().setTile(defaultTileTexture);
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

	/**
	 * Method for disposal of items.
	 */
	@Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

	/**
	 * Render method that places new and changes current items on the board, dynamically.
	 */
	@Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //playerLayer.setCell(1,2, playerCell);
        //playerLayer.setCell(0,2, playerWonCell);
        //playerLayer.setCell(0,4, playerDiedCell);

        if(flagLayer.getCell((int) playerVec.x, (int) playerVec.y) != null) {
            playerLayer.setCell((int) playerVec.x, (int) playerVec.y, playerWonCell);

        }
        if(holeLayer.getCell((int) playerVec.x, (int) playerVec.y) != null) {
            playerLayer.setCell((int) playerVec.x, (int) playerVec.y, playerDiedCell);
        }

        mapRenderer.render();
    }


    /**
	 * keyUp method that listens for keys released on the keyboard, and perfoms wanted action based on conditions.
     */
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                if(playerVec.y >= boardLayer.getHeight()-1){
                    return true;
                }
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y + 1, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x, playerVec.y + 1);
                System.out.println("W-Pressed; Player moved up");
                return true;

            case Input.Keys.A:
                if (playerVec.x < 1){
                    return true;
                }
                playerLayer.setCell((int) playerVec.x-1, (int) playerVec.y, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x-1,playerVec.y);
                System.out.println("A-Pressed; Player moved left");
                return true;

            case Input.Keys.S:
                if(playerVec.y < 1){
                    return true;
                }
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y - 1, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x, playerVec.y -1);
                System.out.println("S-Pressed; Player moved down");
                return true;

            case Input.Keys.D:
                if (playerVec.x >= boardLayer.getWidth()-1){
                    return true;
                }
                playerLayer.setCell((int) playerVec.x+1, (int) playerVec.y, playerCell);
                playerLayer.setCell((int) playerVec.x, (int) playerVec.y, defaultTileCell);
                playerVec.set(playerVec.x+1,playerVec.y);
                System.out.println("D-Pressed; Player moved right");
                return true;
        }
        return false;
    }

	/**
	 * Method for resizing.
	 * @param width
	 * @param height
	 */
	@Override
    public void resize(int width, int height) {
    }

	/**
	 * Pause method to pause an active game.
	 */
	@Override
    public void pause() {
    }

	/**
	 * Resume method to resume paused game.
	 */
	@Override
    public void resume() {
    }
}
