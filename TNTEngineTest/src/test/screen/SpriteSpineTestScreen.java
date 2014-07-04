
package test.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import org.tntstudio.Debug;
import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.Input;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.Spine;
import org.tntstudio.graphics.Updater;
import org.tntstudio.graphics.objs.SpriteSpine;
import org.tntstudio.graphics.objs.SpriteWorld;
import org.tntstudio.interfaces.Updating;
import org.tntstudio.utils.io.IOUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteSpineTestScreen extends BaseScreen {

	SpriteWorld world;
	SpriteSpine sprite;
	Object walk = null;
	Object jump = null;

	@Override
	public void onCreate () {
		world = SpriteWorld.newWorld("MyWorld");
		sprite = world.newSprite(SpriteSpine.class);

		Spine spine = Top.tres.rGet("spineboy", Spine.class);
		Debug.out(spine);
		sprite.bind(spine.createSkeleton(1f)).setPosition(100, 100);
		sprite.setAnimation(1, "jump", false);
		sprite.addAnimation(1, "walk", true);
		sprite.setAnimation(2, "drawOrder", true);
		sprite.setPlaybackSpeed(0.5f).start(10f);

		LuaState lua = LuaStateFactory.newLuaState();
		lua.openLibs();
		try {
			lua.LdoString(IOUtils.toString(new FileInputStream(new File("data/script/spine.lua"))));
			Debug.out(lua.dumpStack());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			walk = lua.getLuaObject("walk").createProxy(Updating.class.getName());
			jump = lua.getLuaObject("jump").createProxy(Updating.class.getName());
			System.out.println(walk);
			Updater.bind("walk", (Updating)walk);
			Updater.bind("jump", (Updating)jump);
// walk.update(sprite, 1);
// jump.update(sprite, 1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (LuaException e) {
			e.printStackTrace();
		}

		bindInputCallback(new Input.KeyCallback() {
			@Override
			public boolean onKeyUp (int keycode) {
				if (keycode == Keys.W) {
					Debug.out("Walk now");
					sprite.postUpdater("walk");
// ((Updating)walk).update(sprite, 1);
// sprite.start();
				} else if (keycode == Keys.J) {
					Debug.out("Jump now");
					sprite.postUpdater("jump");
// ((Updating)jump).update(sprite, 1);
// sprite.start();
				} else if (keycode == Keys.S) {
					if (sprite.isRunning())
						sprite.pause();
					else
						sprite.start();
				}
				return false;
			}

			@Override
			public boolean onKeyTyped (char character) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onKeyDown (int keycode) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate (float delta) {
		sprite.update(delta);
	}

	@Override
	public void onRender (SpriteBatch batch) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		batch.end();
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
		return true;
	}
}
