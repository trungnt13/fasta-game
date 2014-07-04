
package test.screen;

import org.tntstudio.Debug;
import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.objs.SpriteAnimation;
import org.tntstudio.graphics.objs.SpriteParticle;
import org.tntstudio.graphics.objs.SpriteWorld;
import org.tntstudio.resources.Graphics;
import org.tntstudio.utils.UnitEvaluation.TimeRecord;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;

public class SpriteParticleScreen extends BaseScreen {

	SpriteParticle effect;
	SpriteAnimation ice;
	SpriteAnimation black;
	FrameBuffer frameBuffer;
	SpriteWorld world;

	Texture bg;
	float angle = -90f;
	float x, y;
	float v = 200f;
	float w = 50f;

	Texture tmpTex;

	@Override
	public void onCreate () {
		world = SpriteWorld.newWorld("TestWorld");

		effect = world.newSprite(SpriteParticle.class);
		effect.bind(Top.tres.rGet("data/particle_ice", ParticleEffect.class));
		x = 280;
		y = 360;
		ice = world.newSprite(SpriteAnimation.class);
		Texture texture = Top.tres.rGet("data/ice.png", Texture.class);
		ice.bind(Graphics.split(new TextureRegion(texture), 5, 1, 5, 0));
		ice.setSize(ice.getWidth() * 0.5f, ice.getHeight() * 0.5f);
		ice.setOrigin(ice.getWidth() / 2, ice.getHeight() / 2);
		ice.setPosition(x - ice.getOriginX(), y - ice.getOriginY());
// ice.setRotation(angle);
		ice.setLoop(true);
		ice.setPlaybackSpeed(0.1f);
		ice.start();

		effect.setPosition(x, y);

		bg = Top.tres.rGet("data/background.jpg", Texture.class);
		Pixmap pixmap = new Pixmap(1280, 720, Format.RGBA8888);
		pixmap.setColor(new Color(0, 0, 0, 0.3f));
		pixmap.fill();
		Texture tex = new Texture(pixmap);
		pixmap.dispose();

		black = world.newSprite(SpriteAnimation.class).bind(tex);
		black.setPosition(0, 0);

		frameBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	TimeRecord time = new TimeRecord();

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdate (float delta) {
		angle += delta * w;
		float dx = v * delta * MathUtils.cosDeg(angle), dy = v * delta * MathUtils.sinDeg(angle);

//		effect.update(delta);
//		ice.update(delta);
		world.update(delta);

		ice.translate(dx, dy);
		ice.setRotation(angle);

		Debug.out(effect.getX() + ":" + effect.getY());

// effect.translate(dx, dy);
		effect.setRotation(angle);
	}

	@Override
	public void onRender (SpriteBatch batch) {

		frameBuffer.begin();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		effect.draw(batch);
// ice.draw(batch);
		batch.end();
		frameBuffer.end();
		Texture tmp = frameBuffer.getColorBufferTexture();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(bg, 0, 0);
// black.draw(batch);
// effect.draw(batch);
// ice.draw(batch);
		batch.draw(tmp, 0, 0);
		ice.draw(batch);
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
		// TODO Auto-generated method stub
		return false;
	}

}
