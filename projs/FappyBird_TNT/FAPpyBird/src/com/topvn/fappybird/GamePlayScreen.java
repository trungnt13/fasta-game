
package com.topvn.fappybird;

import java.util.Random;

import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.Input;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.objs.Sprite;
import org.tntstudio.graphics.objs.SpriteAnimation;
import org.tntstudio.graphics.objs.SpriteManager;
import org.tntstudio.graphics.objs.SpriteWorld;
import org.tntstudio.graphics.objs.Updater;
import org.tntstudio.resources.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Timer;

public class GamePlayScreen extends BaseScreen {
	SpriteWorld world = SpriteWorld.newWorld("FappyBird");
	SpriteManager birdManager = world.newManager("Birds", SpriteManager.class);
	SpriteManager obstacleManager = world.newManager("Obstacles", SpriteManager.class);

	Texture bg;
	TextureRegion[] bird;
	FappyBird fapfap;
	SpriteAnimation animation;

	ShapeRenderer shape;

	boolean isShowAdmob = true;

	@Override
	public void onCreate () {
		bg = Top.tres.rGet("bg.png", Texture.class);

		bird = Graphics.split(Top.tres.rGet("bird.png", Texture.class), 3, 68, 256 / 3, 0);

		fapfap = birdManager.newSprite(FappyBird.class);
		fapfap.postUpdater(new Updater() {
			@Override
			public boolean update (Sprite sprite, float delta) {
				return false;
			}
		});
		animation = fapfap.newSprite(SpriteAnimation.class).bind(bird);
		fapfap.setPosition(100, 100);
		fapfap.setLoop(true).setPlaybackSpeed(0.3f).start();
		bindInputCallback(fapfap);

		shape = shapeRenderer();

		/*-------- input callback for testing --------*/

		bindInputCallback(new Input.MotionCallback() {
			@Override
			public boolean onTouchUp (float x, float y, int pointer, int button) {
				int i = new Random().nextInt(4);
				String pos = "topleft";
				if (i == 0) {
					pos = "topright";
				} else if (i == 1) {
					pos = "bottomleft";
				} else if (i == 2) {
					pos = "bottomright";
				} else if (i == 3) {

				}

				if (isShowAdmob)
					Top.tcus.onShowAdview(false);
				else
					Top.tcus.onShowAdview(true, pos);
				isShowAdmob = !isShowAdmob;

				return false;
			}

			@Override
			public boolean onTouchMoved (float x, float y) {
				return false;
			}

			@Override
			public boolean onTouchDragged (float x, float y, int pointer) {
				return false;
			}

			@Override
			public boolean onTouchDown (float x, float y, int pointer, int button) {
				return false;
			}
		});
		/*-------- timer --------*/
		Timer.schedule(new Timer.Task() {
			public void run () {
				Top.tcus.onRefreshAdview();
			}
		}, 5, 20);
	}

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate (float delta) {
		world.update(delta);
		fapfap.update(delta);
	}

	@Override
	public void onRender (SpriteBatch batch) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 1, 1, 1);

		batch.begin();
		batch.draw(bg, 0, 0, vpWidth(), vpHeight());
		world.draw(batch);
		fapfap.draw(batch);
		batch.end();

		shape.begin(ShapeType.Line);
		shape.setColor(Color.RED);
		shape.polygon(fapfap.getBounding().get(0).items);
		shape.end();
	}

	@Override
	public void onHide (ScreenState mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisposed () {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isGamePlayScreen () {
		// TODO Auto-generated method stub
		return true;
	}

}
