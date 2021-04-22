package inf112.isolasjonsteamet.roborally.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;
import inf112.isolasjonsteamet.roborally.board.ClientBoard;

/**
 * A {@link Widget} which wraps around a {@link TiledMapRenderer} renders it.
 */
public class MapRendererWidget extends Widget {

	private final OrthographicCamera camera = new OrthographicCamera(getWidth(), getHeight());
	private final int tileSize;

	private final TiledMapRenderer mapRenderer;
	private final ClientBoard board;

	/**
	 * Creates a map renderer widget with an orthogonal map renderer.
	 */
	public MapRendererWidget(ClientBoard board, int tileSize) {
		this.tileSize = tileSize;
		this.board = board;
		mapRenderer = new OrthogonalTiledMapRenderer(board.getMap(), (float) tileSize / board.getTextureTileSize());
		sizeChanged();
	}

	/**
	 * Draw method.
	 */
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		// The map renderer does not behave well together with scene2d's sprite batch,
		// so we need to stop the current one, and restart it after we have rendered the map.
		batch.end();

		mapRenderer.render();

		batch.begin();

		board.renderEffects(batch);
	}

	@Override
	public float getMaxHeight() {
		return getPrefHeight();
	}

	@Override
	public float getMaxWidth() {
		return getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return board.getHeight() * tileSize;
	}

	@Override
	public float getPrefWidth() {
		return board.getWidth() * tileSize;
	}

	/**
	 * Check if position is changed.
	 */
	@Override
	protected void positionChanged() {
		super.positionChanged();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(-getX(), -getY(), 0);

		camera.update();
		mapRenderer.setView(camera);
	}

	/**
	 * Check if size changed.
	 */
	@Override
	protected void sizeChanged() {
		super.sizeChanged();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(-getX(), -getY(), 0);

		camera.update();
		mapRenderer.setView(camera);
	}

	@Override
	public void clear() {
		super.clear();
		if (mapRenderer instanceof Disposable) {
			((Disposable) mapRenderer).dispose();
		}
	}
}
