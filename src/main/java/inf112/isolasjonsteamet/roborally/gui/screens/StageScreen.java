package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;

/**
 * Common code for {@link Screen}s which uses a {@link Stage} for its layout and rendering.
 */
public class StageScreen implements Screen, DelegatingInputProcessor {

	protected Stage stage;
	protected Skin skin;

	void create() {
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	}

	protected Table createRootTable() {
		var table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);
		return table;
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0F, 0F, 0F, 1F);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		stage.getViewport().apply();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return stage;
	}
}
