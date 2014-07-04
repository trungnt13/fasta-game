
package test.screen;

import org.tntstudio.Debug;
import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.Input;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.objs.SpriteBox;
import org.tntstudio.graphics.objs.SpriteWorld;
import org.tntstudio.graphics.objs.SpriteBox.Box2DCreater;
import org.tntstudio.lua.LuaScript;
import org.tntstudio.resources.Graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class SpriteBoxTestScreen extends BaseScreen {

	World world;
	Array<SpriteBox> boxs;
	Box2DDebugRenderer debug;
	final LuaScript script = Top.tlua;
	boolean isUseNativeCreator = false;
	SpriteWorld world1 = SpriteWorld.newWorld("Test");

	private final Box2DCreater mBox2DCreator = new Box2DCreater() {

		@Override
		public FixtureDef initFixtureDef (Shape shape) {
			FixtureDef def = new FixtureDef();
			def.density = 5f;
			def.restitution = 0.2f;
			def.shape = shape;
			return def;
		}

		@Override
		public BodyDef initBodyDef (float rX, float rY) {
			BodyDef def = new BodyDef();
			def.type = BodyType.DynamicBody;
			def.position.set(rX, rY);
			return def;
		}

		@Override
		public Shape initShape (float width, float height) {
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(width / 2, height / 2, new Vector2(-width / 2, 0), 0);
			return shape;
		}

		@Override
		public Body attachFixture (World world, Body body, FixtureDef fixtureDef) {
			body.createFixture(fixtureDef);

// FixtureDef fd = new FixtureDef();
// fd.density = 20f;
// fd.friction = 0.2f;
// PolygonShape shape = new PolygonShape();
// shape.setAsBox(0.11f, 0.11f);
// fd.shape = shape;
// body.createFixture(fd);
			return body;
		}

	};

	private Box2DCreater creator = null;

	@Override
	public void onCreate () {
		/*--------  create lua --------*/
		script.loadFile("data/script/spriteBox2D.lua");
		Debug.out(script.toString());
		if (isUseNativeCreator)
			creator = mBox2DCreator;
		else
			creator = script.getProxy("box2D", Box2DCreater.class);

		/*-------- create box2D obj --------*/

		world = new World(new Vector2(0, -9.8f), true);
		debug = new Box2DDebugRenderer();
		vpSetOrtho2D(0, 0, 12.8f, 7.2f, false);

		boxs = new Array<SpriteBox>();

		bindInputCallback(new Input.MotionCallback() {

			@Override
			public boolean onTouchUp (float x, float y, int pointer, int button) {
				SpriteBox spriteBox = world1.newSprite(SpriteBox.class);

				TextureRegion[] regions = Graphics.split(new TextureRegion(Top.tres.rGet("data/firearrow.png", Texture.class)), 13,
					13, 1, 0);
				spriteBox.bind(regions);
				spriteBox.setBounds(1, 2, spriteBox.getWidth() * 0.01f / 3f, spriteBox.getHeight() * 0.01f / 3f);
				spriteBox.setOrigin(spriteBox.getWidth(), spriteBox.getHeight() / 2);
				spriteBox.bindBox(world, creator);

				spriteBox.body().setTransform(spriteBox.body().getPosition(), 45f * MathUtils.degreesToRadians);
				spriteBox.body().applyLinearImpulse(new Vector2(6f, 6f), new Vector2(-0.2f, 0.1f), true);
				spriteBox.setLoop(true);
				spriteBox.setPlaybackSpeed(0.1f);
				spriteBox.start();

				boxs.add(spriteBox);
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

		// Init Ground
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(6.4f, 0f);

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.density = 5f;
			fixtureDef.restitution = 0.2f;
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(6.4f, 0.1f);
			fixtureDef.shape = shape;

			Body groundBody = world.createBody(bodyDef);
			groundBody.createFixture(fixtureDef);
		}// end Init Ground

	}

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdate (float delta) {
		world.step(delta, 6, 2);
		for (SpriteBox box : boxs) {
			box.update(delta);
		}
	}

	@Override
	public void onRender (SpriteBatch batch) {
		batch.begin();
		for (SpriteBox box : boxs) {
			box.draw(batch);
		}
		batch.end();
		debug.render(world, vpCombined());
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
