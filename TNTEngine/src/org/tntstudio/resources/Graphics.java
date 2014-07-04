
package org.tntstudio.resources;

import org.tntstudio.Const.Orientation;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.Frame;
import org.tntstudio.graphics.FrameObject;
import org.tntstudio.interfaces.FrameRenderer;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.resources.Audio.AudioCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public final class Graphics implements LifecycleListener {
	static Graphics CurrentGraphics = null;

	/*-------- drawer --------*/
	private SpriteBatch mSpriteBatch;
	private SpriteCache mSpriteCache;
	private ShapeRenderer mShapeRenderer;
	private Frame mFrame;

	/*-------- game size --------*/
	private float mGameWidth = Gdx.graphics.getWidth();
	private float mGameHeight = Gdx.graphics.getHeight();
	private int mOrientation = mGameWidth > mGameHeight ? Orientation.LANDSCAPE : Orientation.PORTRAIT;

	/*-------- frameobj manage --------*/
	private ObjectMap<String, FrameObject> mFrameObjectMap = new ObjectMap<String, FrameObject>();

	/*-------- creator --------*/
	public final GraphicsCreator Creator = new GraphicsCreator();

	public Graphics () {
		CurrentGraphics = this;
	}

	public class GraphicsCreator {
		private GraphicsCreator () {
		}

		public AudioCreator initGraphics (float gameWidth, float gameHeight, int spriteBatchSize, boolean isUseFrame,
			float frameWidth, float frameHeight, boolean isKeepAspectRatio) {
			mGameWidth = gameWidth;
			mGameHeight = gameHeight;
			mOrientation = mGameWidth > mGameHeight ? Orientation.LANDSCAPE : Orientation.PORTRAIT;

			if (spriteBatchSize > 0) mSpriteBatch = new SpriteBatch(spriteBatchSize);

			if (isUseFrame) mFrame = new Frame(frameWidth, frameHeight, isKeepAspectRatio);

			return Audio.CurrentAudio.Creator;
		}

		public AudioCreator initGraphics (float gameWidth, float gameHeight, int spriteBatchSize, boolean isUseFrame,
			float frameWidth, float frameHeight, boolean isKeepAspectRatio, int spriteCacheSize, int shapeRendererSize,
			ShaderProgram spriteBatchShader, ShaderProgram spriteCacheShader) {

			// game width and height
			mGameWidth = gameWidth;
			mGameHeight = gameHeight;
			mOrientation = mGameWidth > mGameHeight ? Orientation.LANDSCAPE : Orientation.PORTRAIT;

			// init sprite batch
			if (spriteBatchSize > 0) {
				if (Gdx.graphics.isGL20Available() && spriteBatchShader != null)
					mSpriteBatch = new SpriteBatch(spriteBatchSize, spriteBatchShader);
				else
					mSpriteBatch = new SpriteBatch(spriteBatchSize);
			}

			// frame
			if (isUseFrame) mFrame = new Frame(frameWidth, frameHeight, isKeepAspectRatio);

			// sprite cache
			if (spriteCacheSize > 0) {
				if (Gdx.graphics.isGL20Available() && spriteCacheShader != null)
					mSpriteCache = new SpriteCache(spriteCacheSize, spriteCacheShader, false);
				else
					mSpriteCache = new SpriteCache(spriteCacheSize, false);
			} else
				mSpriteCache = null;

			// shaperenderer
			if (shapeRendererSize > 0) {
				mShapeRenderer = new ShapeRenderer(shapeRendererSize);
			} else
				mShapeRenderer = null;

			return Audio.CurrentAudio.Creator;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// frameobject manage
	// ///////////////////////////////////////////////////////////////
	public Texture useFrameObj (Format format, int width, int height, boolean depth, FrameRenderer... renderers) {
		String key = getKeyNameOfFrameObj(format, width, height, depth);
		FrameObject fo = mFrameObjectMap.get(key);
		if (fo == null) {
			fo = new FrameObject(format, width, height, depth);
			mFrameObjectMap.put(key, fo);
		}
		fo.bindRenderer(renderers);
		return fo.getColorBufferTexture();
	}

	public ObjectMap<String, FrameObject> getFrameObjectMap () {
		return mFrameObjectMap;
	}

	public void manageFrameObj (FrameObject obj) {
		String key = getKeyNameOfFrameObj(obj);
		FrameObject old = mFrameObjectMap.get(key);
		if (obj != null) old.dispose();

		mFrameObjectMap.put(key, obj);
	}

	public void deleteFrameObj (FrameObject obj) {
		String key = getKeyNameOfFrameObj(obj);
		mFrameObjectMap.remove(key);
		if (!obj.isDisposed()) obj.dispose();
	}

	private String getKeyNameOfFrameObj (FrameObject obj) {
		return "Frame:" + obj.getFormat() + obj.getWidth() + obj.getHeight() + obj.isDepth();
	}

	private String getKeyNameOfFrameObj (Format format, int width, int height, boolean depth) {
		return "Frame:" + format + width + height + depth;
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	public float getGameWidth () {
		return mGameWidth;
	}

	public float getGameHeight () {
		return mGameHeight;
	}

	public int getOrientation () {
		return mOrientation;
	}

	public SpriteBatch spriteBatch () {
		return mSpriteBatch;
	}

	public SpriteCache spriteCache () {
		if (mSpriteCache == null) mSpriteCache = new SpriteCache();
		return mSpriteCache;
	}

	public ShapeRenderer shapeRenderer () {
		if (mShapeRenderer == null) mShapeRenderer = new ShapeRenderer();
		return mShapeRenderer;
	}

	public Frame frame () {
		return mFrame;
	}

	// ///////////////////////////////////////////////////////////////
	// Override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void dispose () {
		mSpriteBatch.dispose();
		if (mSpriteCache != null) mSpriteCache.dispose();
		if (mShapeRenderer != null) mShapeRenderer.dispose();
		if (mFrame != null) mFrame.dispose();

		for (FrameObject fo : mFrameObjectMap.values())
			fo.dispose();
		mFrameObjectMap.clear();
		
		//this is important, clear last static data
		CurrentGraphics = null;
	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////////////////////
	// static methods
	// ///////////////////////////////////////////////////////////////
	public static TextureRegion[] split (Texture texture, int regionNumber, int tileWidth, int tileHeight, int padding) {
		TextureRegion tmp = new TextureRegion(texture);
		int x = tmp.getRegionX();
		int y = tmp.getRegionY();
		int width = tmp.getRegionWidth();
		int height = tmp.getRegionHeight();

		int pad = 0;

		if (padding > 0) pad = padding;

		if (width < 0) {
			x = x - width;
			width = -width;
		}

		if (height < 0) {
			y = y - height;
			height = -height;
		}
		/** Row is a int so when i devide like this the excessive area will be dismiss . But remember the excessive area must be
		 * smaller than tile width and tile height */
		int rows = (height + pad) / (tileHeight + pad);
		int cols = (width + pad) / (tileWidth + pad);

		TextureRegion[] tiles = new TextureRegion[regionNumber];

		int count = 0;
		int startX = x;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (count < regionNumber) {
					tiles[count] = new TextureRegion(texture, x, y, tileWidth, tileHeight);
					count++;
				}
				x += tileWidth + pad;
			}
			x = startX;
			y += tileHeight + pad;
		}

		return tiles;
	}

	public static TextureRegion[] split (TextureRegion region, int totalRegionNumber, int numberColumns, int numberRows,
		int padding) {
		Texture texture = region.getTexture();

		TextureRegion[] tmp = new TextureRegion[totalRegionNumber];
		int count = 0;

		final int firstX = region.getRegionX();
		final int firstY = region.getRegionY();

		int tileWidth = (region.getRegionWidth() - (padding * (numberColumns - 1))) / numberColumns;
		int tileHeight = (region.getRegionHeight() - (padding * (numberRows - 1))) / numberRows;

		for (int i = 0; i < numberRows; i++) {
			for (int j = 0; j < numberColumns; j++) {
				tmp[count] = new TextureRegion(texture, firstX + (j * (tileWidth + padding)), firstY + (i * (tileHeight + padding)),
					tileWidth, tileHeight);
				count++;
				if (count >= totalRegionNumber) return tmp;
			}
		}
		return tmp;
	}

	public static TextureRegion[] regionConvert (Array<AtlasRegion> regions) {
		if (!(regions.get(0) instanceof TextureRegion)) throw new TNTRuntimeException("Your List not instance of TextureRegion");
		TextureRegion[] tmp = new TextureRegion[regions.size];
		for (int i = 0, n = regions.size; i < n; i++) {
			tmp[i] = (TextureRegion)regions.get(i);
		}
		return tmp;
	}
}
