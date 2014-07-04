
package org.tntstudio.graphics.objs;

import org.tntstudio.Utils;
import org.tntstudio.graphics.Animator;
import org.tntstudio.utils.math.PolygonList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

public class SpriteParticle extends SpriteBase implements Animator {
	// ///////////////////////////////////////////////////////////////
	// map for caching particle emitters
	// ///////////////////////////////////////////////////////////////
	static final ObjectMap<String, Array<ParticleEmitter>> mParticleCachingMap = new ObjectMap<String, Array<ParticleEmitter>>();

	static void ClearOldData () {
		Values<Array<ParticleEmitter>> list = mParticleCachingMap.values();
		for (Array<ParticleEmitter> a : list)
			a.clear();
	}

	/*-------- sprite params --------*/
	private float x, y;
	private float width, height;
	private float rotation;
	private float scaleX, scaleY;
	private float originX, originY;

	/*-------- params --------*/

	private final ParticleEffect effect;
	private PolygonList mBounds;

	public SpriteParticle () {
		effect = new ParticleEffect();
	}

	public SpriteParticle bind (String boundName, ParticleEmitter... emitters) {
		final Array<ParticleEmitter> mainList = effect.getEmitters();
		mainList.clear();

		for (ParticleEmitter e : emitters) {
			Array<ParticleEmitter> list = mParticleCachingMap.get(getNameOfParticleEmitter(e));
			if (list == null)
				mainList.add(new ParticleEmitter(e));
			else {
				ParticleEmitter cached = list.pop();
				mainList.add(cached);
			}
		}

		mBounds = SpriteWorld.getPolygonForSprite(boundName, 1, 1);
		return this;
	}

	public SpriteParticle bind (String boundName, ParticleEffect copyEffect) {
		final Array<ParticleEmitter> mainList = effect.getEmitters();
		final Array<ParticleEmitter> emitters = copyEffect.getEmitters();
		mainList.clear();

		for (ParticleEmitter e : emitters) {
			Array<ParticleEmitter> list = mParticleCachingMap.get(getNameOfParticleEmitter(e));
			if (list == null)
				mainList.add(new ParticleEmitter(e));
			else {
				ParticleEmitter cached = list.pop();
				mainList.add(cached);
			}
		}

		mBounds = SpriteWorld.getPolygonForSprite(boundName, 1, 1);
		return this;
	}

	public SpriteParticle bind (ParticleEffect copyEffect) {
		final Array<ParticleEmitter> mainList = effect.getEmitters();
		final Array<ParticleEmitter> emitters = copyEffect.getEmitters();
		mainList.clear();

		for (ParticleEmitter e : emitters) {
			Array<ParticleEmitter> list = mParticleCachingMap.get(getNameOfParticleEmitter(e));
			if (list == null)
				mainList.add(new ParticleEmitter(e));
			else {
				ParticleEmitter cached = list.pop();
				mainList.add(cached);
			}
		}

		mBounds = SpriteWorld.getPolygonForSprite(Utils.getIdentification(copyEffect), 1, 1);
		return this;
	}

	public SpriteParticle bind (ParticleEmitter... emitters) {
		final Array<ParticleEmitter> mainList = effect.getEmitters();
		StringBuilder builder = new StringBuilder();
		mainList.clear();

		for (ParticleEmitter e : emitters) {
			Array<ParticleEmitter> list = mParticleCachingMap.get(getNameOfParticleEmitter(e));
			if (list == null)
				mainList.add(new ParticleEmitter(e));
			else {
				ParticleEmitter cached = list.pop();
				mainList.add(cached);
			}
			builder.append(Utils.getIdentification(e));
		}

		mBounds = SpriteWorld.getPolygonForSprite(builder.toString(), 1, 1);
		return this;
	}

	private final String getNameOfParticleEmitter (ParticleEmitter e) {
		return e.getName() + e.getImagePath();
	}

	// ///////////////////////////////////////////////////////////////
	// processor
	// ///////////////////////////////////////////////////////////////

	@Override
	protected void updateInternal (float delta) {
		super.update(delta);
		if (isRunning) {
			effect.update(mPlaybackSpeed * mReversed * delta);
		} else {
			if (!effect.isComplete())
				effect.update(mPlaybackSpeed * mReversed * delta);
			else
				effect.reset();
		}
	}

	@Override
	protected void drawInternal (Batch batch) {
		super.draw(batch);
		effect.draw(batch);
	}

	@Override
	protected void disposeInternal () {
		effect.getEmitters().clear();
	}

	@Override
	protected void resetInternal () {
		final Array<ParticleEmitter> list = effect.getEmitters();
		for (ParticleEmitter e : list) {
			e.reset();

			Array<ParticleEmitter> cached = mParticleCachingMap.get(getNameOfParticleEmitter(e));
			if (cached == null) {
				cached = new Array<ParticleEmitter>();
				mParticleCachingMap.put(getNameOfParticleEmitter(e), cached);
			}
			cached.add(e);
		}
		list.clear();
	}

	// ///////////////////////////////////////////////////////////////
	// setter
	// ///////////////////////////////////////////////////////////////

	@Override
	public void flip (boolean flipX, boolean flipY) {
		super.flip(flipX, flipY);
		effect.setFlip(isFlipX(), isFlipY());
	}

	@Override
	public void setBounds (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		effect.setPosition(x, y);
	}

	@Override
	public void setSize (float width, float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		effect.setPosition(x, y);
	}

	@Override
	public void setX (float x) {
		this.x = x;
		effect.setPosition(x, y);
	}

	@Override
	public void setY (float y) {
		this.y = y;
		effect.setPosition(x, y);
	}

	@Override
	public void translate (float xAmount, float yAmount) {
		this.x += xAmount;
		this.y += yAmount;
		effect.setPosition(x, y);

	}

	@Override
	public void translateX (float xAmount) {
		this.x += xAmount;
		effect.setPosition(x, y);
	}

	@Override
	public void translateY (float yAmount) {
		this.y += yAmount;
		effect.setPosition(x, y);
	}

	/** Default 0,0. Can't change */
	@Override
	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
	}

	@Override
	public void setRotation (float degree) {
		this.rotation = degree;
		for (ParticleEmitter emiter : effect.getEmitters()) {
			emiter.getAngle().setLow(degree);
			emiter.getAngle().setHigh(degree);
		}
	}

	@Override
	public void rotate (float degree) {
		this.rotation += degree;
		for (ParticleEmitter emiter : effect.getEmitters()) {
			ScaledNumericValue angle = emiter.getAngle();
			float everage = (angle.getHighMin() + angle.getHighMax()) / 2;
			angle.setLow(everage + degree);
			angle.setHigh(everage + degree);
		}
	}

	@Override
	public void setScale (float scaleXY) {
		this.scaleX = scaleXY;
		this.scaleY = scaleXY;

		for (ParticleEmitter emiter : effect.getEmitters()) {
			ScaledNumericValue scale = emiter.getScale();
			scale.setLow(scaleXY);
			scale.setHigh(scaleXY);
		}
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public void scale (float amount) {
		this.scaleX += amount;
		this.scaleY += amount;

		for (ParticleEmitter emiter : effect.getEmitters()) {
			ScaledNumericValue scale = emiter.getScale();
			float everage = (scale.getHighMin() + scale.getHighMax()) / 2;
			scale.setLow(everage + amount);
			scale.setHigh(everage + amount);
		}
	}

	@Override
	public void setColor (float r, float g, float b, float a) {
	}

	@Override
	public void setColor (float a) {
	}

	@Override
	public void setColor (Color color) {
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	@Override
	public float[] getVertices () {
		return null;
	}

	@Override
	public Array<FloatArray> getBounding () {
		mBounds.apply(this);
		return mBounds.getTransformedVertices();
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public float getCenterX () {
		return x;
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public float getCenterY () {
		return y;
	}

	@Override
	public float getWidth () {
		return width;
	}

	@Override
	public float getHeight () {
		return height;
	}

	@Override
	public float getOriginX () {
		return originX;
	}

	@Override
	public float getOriginY () {
		return originY;
	}

	@Override
	public float getRotation () {
		return rotation;
	}

	@Override
	public float getScaleX () {
		return scaleX;
	}

	@Override
	public float getScaleY () {
		return scaleY;
	}

	@Override
	public Color getColor () {
		return null;
	}

	public ParticleEffect getParticle () {
		return effect;
	}

	// ///////////////////////////////////////////////////////////////
	// animator
	// ///////////////////////////////////////////////////////////////
	float mPlaybackSpeed;
	private boolean isRunning = false;
	private int mReversed = 1;

	@Override
	public Animator setPlaybackSpeed (float playback) {
		mPlaybackSpeed = playback;
		return this;
	}

	@Override
	public Animator setLoop (boolean loop) {
		final Array<ParticleEmitter> list = effect.getEmitters();
		for (ParticleEmitter e : list) {
			e.setContinuous(loop);
			if (!loop) e.allowCompletion();
		}
		return this;
	}

	@Override
	public Animator start (float statetime, boolean reversed) {
		isRunning = true;
		if (reversed)
			mReversed = -1;
		else
			mReversed = 1;

		effect.start();
		return this;
	}

	@Override
	public Animator start (float statetime) {
		return start(statetime, false);
	}

	@Override
	public Animator start () {
		return start(0, false);
	}

	@Override
	public Animator stop () {
		isRunning = false;
		effect.allowCompletion();

		return this;
	}

	@Override
	public Animator pause () {
		isRunning = false;
		return this;
	}

	@Override
	public boolean isRunning () {
		return isRunning;
	}

	@Override
	public boolean isLooping () {
		final Array<ParticleEmitter> list = effect.getEmitters();
		for (ParticleEmitter e : list) {
			if (!e.isContinuous()) return false;
		}
		return true;
	}

	@Override
	public boolean isReversed () {
		return mReversed == -1;
	}

	@Override
	public float getPlaybackSpeed () {
		return mPlaybackSpeed;
	}

	@Override
	public float getStateTime () {
		return 0;
	}

	@Override
	public float getTotalDuration () {
		float maxDuration = 0;
		float curDuration = 0;
		final Array<ParticleEmitter> list = effect.getEmitters();
		for (ParticleEmitter e : list) {
			curDuration = e.getDuration().getLowMax() + e.getDelay().getLowMax();
			if (curDuration > maxDuration) maxDuration = curDuration;
		}
		return maxDuration;
	}
}
