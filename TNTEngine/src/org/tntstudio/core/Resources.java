
package org.tntstudio.core;

import java.util.ArrayList;

import org.tntstudio.graphics.FrameObject;
import org.tntstudio.interfaces.AssetsListener;
import org.tntstudio.interfaces.FrameRenderer;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.interfaces.PackLoadedListener;
import org.tntstudio.resources.Assets;
import org.tntstudio.resources.Assets.AssetsCreator;
import org.tntstudio.resources.Audio;
import org.tntstudio.resources.Graphics;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap.Values;

public class Resources implements LifecycleListener {
	final Graphics mGraphics;
	final Audio mAudio;
	final Assets mAssets;

	final ResourcesCreator Creator = new ResourcesCreator();

	Resources () {
		mGraphics = new Graphics();
		mAudio = new Audio();
		mAssets = new Assets();
		Top.tgame.registerLifecycleListener(this);
	}

	public class ResourcesCreator {
		private ResourcesCreator () {

		}

		public AssetsCreator initResources () {

			return mAssets.Creator;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// graphics methods
	// ///////////////////////////////////////////////////////////////

	public void gValidateScreenSize (int width, int height) {

	}

	public Texture gUseFrameBuffer (FrameRenderer... renderers) {
		return mGraphics.useFrameObj(Format.RGBA8888, (int)Top.gameWidth(), (int)Top.gameHeight(), false, renderers);
	}

	public Texture gUseFrameBuffer (int width, int height, FrameRenderer... renderers) {
		return mGraphics.useFrameObj(Format.RGBA8888, width, height, false, renderers);
	}

	public Texture gUseFrameBuffer (Format format, int width, int height, FrameRenderer... renderers) {
		return mGraphics.useFrameObj(format, width, height, false, renderers);
	}

	public Texture gUseFrameBuffer (Format format, int width, int height, boolean depth, FrameRenderer... renderers) {
		return mGraphics.useFrameObj(format, width, height, depth, renderers);
	}

	public void gManageFrameObj (FrameObject obj) {
		mGraphics.manageFrameObj(obj);
	}

	public void gDeleteFrameBuffer (FrameObject obj) {
		mGraphics.deleteFrameObj(obj);
	}

	public SpriteCache gSpriteCache () {
		return mGraphics.spriteCache();
	}

	public ShapeRenderer gShapeRenderer () {
		return mGraphics.shapeRenderer();
	}

	public SpriteBatch gSpriteBatch () {
		return mGraphics.spriteBatch();
	}

	public Frame gFrame () {
		return mGraphics.frame();
	}

	// ///////////////////////////////////////////////////////////////
	// audio methods
	// ///////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////
	// raw methods
	// ///////////////////////////////////////////////////////////////
	void rRegisterPackLoadedListener (Screen screen, PackLoadedListener listener) {
		mAssets.registerPackListener(screen, listener);
	}

	void rUnregisterPackLoadedListener (Screen screen) {
		mAssets.unregisterPackListener(screen);
	}

	/** Start loading process until loading done */
	void rStartLoadingProcess (AssetsListener listener, ArrayList<String> unload, ArrayList<String> load) {
		mAssets.startLoadingProcess(listener, unload, load);
	}

	/** Unload and remove pack */
	void rDeletePack (Screen screen) {
		mAssets.deletePack(screen);
	}

	public boolean isScreenResourcesLoaded (Screen screen) {
		return mAssets.isScreenLoaded(screen);
	}

	public boolean isScreenResourcesLoaded (String screen) {
		return mAssets.isScreenLoaded(screen);
	}

	@SuppressWarnings("rawtypes")
	public boolean isScreenResourceLoaded (Class clazz) {
		return mAssets.isScreenLoaded(clazz.getSimpleName());
	}

	public <T> T rGet (String fileName, Class<T> type) {
		return mAssets.get(fileName, type);
	}

	public void rUnload (String fileName) {
		mAssets.unload(fileName);
	}

	public <T> boolean isLoaded (String fileName, Class<T> type) {
		return mAssets.isLoaded(fileName, type);
	}

	// ///////////////////////////////////////////////////////////////
	// file methods
	// ///////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////
	// lifceycle
	// ///////////////////////////////////////////////////////////////

	void updateAssets (float delta) {
		mAssets.update();
	}

	void renderGraphics () {
		final Values<FrameObject> set = mGraphics.getFrameObjectMap().values();
		for (FrameObject frameObject : set)
			frameObject.getColorBufferTexture();
	}

	@Override
	public void dispose () {
		mGraphics.dispose();
		mAudio.dispose();
		mAssets.dispose();
	}

	@Override
	public void resume () {
		mGraphics.resume();
		mAudio.resume();
		mAssets.resume();
	}

	@Override
	public void pause () {
		mGraphics.pause();
		mAudio.pause();
		mAssets.pause();
	}
}
