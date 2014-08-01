
package com.ict;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.google.gson.Gson;
import com.ict.data.GameCreator;
import com.ict.data.GameData;
import com.ict.data.I;
import com.ict.screen.LoadingGameScreen;
import com.ict.screen.MainMenuScreen;
import com.ict.screen.MapSelectScreen;
import com.ict.screen.StoriesSelectScreen;
import com.ict.screen.UserProfileScreen;

public class DicteriousGame extends Game {
	// ///////////////////////////////////////////////////////////////
	// static information
	// ///////////////////////////////////////////////////////////////

	public static final AssetManager AssetManager = new AssetManager();
	public static SpriteBatch Batch;
	public static BitmapFont FontSmall;
	public static BitmapFont FontNormal;
	public static BitmapFont FontBig;
	public static GameCreator GameGenerator;

	/** Graphic constants */
	public static final int ScreenWidth = 720;
	public static final int ScreenHeight = 1280;
	public static final Matrix4 ScreenViewport = new Matrix4();

	/** IO constants */
	public static final InputMultiplexer InputMultiplexer = new InputMultiplexer();
	public static Stage Layout;

	/** Screen */
	public static MainMenuScreen SMainMenu;
	public static MapSelectScreen SMapSelect;
	public static UserProfileScreen SUserProfile;
	public static StoriesSelectScreen SStoriesSelect;

	/** Current Game */
	public static Game Game;

	// ///////////////////////////////////////////////////////////////
	// override part
	// ///////////////////////////////////////////////////////////////

	@Override
	public void create () {
		/** set curerent game */
		Game = this;

		setScreen(new LoadingGameScreen());
	}

	@Override
	public void dispose () {
		Batch.dispose();
		FontSmall.dispose();
		FontBig.dispose();
		FontNormal.dispose();
		super.dispose();
		AssetManager.dispose();
	}
}
