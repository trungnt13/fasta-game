
package org.tntstudio.resources;

import org.robovm.cocoatouch.glkit.GLKViewDrawableColorFormat;
import org.robovm.cocoatouch.glkit.GLKViewDrawableDepthFormat;
import org.robovm.cocoatouch.glkit.GLKViewDrawableStencilFormat;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

@SuppressWarnings("rawtypes")
/**
 * load from {@link GameConfigJson} 
 */
public final class GameConfiguration {
	// OpenGL20
	public boolean isUseOpenGL20 = false;
	// RGBA
	public int r = 5, g = 6, b = 5, a = 0;
	// Depth, Stencil
	public int depth = 16, stencil = 0;

	// Accelerometer
	public boolean useAccelerometer = false;
	// Compass
	public boolean useCompass = false;
	// Wakelock
	public boolean useWakeLock = false;

	// Screen
	/* vital */public Class[] baseScreenList;
	/* vital */public Class[] popupScreenList;
	/* vital */public Class[] loadingScreenList;
	// FirstScreen
	/* Optional */public Class firstScreen;
	// Core
	/* vital */public Class gameCore;

	// ResourceType
	public Class[] resourceType;
	public Class[] resourceParams;
	// Resources
	public String[] listOfResourceFile;

	// GameWidth
	public int gameWidth;
	// GameHeight
	public int gameHeight;

	// SpriteBatchSize
	public int spriteBatchSize;
	// SpriteCacheSize
	public int spriteCacheSize;
	// ShapeRendererSize
	public int shapeRendererSize;

	// UseFrame
	public boolean isUseFrame;
	// FrameWidth
	public int frameWidth;
	// FrameHeight
	public int frameHeight;
	// FrameKeepAspect
	public boolean isKeepAspectRatio;

	// BatchShader
	public ShaderProgram shader;
	// CacheShader
	public ShaderProgram spriteCacheShader;

	// AudioEnable
	public boolean isEnableAudio;
	// MusicVolume
	public float musicVolume;
	// SoundVolume
	public float soundVolume;

	public boolean useLua = false;

	/*-------- admob info --------*/
	public String admobID = null;
	public String admobPos = "topleft";
	public String admobSize = "banner";

	/*-------- google analytic info --------*/

	public String analytic = null;
	public int analyticRate = 100;
	public boolean analyticDryrun = false;
	public String analyticLoglevel = "info";

	public GameConfiguration () {
	}

	// ///////////////////////////////////////////////////////////////
	// transform into application configuration
	// ///////////////////////////////////////////////////////////////

	public IOSApplicationConfiguration getIOSConfiguration () {
		IOSApplicationConfiguration appConfig = new IOSApplicationConfiguration();
		appConfig.orientationPortrait = gameWidth <= gameHeight;
		appConfig.orientationLandscape = gameWidth >= gameHeight;

		appConfig.useAccelerometer = useAccelerometer;
		appConfig.useCompass = useCompass;

		if (a == 0)
			appConfig.colorFormat = GLKViewDrawableColorFormat.RGB565;
		else
			appConfig.colorFormat = GLKViewDrawableColorFormat.RGBA8888;

		if (stencil != 0)
			appConfig.stencilFormat = GLKViewDrawableStencilFormat.Format8;
		else
			appConfig.stencilFormat = GLKViewDrawableStencilFormat.None;

		if (depth == 16)
			appConfig.depthFormat = GLKViewDrawableDepthFormat.Format16;
		else
			appConfig.depthFormat = GLKViewDrawableDepthFormat.Format24;
		return appConfig;
	}

	public AndroidApplicationConfiguration getAndroidConfiguration () {
		AndroidApplicationConfiguration appConfig = new AndroidApplicationConfiguration();
		appConfig.useGL20 = isUseOpenGL20;
		appConfig.useAccelerometer = useAccelerometer;
		appConfig.useCompass = useCompass;
		appConfig.useWakelock = useWakeLock;

		appConfig.a = a;
		appConfig.b = b;
		appConfig.depth = depth;
		appConfig.g = g;
		appConfig.r = r;
		appConfig.stencil = stencil;
		return appConfig;
	}

	public LwjglApplicationConfiguration getLwjglConfiguration () {
		LwjglApplicationConfiguration appConfig = new LwjglApplicationConfiguration();
		appConfig.useGL20 = isUseOpenGL20;
		appConfig.a = a;
		appConfig.b = b;
		appConfig.depth = depth;
		appConfig.g = g;
		appConfig.r = r;
		appConfig.stencil = stencil;
		appConfig.width = gameWidth;
		appConfig.height = gameHeight;
		return appConfig;
	}

	// ///////////////////////////////////////////////////////////////
	// debug
	// ///////////////////////////////////////////////////////////////

	@Override
	public String toString () {
		StringBuilder builder = new StringBuilder();
		builder.append("GL20: ");
		builder.append(isUseOpenGL20);
		builder.append("\n");

		builder.append("RGBA: ");
		builder.append(r);
		builder.append(g);
		builder.append(b);
		builder.append(a);
		builder.append("\n");

		builder.append("Depth: ");
		builder.append(depth);
		builder.append("\n");

		builder.append("Stencil: ");
		builder.append(stencil);
		builder.append("\n");

		builder.append("Accelerometer: ");
		builder.append(useAccelerometer);
		builder.append("\n");

		builder.append("Compass: ");
		builder.append(useCompass);
		builder.append("\n");

		builder.append("Wakelock: ");
		builder.append(useWakeLock);
		builder.append("\n");

		builder.append("BaseScreen: ");
		for (Class c : baseScreenList)
			builder.append(c.getName() + " - ");
		builder.append("\n");

		builder.append("PopupScreen: ");
		for (Class c : popupScreenList)
			builder.append(c.getName() + " - ");
		builder.append("\n");

		builder.append("LoadingScreen: ");
		for (Class c : loadingScreenList)
			builder.append(c.getName() + " - ");
		builder.append("\n");

		builder.append("FirstScreen: ");
		builder.append(firstScreen.getName());
		builder.append("\n");

		builder.append("GameCore: ");
		builder.append(gameCore.getName());
		builder.append("\n");

		builder.append("Resource Type: ");
		for (Class c : resourceType)
			builder.append(c.getName() + " - ");
		builder.append("\n");

		builder.append("Resource Params: ");
		for (Class c : resourceParams)
			builder.append(c.getName() + " - ");
		builder.append("\n");

		builder.append("Resources List: ");
		for (String c : listOfResourceFile)
			builder.append(c + " - ");
		builder.append("\n");

		builder.append("GameWidth: ");
		builder.append(gameWidth);
		builder.append("\n");

		builder.append("GameHeight: ");
		builder.append(gameHeight);
		builder.append("\n");

		builder.append("Batch Size: ");
		builder.append(spriteBatchSize);
		builder.append("\n");

		builder.append("Cache Size: ");
		builder.append(spriteCacheSize);
		builder.append("\n");

		builder.append("Shape Size: ");
		builder.append(shapeRendererSize);
		builder.append("\n");

		builder.append("Use Frame: ");
		builder.append(isUseFrame);
		builder.append("\n");

		builder.append("Frame Width: ");
		builder.append(frameWidth);
		builder.append("\n");

		builder.append("Frame Height: ");
		builder.append(frameHeight);
		builder.append("\n");

		builder.append("Keep Aspect Ratio: ");
		builder.append(isKeepAspectRatio);
		builder.append("\n");

		builder.append("Audio Enable: ");
		builder.append(isEnableAudio);
		builder.append("\n");

		builder.append("Audio Enable: ");
		builder.append(isEnableAudio);
		builder.append("\n");

		builder.append("Music Volume: ");
		builder.append(musicVolume);
		builder.append("\n");

		builder.append("Sound Volume: ");
		builder.append(soundVolume);
		builder.append("\n");

		builder.append("Use Lua: ");
		builder.append(useLua);
		builder.append("\n");

		/*-------- admob --------*/

		builder.append("Admob ID: ");
		builder.append(admobID);
		builder.append("\n");

		builder.append("Admob Pos: ");
		builder.append(admobPos);
		builder.append("\n");

		builder.append("Admob Size: ");
		builder.append(admobSize);
		builder.append("\n");

		/*-------- analytic --------*/

		builder.append("Analytic: ");
		builder.append(analytic);
		builder.append("\n");

		builder.append("Analytic Rate: ");
		builder.append(analyticRate);
		builder.append("\n");

		builder.append("Analytic Dryrun: ");
		builder.append(analyticDryrun);
		builder.append("\n");

		builder.append("Analytic Log: ");
		builder.append(analyticLoglevel);
		builder.append("\n");

		return builder.toString();
	}

}
