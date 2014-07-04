
package org.tntstudio.graphics.objs;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

import org.tntstudio.Const;
import org.tntstudio.Const.AnimationMode;
import org.tntstudio.Utils;
import org.tntstudio.graphics.Animator;
import org.tntstudio.utils.math.PolygonList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;

/** @author trungnt13 */
@SuppressWarnings("rawtypes")
public class SpriteAnimation extends SpriteBase implements Animator {
	// ---------------------------------------------------------

	static final int VERTEX_SIZE = 2 + 1 + 2;
	static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

	// ---------------------------------------------------------

	private final float[] mVertices = new float[SPRITE_SIZE];

	private final Color mColor = new Color(1, 1, 1, 1);
	private float x = -100, y = -100;
	float width = 100, height = 100;
	private float originX = 50, originY = 50;
	private float rotation = 0;
	private float scaleX = 1, scaleY = 1;
	private boolean dirty = true;
	private PolygonList mBounds;

	/** Creates an uninitialized sprite. The sprite will need a texture, texture region, bounds, and color set before it can be
	 * drawn. */
	public SpriteAnimation () {
		setColor(1, 1, 1, 1);
	}

	public SpriteAnimation bind (String spriteName, Array<? extends TextureRegion> region) {
		keyFrames = new TextureRegion[region.size];
		for (int i = 0; i < keyFrames.length; i++)
			keyFrames[i] = (TextureRegion)region.get(i);
		setRegion(keyFrames[0]);

		setColor(1, 1, 1, 1);
		setSize(keyFrames[0].getRegionWidth(), keyFrames[0].getRegionHeight());
		setOrigin(width / 2, height / 2);

		setTexture(keyFrames);
		active();

		mBounds = SpriteWorld.getPolygonForSprite(spriteName, width, height);
		return this;
	}

	public SpriteAnimation bind (String spriteName, TextureRegion[] region) {
		keyFrames = region;
		setRegion(region[0]);

		setColor(1, 1, 1, 1);
		setSize(keyFrames[0].getRegionWidth(), keyFrames[0].getRegionHeight());
		setOrigin(width / 2, height / 2);

		setTexture(keyFrames);
		active();

		mBounds = SpriteWorld.getPolygonForSprite(spriteName, width, height);
		return this;
	}

	public SpriteAnimation bind (String spriteName, TextureRegion region) {
		tmpFrames[0] = region;
		keyFrames = tmpFrames;
		setRegion(region);

		setColor(1, 1, 1, 1);
		setSize(keyFrames[0].getRegionWidth(), keyFrames[0].getRegionHeight());
		setOrigin(width / 2, height / 2);

		setTexture(keyFrames);
		active();

		mBounds = SpriteWorld.getPolygonForSprite(spriteName, width, height);
		return this;
	}

	public SpriteAnimation bind (String spriteName, Texture tex) {
		TextureRegion region = new TextureRegion(tex);
		return bind(spriteName, region);
	}

	public SpriteAnimation bind (SpriteAnimation sprite) {
		if (sprite == null) throw new IllegalArgumentException("sprite cannot be null.");
		System.arraycopy(sprite.mVertices, 0, mVertices, 0, SPRITE_SIZE);
		keyFrames = sprite.keyFrames;
		setTexture(keyFrames);
		x = sprite.x;
		y = sprite.y;
		width = sprite.width;
		height = sprite.height;
		originX = sprite.originX;
		originY = sprite.originY;
		rotation = sprite.rotation;
		scaleX = sprite.scaleX;
		scaleY = sprite.scaleY;
		mColor.set(sprite.mColor);
		dirty = sprite.dirty;

		mBounds = sprite.mBounds;
		active();
		return this;
	}

	public SpriteAnimation bind (Array<? extends TextureRegion> region) {
		StringBuilder builder = new StringBuilder();
		for (TextureRegion textureRegion : region) {
			builder.append(Utils.getIdentification(textureRegion));
		}
		return bind(builder.toString(), region);
	}

	public SpriteAnimation bind (TextureRegion[] region) {
		StringBuilder builder = new StringBuilder();
		for (TextureRegion textureRegion : region) {
			builder.append(Utils.getIdentification(textureRegion));
		}
		return bind(builder.toString(), region);
	}

	public SpriteAnimation bind (TextureRegion region) {
		return bind(Utils.getIdentification(region), region);
	}

	public SpriteAnimation bind (Texture tex) {
		return bind(Utils.getIdentification(tex), tex);
	}

	private void setTexture (TextureRegion[] texture) {
		mCurrentTexture = texture[0].getTexture();
	}

	/***********************************************************
	 * 
	 ***********************************************************/

	/** Sets the position and size of the sprite when drawn, before scaling and rotation are applied. If origin, rotation, or scale
	 * are changed, it is slightly more efficient to set the bounds after those operations. */
	public void setBounds (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		if (dirty) return;

		float x2 = x + width;
		float y2 = y + height;
		final float[] vertices = this.mVertices;
		vertices[X1] = x;
		vertices[Y1] = y;

		vertices[X2] = x;
		vertices[Y2] = y2;

		vertices[X3] = x2;
		vertices[Y3] = y2;

		vertices[X4] = x2;
		vertices[Y4] = y;

		if (rotation != 0 || scaleX != 1 || scaleY != 1) dirty = true;
	}

	/** Sets the size of the sprite when drawn, before scaling and rotation are applied. If origin, rotation, or scale are changed,
	 * it is slightly more efficient to set the size after those operations. If both position and size are to be changed, it is
	 * better to use {@link #setBounds(float, float, float, float)}. */
	public void setSize (float width, float height) {
		this.width = width;
		this.height = height;

		if (dirty) return;

		float x2 = x + width;
		float y2 = y + height;
		final float[] vertices = this.mVertices;
		vertices[X1] = x;
		vertices[Y1] = y;

		vertices[X2] = x;
		vertices[Y2] = y2;

		vertices[X3] = x2;
		vertices[Y3] = y2;

		vertices[X4] = x2;
		vertices[Y4] = y;

		if (rotation != 0 || scaleX != 1 || scaleY != 1) dirty = true;
	}

	/** Sets the position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more efficient
	 * to set the position after those operations. If both position and size are to be changed, it is better to use
	 * {@link #setBounds(float, float, float, float)}. */
	public void setPosition (float x, float y) {
		translate(x - this.x, y - this.y);
	}

	/** Sets the x position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more efficient
	 * to set the position after those operations. If both position and size are to be changed, it is better to use
	 * {@link #setBounds(float, float, float, float)}. */
	public void setX (float x) {
		translateX(x - this.x);
	}

	/** Sets the y position where the sprite will be drawn. If origin, rotation, or scale are changed, it is slightly more efficient
	 * to set the position after those operations. If both position and size are to be changed, it is better to use
	 * {@link #setBounds(float, float, float, float)}. */
	public void setY (float y) {
		translateY(y - this.y);
	}

	/** Sets the x position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
	 * changed, it is slightly more efficient to translate after those operations. */
	public void translateX (float xAmount) {
		this.x += xAmount;

		if (dirty) return;

		final float[] vertices = this.mVertices;
		vertices[X1] += xAmount;
		vertices[X2] += xAmount;
		vertices[X3] += xAmount;
		vertices[X4] += xAmount;
	}

	/** Sets the y position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
	 * changed, it is slightly more efficient to translate after those operations. */
	public void translateY (float yAmount) {
		y += yAmount;

		if (dirty) return;

		final float[] vertices = this.mVertices;
		vertices[Y1] += yAmount;
		vertices[Y2] += yAmount;
		vertices[Y3] += yAmount;
		vertices[Y4] += yAmount;
	}

	/** Sets the position relative to the current position where the sprite will be drawn. If origin, rotation, or scale are
	 * changed, it is slightly more efficient to translate after those operations. */
	public void translate (float xAmount, float yAmount) {
		x += xAmount;
		y += yAmount;

		if (dirty) return;

		final float[] vertices = this.mVertices;
		vertices[X1] += xAmount;
		vertices[Y1] += yAmount;

		vertices[X2] += xAmount;
		vertices[Y2] += yAmount;

		vertices[X3] += xAmount;
		vertices[Y3] += yAmount;

		vertices[X4] += xAmount;
		vertices[Y4] += yAmount;
	}

	public void setColor (float alphaModulation) {
		Color color = getColor();
		color.a *= alphaModulation;
		setColor(color);
	}

	public void setColor (Color tint) {
		float color = tint.toFloatBits();
		final float[] vertices = this.mVertices;
		vertices[C1] = color;
		vertices[C2] = color;
		vertices[C3] = color;
		vertices[C4] = color;
	}

	public void setColor (float r, float g, float b, float a) {
		final int intBits = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
		float color = NumberUtils.intToFloatColor(intBits);
		final float[] vertices = this.mVertices;
		vertices[C1] = color;
		vertices[C2] = color;
		vertices[C3] = color;
		vertices[C4] = color;
	}

	/** Sets the origin in relation to the sprite's position for scaling and rotation. */
	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
		dirty = true;
	}

	public void setRotation (float degrees) {
		this.rotation = degrees;
		dirty = true;
	}

	/** Sets the sprite's rotation relative to the current rotation. */
	public void rotate (float degrees) {
		rotation += degrees;
		dirty = true;
	}

	/** Rotates this sprite 90 degrees in-place by rotating the texture coordinates. This rotation is unaffected by
	 * {@link #setRotation(float)} and {@link #rotate(float)}. */
	public void rotate90 (boolean clockwise) {
		final float[] vertices = this.mVertices;

		if (clockwise) {
			float temp = vertices[V1];
			vertices[V1] = vertices[V4];
			vertices[V4] = vertices[V3];
			vertices[V3] = vertices[V2];
			vertices[V2] = temp;

			temp = vertices[U1];
			vertices[U1] = vertices[U4];
			vertices[U4] = vertices[U3];
			vertices[U3] = vertices[U2];
			vertices[U2] = temp;
		} else {
			float temp = vertices[V1];
			vertices[V1] = vertices[V2];
			vertices[V2] = vertices[V3];
			vertices[V3] = vertices[V4];
			vertices[V4] = temp;

			temp = vertices[U1];
			vertices[U1] = vertices[U2];
			vertices[U2] = vertices[U3];
			vertices[U3] = vertices[U4];
			vertices[U4] = temp;
		}
	}

	public void setScale (float scaleXY) {
		this.scaleX = scaleXY;
		this.scaleY = scaleXY;
		dirty = true;
	}

	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		dirty = true;
	}

	/** Sets the sprite's scale relative to the current scale. */
	public void scale (float amount) {
		this.scaleX += amount;
		this.scaleY += amount;
		dirty = true;
	}

	public void flip (boolean x, boolean y) {
		super.flip(x, y);

		for (int i = 0; i < keyFrames.length; i++)
			keyFrames[i].flip(x, y);

		float[] vertices = SpriteAnimation.this.mVertices;
		if (x) {
			float temp = vertices[U1];
			vertices[U1] = vertices[U3];
			vertices[U3] = temp;
			temp = vertices[U2];
			vertices[U2] = vertices[U4];
			vertices[U4] = temp;
		}
		if (y) {
			float temp = vertices[V1];
			vertices[V1] = vertices[V3];
			vertices[V3] = temp;
			temp = vertices[V2];
			vertices[V2] = vertices[V4];
			vertices[V4] = temp;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// animation controller
	// ///////////////////////////////////////////////////////////////
	// TODO: add more animation control here
	/*-------- params --------*/

	TextureRegion[] keyFrames;
	private final TextureRegion[] tmpFrames = new TextureRegion[1];

	float mPlaybackSpeed;
	float mStateTime;

	private int mCurrFrameIdx;

	private int mPlayMode = AnimationMode.NORMAL;

	private Texture mCurrentTexture;

	private boolean isRunning = false;

	/*-------- methdos --------*/

	public float getPlaybackSpeed () {
		return mPlaybackSpeed;
	}

	public Animator setLoop (boolean looping) {
		if (looping) {
			mPlayMode = AnimationMode.LOOP;
		} else {
			mPlayMode = AnimationMode.NORMAL;
		}
		return this;
	}

	@Override
	public Animator setPlaybackSpeed (float playback) {
		mPlaybackSpeed = playback;
		return this;
	}

	public void setKeyFrames (TextureRegion[] keyFrame) {
		this.keyFrames = keyFrame;
		setRegion(keyFrames[0]);
		setTexture(keyFrame);
	}

	public void setKeyFrames (Array keyFrame) {
		keyFrames = new TextureRegion[keyFrame.size];
		for (int i = 0; i < keyFrames.length; i++)
			keyFrames[i] = (TextureRegion)keyFrame.get(i);
		setRegion(keyFrames[0]);
		setTexture(keyFrames);
	}

	@Override
	public boolean isLooping () {
		return mPlayMode == Const.AnimationMode.LOOP || mPlayMode == Const.AnimationMode.LOOP_REVERSED;
	}

	@Override
	public boolean isReversed () {
		return mPlayMode == AnimationMode.LOOP_REVERSED || mPlayMode == AnimationMode.REVERSED;
	}

	public Animator start () {
		start(mStateTime, false);
		return this;
	}

	@Override
	public Animator start (float statetime) {
		start(statetime, false);
		return this;
	}

	@Override
	public Animator start (float statetime, boolean reversed) {
		mStateTime = statetime;
		isRunning = true;
		if (reversed) {
			if (mPlayMode == AnimationMode.LOOP)
				mPlayMode = AnimationMode.LOOP_REVERSED;
			else if (mPlayMode == AnimationMode.NORMAL) mPlayMode = AnimationMode.REVERSED;
		} else {
			if (mPlayMode == AnimationMode.LOOP_REVERSED)
				mPlayMode = AnimationMode.LOOP;
			else if (mPlayMode == AnimationMode.REVERSED) mPlayMode = AnimationMode.NORMAL;
		}
		return this;
	}

	public Animator stop () {
		isRunning = false;
		mStateTime = 0;
		setRegion(keyFrames[0]);
		return this;
	}

	public Animator pause () {
		isRunning = false;
		return this;
	}

	protected void updateInternal (float delta) {
		if (!isRunning || mPlaybackSpeed == 0) return;

		mStateTime += delta;

		mCurrFrameIdx = (int)(mStateTime / mPlaybackSpeed);

		switch (mPlayMode) {
		case AnimationMode.NORMAL:
			mCurrFrameIdx = Math.min(keyFrames.length - 1, mCurrFrameIdx);
			break;
		case AnimationMode.LOOP:
			mCurrFrameIdx = mCurrFrameIdx % keyFrames.length;
			break;
		case AnimationMode.LOOP_PINGPONG:
			mCurrFrameIdx = mCurrFrameIdx % (keyFrames.length * 2);
			if (mCurrFrameIdx >= keyFrames.length) mCurrFrameIdx = keyFrames.length - 1 - (mCurrFrameIdx - keyFrames.length);
			break;
		case AnimationMode.LOOP_RANDOM:
			mCurrFrameIdx = MathUtils.random(keyFrames.length - 1);
			break;
		case AnimationMode.REVERSED:
			mCurrFrameIdx = Math.max(keyFrames.length - mCurrFrameIdx - 1, 0);
			break;
		case AnimationMode.LOOP_REVERSED:
			mCurrFrameIdx = mCurrFrameIdx % keyFrames.length;
			mCurrFrameIdx = keyFrames.length - mCurrFrameIdx - 1;
			break;

		default:
			// play normal otherwise
			mCurrFrameIdx = Math.min(keyFrames.length - 1, mCurrFrameIdx);
			break;
		}

		setRegion(keyFrames[mCurrFrameIdx]);
	}

	public int getCurrFrameIdx () {
		return mCurrFrameIdx;
	}

	public TextureRegion[] getFrames () {
		return this.keyFrames;
	}

	@Override
	public boolean isRunning () {
		return isRunning;
	}

	public boolean isAnimationFinished () {
		int frameNumber = (int)(mStateTime / mPlaybackSpeed);
		return keyFrames.length - 1 < frameNumber;
	}

	@Override
	public float getStateTime () {
		return mStateTime;
	}

	@Override
	public float getTotalDuration () {
		return keyFrames.length * mPlaybackSpeed;
	}

	/***********************************************************
	 * 
	 ***********************************************************/

	/** Returns the packed vertices, colors, and texture coordinates for this sprite. */
	public float[] getVertices () {
		if (dirty) {
			dirty = false;

			float[] vertices = this.mVertices;
			float localX = -originX;
			float localY = -originY;
			float localX2 = localX + width;
			float localY2 = localY + height;
			float worldOriginX = this.x - localX;
			float worldOriginY = this.y - localY;
			if (scaleX != 1 || scaleY != 1) {
				localX *= scaleX;
				localY *= scaleY;
				localX2 *= scaleX;
				localY2 *= scaleY;
			}
			if (rotation != 0) {
				final float cos = MathUtils.cosDeg(rotation);
				final float sin = MathUtils.sinDeg(rotation);
				final float localXCos = localX * cos;
				final float localXSin = localX * sin;
				final float localYCos = localY * cos;
				final float localYSin = localY * sin;
				final float localX2Cos = localX2 * cos;
				final float localX2Sin = localX2 * sin;
				final float localY2Cos = localY2 * cos;
				final float localY2Sin = localY2 * sin;

				final float x1 = localXCos - localYSin + worldOriginX;
				final float y1 = localYCos + localXSin + worldOriginY;
				vertices[X1] = x1;
				vertices[Y1] = y1;

				final float x2 = localXCos - localY2Sin + worldOriginX;
				final float y2 = localY2Cos + localXSin + worldOriginY;
				vertices[X2] = x2;
				vertices[Y2] = y2;

				final float x3 = localX2Cos - localY2Sin + worldOriginX;
				final float y3 = localY2Cos + localX2Sin + worldOriginY;
				vertices[X3] = x3;
				vertices[Y3] = y3;

				vertices[X4] = x1 + (x3 - x2);
				vertices[Y4] = y3 - (y2 - y1);
			} else {
				final float x1 = localX + worldOriginX;
				final float y1 = localY + worldOriginY;
				final float x2 = localX2 + worldOriginX;
				final float y2 = localY2 + worldOriginY;

				vertices[X1] = x1;
				vertices[Y1] = y1;

				vertices[X2] = x1;
				vertices[Y2] = y2;

				vertices[X3] = x2;
				vertices[Y3] = y2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
			}
		}
		return mVertices;
	}

	/** Returns the bounding axis aligned {@link Rectangle} that bounds this sprite. The rectangles x and y coordinates describe its
	 * bottom left corner. If you change the position or size of the sprite, you have to fetch the triangle again for it to be
	 * recomputed.
	 * 
	 * @return the bounding Rectangle */
	public Array<FloatArray> getBounding () {
		mBounds.apply(this);
		return mBounds.getTransformedVertices();
	}

	protected void drawInternal (Batch spriteBatch) {
		spriteBatch.draw(mCurrentTexture, getVertices(), 0, SPRITE_SIZE);
	}

	/***********************************************************
	 * 
	 ***********************************************************/

	public float getX () {
		return x;
	}

	public float getCenterX () {
		final float[] vertices = getVertices();

		float minx = vertices[X1];
		float maxx = vertices[X1];

		minx = minx > vertices[X2] ? vertices[X2] : minx;
		minx = minx > vertices[X3] ? vertices[X3] : minx;
		minx = minx > vertices[X4] ? vertices[X4] : minx;

		maxx = maxx < vertices[X2] ? vertices[X2] : maxx;
		maxx = maxx < vertices[X3] ? vertices[X3] : maxx;
		maxx = maxx < vertices[X4] ? vertices[X4] : maxx;

		return (minx + maxx) / 2;
	}

	public float getY () {
		return y;
	}

	public float getCenterY () {
		final float[] vertices = getVertices();

		float miny = vertices[Y1];
		float maxy = vertices[Y1];

		miny = miny > vertices[Y2] ? vertices[Y2] : miny;
		miny = miny > vertices[Y3] ? vertices[Y3] : miny;
		miny = miny > vertices[Y4] ? vertices[Y4] : miny;

		maxy = maxy < vertices[Y2] ? vertices[Y2] : maxy;
		maxy = maxy < vertices[Y3] ? vertices[Y3] : maxy;
		maxy = maxy < vertices[Y4] ? vertices[Y4] : maxy;

		return (miny + maxy) / 2;
	}

	public float getWidth () {
		return width;
	}

	public float getHeight () {
		return height;
	}

	public float getOriginX () {
		return originX;
	}

	public float getOriginY () {
		return originY;
	}

	public float getRotation () {
		return rotation;
	}

	public float getScaleX () {
		return scaleX;
	}

	public float getScaleY () {
		return scaleY;
	}

	/** Returns the color of this sprite. Changing the returned color will have no affect, {@link #setColor(Color)} or
	 * {@link #setColor(float, float, float, float)} must be used. */
	public Color getColor () {
		final int intBits = NumberUtils.floatToIntColor(mVertices[C1]);
		final Color color = this.mColor;
		color.r = (intBits & 0xff) / 255f;
		color.g = ((intBits >>> 8) & 0xff) / 255f;
		color.b = ((intBits >>> 16) & 0xff) / 255f;
		color.a = ((intBits >>> 24) & 0xff) / 255f;
		return color;
	}

	private void setRegion (TextureRegion region) {
		this.mCurrentTexture = region.getTexture();

		final float u = region.getU();
		final float v = region.getV();
		final float u2 = region.getU2();
		final float v2 = region.getV2();

		final float[] vertices = SpriteAnimation.this.mVertices;

		vertices[U1] = u;
		vertices[V1] = v2;

		vertices[U2] = u;
		vertices[V2] = v;

		vertices[U3] = u2;
		vertices[V3] = v;

		vertices[U4] = u2;
		vertices[V4] = v2;
	}

	public void scroll (float xAmount, float yAmount) {

		final float[] vertices = SpriteAnimation.this.mVertices;
		if (xAmount != 0) {
			final float u = (vertices[U1] + xAmount) % 1;
			final float u2 = u + width / mCurrentTexture.getWidth();
			vertices[U1] = u;
			vertices[U2] = u;
			vertices[U3] = u2;
			vertices[U4] = u2;
		}
		if (yAmount != 0) {
			final float v = (vertices[V2] + yAmount) % 1;
			final float v2 = v + height / mCurrentTexture.getHeight();
			vertices[V1] = v2;
			vertices[V2] = v;
			vertices[V3] = v;
			vertices[V4] = v2;
		}
	}

	public boolean hit (float x, float y) {
		if (x >= getX() && x <= (getX() + getWidth()) && y >= getY() && y <= (getY() + getHeight())) {
			return true;
		}
		return false;
	}

	@Override
	protected void resetInternal () {
		stop();
		setPosition(0, 0);
		setSize(0, 0);
		setOrigin(0, 0);
		rotation = 0;
		scaleX = 1;
		scaleY = 1;
		dirty = false;
		setColor(1, 1, 1, 1);

		clearUpdater();
	}
}
