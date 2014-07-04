
package org.tntstudio.graphics;

import org.tntstudio.core.Top;
import org.tntstudio.interfaces.FrameRenderer;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

/** @author trungnt13 */
public final class FrameObject extends FrameBuffer {
	private final ViewportSize mViewportSize = new ViewportSize();
	private final Array<FrameRenderer> mFrameRenderer = new Array<FrameRenderer>();
	private boolean isDisposed = false;
	private final Format mFormat;
	private final boolean isDepth;

	public FrameObject (Format format, int width, int height, boolean hasDepth) {
		super(format, width, height, hasDepth);
		mFormat = format;
		isDepth = hasDepth;
		mViewportSize.setDimension(0, 0, width, height);
	}

	public FrameObject (Format format) {
		this(format, (int)Top.gameWidth(), (int)Top.gameHeight(), false);
	}

	public FrameObject bindRenderer (FrameRenderer... renderer) {
		mFrameRenderer.clear();
		mFrameRenderer.addAll(renderer);
		return this;
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////
	public boolean isDisposed () {
		return isDisposed;
	}

	public boolean isDepth () {
		return isDepth;
	}

	public Format getFormat () {
		return mFormat;
	}

	@Override
	public void dispose () {
		if (isDisposed) return;
		super.dispose();
		isDisposed = true;
		Top.tres.gDeleteFrameBuffer(this);
	}

	@Override
	public Texture getColorBufferTexture () {
		if (mFrameRenderer.size == 0) return colorTexture;
		begin();
		for (FrameRenderer r : mFrameRenderer)
			r.render(mViewportSize);
		end(mViewportSize.x, mViewportSize.y, mViewportSize.width, mViewportSize.height);
		return super.getColorBufferTexture();
	}

	// ///////////////////////////////////////////////////////////////
	// help[er class
	// ///////////////////////////////////////////////////////////////

	/** This class to handle screen viewport */
	public static final class ViewportSize {
		public int x;
		public int y;
		public int width;
		public int height;

		public ViewportSize set (ViewportSize vp) {
			this.x = vp.x;
			this.y = vp.y;
			this.width = vp.width;
			this.height = vp.height;
			return this;
		}

		public ViewportSize setDimension (int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			return this;
		}

		public ViewportSize setPosition (int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public ViewportSize setSize (int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}
	}
}
