
package org.tntstudio.core;

import org.tntstudio.lua.LuaAndroidScript;
import org.tntstudio.lua.LuaScript;
import org.tntstudio.resources.Assets;
import org.tntstudio.resources.AssetsDefinition;
import org.tntstudio.resources.AssetsDefinition.ParamDescription;
import org.tntstudio.resources.Context;
import org.tntstudio.resources.FilePath;
import org.tntstudio.resources.GameConfiguration;
import org.tntstudio.resources.Pack;
import org.tntstudio.resources.gson.FilePathJson;
import org.tntstudio.resources.gson.GameConfigJson;
import org.tntstudio.resources.gson.ShaderProgramJson;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/** Base class to manage all component of Engine
 * @author trungnt13 */
public final class Top {

	/** {@link GameCore} */
	public static GameCore tgame;

	/** {@link Input} */
	public static Input tinp;

	/** {@link CustomerServices} */
	public static CustomerServices tcus;

	/** {@link Resources} */
	public static Resources tres;

	/** {@link LuaAndroidScript} */
	public static LuaScript lua;

	// ///////////////////////////////////////////////////////////////
	//
	// ///////////////////////////////////////////////////////////////
	public static final void init () {
		json.setSerializer(Class.class, new Assets.ClassJson());
		json.setSerializer(Context.class, new Assets.ContextJson());
		json.setSerializer(Pack.class, new Assets.PackJson());

		json.setSerializer(AssetsDefinition.class, new Assets.AssetsDefinitionJson());
		json.setSerializer(ParamDescription.class, new Assets.ParamDescriptionJson());

		json.setSerializer(FilePath.class, new FilePathJson());
		json.setSerializer(ShaderProgram.class, new ShaderProgramJson());
		json.setSerializer(GameConfiguration.class, new GameConfigJson());
	}

	/*-------- gson --------*/
//	public static final Gson Json = new GsonBuilder().registerTypeAdapter(Class.class, new ClassGson())
//		.registerTypeAdapter(Context.class, new ContextGson()).registerTypeAdapter(Pack.class, new PackGson())
//		.registerTypeAdapter(AssetsDefinition.class, new AssetsDefinitionGson())
//		.registerTypeAdapter(ParamDescription.class, new ParamDescriptionGson())
//		.registerTypeAdapter(FilePath.class, new FilePathGson()).registerTypeAdapter(ShaderProgram.class, new ShaderProgramGson())
//		.registerTypeAdapter(GameConfiguration.class, new GameConfigGson()).setPrettyPrinting().create();

	public static final com.badlogic.gdx.utils.Json json = new com.badlogic.gdx.utils.Json();

	/** @return Width of game in pixels */
	public static float gameWidth () {
		return tres.mGraphics.getGameWidth();
	}

	/** @return Height of game in pixels */
	public static float gameHeight () {
		return tres.mGraphics.getGameHeight();
	}

	/** @return real Width of devices in pixels */
	public static int screenWidth () {
		return Gdx.graphics.getWidth();
	}

	/** @return real Height of devices in pixels */
	public static int screenHeight () {
		return Gdx.graphics.getHeight();
	}

	/** @return current orientation of game */
	public static int gameOrientation () {
		return tres.mGraphics.getOrientation();
	}
}
