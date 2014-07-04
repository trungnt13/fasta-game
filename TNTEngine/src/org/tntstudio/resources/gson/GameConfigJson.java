
package org.tntstudio.resources.gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.tntstudio.Const;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.LoadingScreen;
import org.tntstudio.core.PopUpScreen;
import org.tntstudio.core.Top;
import org.tntstudio.resources.GameConfiguration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

@SuppressWarnings({"unchecked", "rawtypes"})
/**
 * {@link GameConfiguration} for more information
 */
public class GameConfigJson implements Serializer<GameConfiguration> {
	/*-------- basic info --------*/

	private static final String UseGL20 = "OpenGL20";
	private static final String RGBA = "RGBA";
	private static final String Depth = "Depth";
	private static final String Stencil = "Stencil";

	/*-------- android --------*/

	private static final String Accelerometer = "Accelerometer";
	private static final String Compass = "Compass";
	private static final String WakeLock = "Wakelock";

	/*-------- game --------*/

	private static final String Screen = "Screen";
	private static final String Core = "Core";
	private static final String FirstScreen = "FirstScreen";

	private static final String Resources = "Resources";
	private static final String ResourceType = "ResourceType";

	private static final String GameWidth = "GameWidth";
	private static final String GameHeight = "GameHeight";

	private static final String FrameWidth = "FrameWidth";
	private static final String FrameHeight = "FrameHeight";

	/*-------- graphics --------*/
	private static final String SpriteBatchSize = "SpriteBatchSize";
	private static final String SpriteCacheSize = "SpriteCacheSize";
	private static final String ShapeRendererSize = "ShapeRendererSize";

	private static final String UseFrame = "UseFrame";
	private static final String FrameAspect = "FrameKeepAspect";

	private static final String AudioEnable = "AudioEnable";
	private static final String MusicVolume = "MusicVolume";
	private static final String SoundVolume = "SoundVolume";

	/*-------- shader --------*/
	private static final String Shader = "BatchShader";
	private static final String SpriteCacheShader = "CacheShader";

	private static final String ClassTag = "ClassTag";

	/*-------- use utils --------*/
	private static final String UseLua = "UseLua";

	/*-------- advertising --------*/
	private static final String Admob = "Admob";
	private static final String AdmobPos = "AdmobPos";
	private static final String AdmobSize = "AdmobSize";

	/*-------- analytic --------*/
	private static final String Analytic = "Analytic";
	private static final String AnalyticRate = "AnalyticRate";
	private static final String AnalyticDryRun = "AnalyticDryRun";
	private static final String AnalyticLog = "AnalyticLog";

	@Override
	public void write (Json json, GameConfiguration object, Class knownType) {
		throw new TNTRuntimeException("We not support serialize GameConfiguration from Json");
	}

	@Override
	public GameConfiguration read (Json json, JsonValue obj, Class type) {
		GameConfiguration config = new GameConfiguration();
		if (!obj.isObject()) return config;

		JsonValue data = null;
		/*-------- opengl20 --------*/
		data = obj.get(UseGL20);
		if (data == null)
			config.isUseOpenGL20 = false;
		else
			config.isUseOpenGL20 = data.asBoolean();
		/*-------- RGBA --------*/
		data = obj.get(RGBA);
		if (data == null) {
			config.r = 5;
			config.g = 6;
			config.b = 5;
			config.a = 0;
		} else {
			String rgba = data.asString();
			config.r = Character.getNumericValue(rgba.charAt(0));
			config.g = Character.getNumericValue(rgba.charAt(1));
			config.b = Character.getNumericValue(rgba.charAt(2));
			config.a = Character.getNumericValue(rgba.charAt(3));
		}
		/*-------- depth and stencil --------*/
		data = obj.get(Depth);
		if (data == null)
			config.depth = 16;
		else
			config.depth = data.asInt();
		data = obj.get(Stencil);
		if (data == null)
			config.stencil = 0;
		else
			config.stencil = data.asInt();
		/*-------- acce, compass,wakelock --------*/
		data = obj.get(Accelerometer);
		if (data == null)
			config.useAccelerometer = false;
		else
			config.useAccelerometer = data.asBoolean();
		data = obj.get(Compass);
		if (data == null)
			config.useCompass = false;
		else
			config.useCompass = data.asBoolean();
		data = obj.get(WakeLock);
		if (data == null)
			config.useWakeLock = false;
		else
			config.useWakeLock = data.asBoolean();
		/*-------- screen list  --------*/
		data = obj.get(Screen);
		final Array<String> tmpStringList = new Array<String>();
		final Array<Class> tmpAllClassList = new Array<Class>();
		final Array<Class> tmpScreenClassList = new Array<Class>();
		final Array<Class> tmpPopupClassList = new Array<Class>();
		final Array<Class> tmpLoadingClassList = new Array<Class>();

		// transform string into Class
		for (int i = 0; i < data.size; i++) {
			tmpStringList.add(data.get(i).asString());
		}
		for (int i = 0; i < tmpStringList.size; i++) {
			try {
				tmpAllClassList.add(Class.forName(tmpStringList.get(i)));
			} catch (ClassNotFoundException e) {
				throw new TNTRuntimeException("Can't find Screen with given class name: " + tmpStringList.get(i));
			}
		}
		// traverse and find coordinate category
		for (Class c : tmpAllClassList) {
			if (LoadingScreen.class.isAssignableFrom(c))
				tmpLoadingClassList.add(c);
			else if (PopUpScreen.class.isAssignableFrom(c))
				tmpPopupClassList.add(c);
			else
				tmpScreenClassList.add(c);
		}
		config.baseScreenList = tmpScreenClassList.toArray(Class.class);
		config.popupScreenList = tmpPopupClassList.toArray(Class.class);
		config.loadingScreenList = tmpLoadingClassList.toArray(Class.class);
		// first screen
		data = obj.get(FirstScreen);
		if (data == null)
			config.firstScreen = null;
		else
			try {
				config.firstScreen = Class.forName(data.asString());
				if (!tmpScreenClassList.contains(config.firstScreen, false))
					throw new TNTRuntimeException("Can't find First Screen in specified Screen List " + data.asString());
			} catch (ClassNotFoundException e1) {
				throw new TNTRuntimeException("Can't find First Screen with given class name: " + data.asString());
			}

		/*-------- gamecore --------*/
		data = obj.get(Core);
		if (data == null) throw new TNTRuntimeException("you must add Core to your config file");
		try {
			config.gameCore = Class.forName(data.asString());
		} catch (ClassNotFoundException e) {
			throw new TNTRuntimeException("Can't find Game Core with given class name: " + data.asString());
		}
		/*-------- list of resource file --------*/
		data = obj.get(Resources);
		if (data == null)
			config.listOfResourceFile = new String[] {"data/resources.json"};
		else {
			config.listOfResourceFile = new String[data.size];
			for (int i = 0; i < data.size; i++) {
				config.listOfResourceFile[i] = data.get(i).asString();
			}
		}
		/*-------- resource map --------*/
		data = obj.get(ResourceType);
		if (data == null) {
			config.resourceType = Const.Assets.DefaultSupportingAssets;
			config.resourceParams = Const.Assets.DefaultLoaderParameters;
		} else {
			HashMap<String, String> mapType = json.fromJson(HashMap.class, data.asString());
			// read resource type
			tmpAllClassList.clear();
			Set<String> key = mapType.keySet();
			for (String string : key) {
				try {
					tmpAllClassList.add(Class.forName(string));
				} catch (ClassNotFoundException e) {
					throw new TNTRuntimeException("Can't find Resource Type with given class name: " + string);
				}
			}
			config.resourceType = tmpAllClassList.toArray(Class.class);
			// read resource param
			tmpAllClassList.clear();
			Collection<String> value = mapType.values();
			for (String string : value) {
				try {
					tmpAllClassList.add(Class.forName(string));
				} catch (ClassNotFoundException e) {
					throw new TNTRuntimeException("Can't find Resource Params with given class name: " + string);
				}
			}
			config.resourceParams = tmpAllClassList.toArray(Class.class);
		}
		/*-------- read dimension --------*/
		data = obj.get(GameWidth);
		if (data == null)
			config.gameWidth = Gdx.graphics.getWidth();
		else
			config.gameWidth = data.asInt();
		data = obj.get(GameHeight);
		if (data == null)
			config.gameHeight = Gdx.graphics.getHeight();
		else
			config.gameHeight = data.asInt();
		data = obj.get(FrameWidth);
		if (data == null)
			config.frameWidth = Gdx.graphics.getWidth();
		else
			config.frameWidth = data.asInt();
		data = obj.get(FrameHeight);
		if (data == null)
			config.frameHeight = Gdx.graphics.getHeight();
		else
			config.frameHeight = data.asInt();
		/*-------- spritebatch size --------*/
		data = obj.get(SpriteBatchSize);
		if (data == null)
			config.spriteBatchSize = 1024;
		else
			config.spriteBatchSize = data.asInt();
		/*-------- sprite cache size --------*/
		data = obj.get(SpriteCacheSize);
		if (data == null)
			config.spriteCacheSize = 0;
		else
			config.spriteCacheSize = data.asInt();
		/*-------- shape renderer size --------*/
		data = obj.get(ShapeRendererSize);
		if (data == null)
			config.shapeRendererSize = 0;
		else
			config.shapeRendererSize = data.asInt();
		/*-------- use frame --------*/
		data = obj.get(UseFrame);
		if (data == null)
			config.isUseFrame = true;
		else
			config.isUseFrame = data.asBoolean();
		/*-------- Aspect Ratio --------*/
		data = obj.get(FrameAspect);
		if (data == null)
			config.isKeepAspectRatio = false;
		else
			config.isKeepAspectRatio = data.asBoolean();
		/*-------- audio --------*/
		data = obj.get(AudioEnable);
		if (data == null)
			config.isEnableAudio = true;
		else
			config.isEnableAudio = data.asBoolean();
		data = obj.get(MusicVolume);
		if (data == null)
			config.musicVolume = 1;
		else
			config.musicVolume = data.asFloat();
		data = obj.get(SoundVolume);
		if (data == null)
			config.soundVolume = 1;
		else
			config.soundVolume = data.asFloat();
		/*-------- shader --------*/
		data = obj.get(Shader);
		if (data == null)
			config.shader = null;
		else
			config.shader = json.fromJson(ShaderProgram.class, data.asString());
		data = obj.get(SpriteCacheShader);
		if (data == null)
			config.spriteCacheShader = null;
		else
			config.spriteCacheShader = json.fromJson(ShaderProgram.class, data.asString());
		/*-------- class tags --------*/
		data = obj.get(ClassTag);
		if (data != null) {
			JsonValue val = null;
			final Json j = Top.json;
			for (int i = 0; i < data.size; i++) {
				val = data.get(i);
				String name = val.name();
				Class clazz = json.fromJson(Class.class, val.asString());
				j.addClassTag(name, clazz);
			}
		}
		/*-------- use lua --------*/
		data = obj.get(UseLua);
		if (data == null)
			config.useLua = false;
		else
			config.useLua = data.asBoolean();

		/*-------- admob --------*/
		data = obj.get(Admob);
		if (data == null)
			config.admobID = null;
		else
			config.admobID = data.asString();

		data = obj.get(AdmobPos);
		if (data == null)
			config.admobPos = null;
		else
			config.admobPos = data.asString();

		data = obj.get(AdmobSize);
		if (data == null)
			config.admobSize = "banner";
		else
			config.admobSize = data.asString();

		/*-------- analytics --------*/
		data = obj.get(UseLua);
		if (data == null)
			config.useLua = false;
		else
			config.useLua = data.asBoolean();

		/*-------- admob --------*/
		data = obj.get(Analytic);
		if (data == null)
			config.analytic = null;
		else
			config.analytic = data.asString();

		data = obj.get(AnalyticDryRun);
		if (data == null)
			config.analyticDryrun = false;
		else
			config.analyticDryrun = data.asBoolean();

		data = obj.get(AnalyticLog);
		if (data == null)
			config.analyticLoglevel = "info";
		else
			config.analyticLoglevel = data.asString();

		data = obj.get(AnalyticRate);
		if (data == null)
			config.analyticRate = 100;
		else
			config.analyticRate = data.asInt();

		// debug
// System.out.println(config.toString());
		return config;
	}
}
