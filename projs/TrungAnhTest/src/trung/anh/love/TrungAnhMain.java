
package trung.anh.love;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TrungAnhMain implements ApplicationListener {
	public static void main (String[] args) {
		new LwjglApplication(new TrungAnhMain(), "TrungLoveAnh", 450, 800);
	}

	Texture tex;
	Sprite trunganh;
	SpriteBatch batch;
	ParticleEffect effect;

	int width = 0;
	int height = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		tex = new Texture(Gdx.files.internal("data/TrungAnh.png"));
		trunganh = new Sprite(tex);
		float hwratio = (float)tex.getHeight() / (float)tex.getWidth();
		float trunganhWidth = Gdx.graphics.getWidth() / 1.3f;
		trunganh.setSize(trunganhWidth, hwratio * trunganhWidth);

		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("data/TrungAnh.p"), Gdx.files.internal("data/"));
		effect.setPosition(Gdx.graphics.getWidth() / 2 - effect.getEmitters().get(0).getXOffsetValue().getLowMin(), 0);
		effect.start();
	}

	@Override
	public void resize (int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
		effect.update(Gdx.graphics.getDeltaTime());

		trunganh.setPosition(width / 2 - trunganh.getWidth() / 2, height / 2 - trunganh.getHeight() / 4);
		batch.begin();
		trunganh.draw(batch);
		effect.draw(batch);
		batch.end();
	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}

}
