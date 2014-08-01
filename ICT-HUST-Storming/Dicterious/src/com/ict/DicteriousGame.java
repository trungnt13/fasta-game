
package com.ict;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
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
import com.ict.screen.G1BuildingCastle;
import com.ict.screen.G3CraftCrossbow;
import com.ict.screen.G4DefendEnemies;
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

		/** create batch */
		Batch = new SpriteBatch();
		ScreenViewport.setToOrtho2D(0, 0, ScreenWidth, ScreenHeight);

		/** load fonts */
		FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("brandonfont.ttf"));
		FreeTypeFontParameter fontParam = new FreeTypeFontParameter();

		fontParam.size = 30;
		FontSmall = fontGen.generateFont(fontParam);

		fontParam.size = 49;
		FontNormal = fontGen.generateFont(fontParam);

		fontParam.size = 58;
		FontBig = fontGen.generateFont(fontParam);

		fontGen.dispose();

		/** load game data */
		Gson gson = new Gson();
		String data = Gdx.files.internal("data.json").readString();
		GameData gameData = gson.fromJson(data, GameData.class);
		GameGenerator = new GameCreator(gameData);

		/** load graphics resources */
		AssetManager.load(I.MainMenu.Background, Texture.class);
		AssetManager.load(I.MainMenu.MapReview, Texture.class);
		AssetManager.load(I.MainMenu.StoriesReview, Texture.class);

		AssetManager.load(I.G1.BrickDark, Texture.class);
		AssetManager.load(I.G1.BrickLight, Texture.class);

		AssetManager.load(I.G1.LandDark, Texture.class);
		AssetManager.load(I.G1.LandLight, Texture.class);

		AssetManager.load(I.G1.Cloud2, Texture.class);
		AssetManager.load(I.G1.Cloud1, Texture.class);

		AssetManager.load(I.G1.Sky, Texture.class);
		AssetManager.load(I.G1.ParticleWin, ParticleEffect.class);

		AssetManager.load(I.G1.Box, Texture.class);
		AssetManager.load(I.G1.True, Texture.class);
		AssetManager.load(I.G1.False, Texture.class);

		while (!AssetManager.update() && AssetManager.getProgress() < 1) {
		}

		/** io init */
		Gdx.input.setInputProcessor(InputMultiplexer);
		Layout = new Stage(new ScalingViewport(Scaling.stretch, ScreenWidth, ScreenHeight));
		InputMultiplexer.addProcessor(Layout);

		/** create screen */
		SMainMenu = new MainMenuScreen();
		SMapSelect = new MapSelectScreen();
		SStoriesSelect = new StoriesSelectScreen();
		SUserProfile = new UserProfileScreen();
		setScreen(SMainMenu);
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
