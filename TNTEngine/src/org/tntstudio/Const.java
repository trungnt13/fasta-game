
package org.tntstudio;

import java.util.Comparator;

import org.tntstudio.graphics.Spine;
import org.tntstudio.resources.loaders.SpineLoader;

import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.MusicLoader.MusicParameter;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.assets.loaders.PixmapLoader.PixmapParameter;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** @author trungnt13 */
public final class Const {
	public static final String ManifestFilePath = "GameManifest.json";
	public static final Class<?>[] FastClassNameResolverSupport = new Class<?>[] {};

	public static final class Assets {
		public static final String DefaultResourceAlias = "Default";
		// the range between Default priority and Normal ones
		public static final int PriorityRange = 10000;

		// TODO: add new type of data over here
		public static final Class<?>[] DefaultSupportingAssets = new Class<?>[] {Texture.class, TextureAtlas.class,
			BitmapFont.class, ParticleEffect.class, Music.class, Sound.class, Pixmap.class, Skin.class, TiledMap.class, Spine.class};
		public static final Class<?>[] DefaultLoaderParameters = new Class<?>[] {TextureParameter.class,
			TextureAtlasParameter.class, BitmapFontParameter.class, ParticleEffectParameter.class, MusicParameter.class,
			SoundParameter.class, PixmapParameter.class, SkinParameter.class, TmxMapLoader.Parameters.class,
			SpineLoader.SpineParameter.class};
	}

	public static final class AnimationMode {
		public static final int NORMAL = 0;
		public static final int REVERSED = 1;
		public static final int LOOP = 2;
		public static final int LOOP_REVERSED = 3;
		public static final int LOOP_PINGPONG = 4;
		public static final int LOOP_RANDOM = 5;
	}

	public static class Orientation {
		public static final int HORIZONTAL = 1;
		public static final int VERTICAL = 2;
		public static final int LANDSCAPE = 1;
		public static final int PORTRAIT = 2;
	}

	public static class Side {
		public static final int TOP = 1 << 0;
		public static final int LEFT = 1 << 1;
		public static final int RIGHT = 1 << 2;
		public static final int BOTTOM = 1 << 3;
	}

	public static enum ScreenChangeMode {
		Hide, Destroy
	}

	public static enum GameState {
		Ready, Paused, Playing, Undefined, Disposed
	}

	public static final class ToastTime {
		public static final float LENGTH_SHORT = 2f;
		public static final float LENGTH_MEDIUM = 5f;
		public static final float LENGTH_LONG = 10f;
		public static final float SCREEN_TOUCH = 0f;
	}

	public static enum ErrorCode {
		ASSETS_ERROR, PREFERENCE_ERROR;
	}

	public static enum ScreenState {
		/** this mode happen when game was paused manually by developer or ApplicationListener call pause() */
		Paused,
		/** this mode happen when hide call */
		Hided,
		/** work with all normal function */
		Showed,
		/** everything is disposed */
		Disposed,
		/** Not inited state */
		NotDefined
	}

	public static enum SpriteState {
		Activated, Reseted, Disposed
	}

	public static enum UpdaterExecuteMode {
		Parallel, Sequence
	}

	public static final class Lua {
		public static final int ERR_RUNTIME = 1;
		public static final int ERR_FILENOTFOUND = 2;
		public static final int ERR_SYNTAX = 3;
		public static final int ERR_MEMORY = 4;

		public static final int INX_ERROR = -1;
	}

	public static final class Compare {
		public static final Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
			public int compare (String str1, String str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
				if (res == 0) {
					res = str1.compareTo(str2);
				}
				return res;
			}
		};
	}
}
