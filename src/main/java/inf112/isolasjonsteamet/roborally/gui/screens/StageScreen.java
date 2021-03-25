package inf112.isolasjonsteamet.roborally.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.isolasjonsteamet.roborally.gui.DelegatingInputProcessor;
import inf112.isolasjonsteamet.roborally.util.Environment;

/**
 * Common code for {@link Screen}s which uses a {@link Stage} for its layout and rendering.
 */
public class StageScreen implements Screen, DelegatingInputProcessor {

	protected Stage stage;
	protected Skin skin;

	void create() {
		if (stage != null) {
			stage.dispose();
		}

		if (skin != null) {
			skin.dispose();
		}

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		if (Environment.IS_DEV) {
			// A refresh button to recreate the screen if the application has
			// been started in debug mode and classes have changed.
			var refreshButton = new TextButton("R", skin);
			var posX = Gdx.graphics.getWidth() - refreshButton.getWidth();
			refreshButton.setPosition(posX, 0);

			stage.addActor(refreshButton);

			refreshButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					StageScreen.this.create();
				}
			});
		}
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
		if (stage == null) {
			create();
		}

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
		skin.dispose();
	}

	@Override
	public InputProcessor delegateInputsTo() {
		return stage;
	}
}
