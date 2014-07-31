
package com.ict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.google.gson.Gson;
import com.ict.data.GameCreator;
import com.ict.data.GameData;
import com.ict.screen.S1BuildingCastle;

public class DicteriousGame extends Game {
	public static final AssetManager AssetManager = new AssetManager();
	public static SpriteBatch Batch;
	public static GameCreator GameData;
	public static BitmapFont FontSmall;
	public static BitmapFont FontNormal;
	public static BitmapFont FontBig;

	public static final int ScreenWidth = 720;
	public static final int ScreenHeight = 1280;

	@Override
	public void create () {
		// create sprite batch
		Batch = new SpriteBatch();

		// load game data
		Gson gson = new Gson();
		GameData = new GameCreator(gson.fromJson(Gdx.files.internal("data.json").readString(), GameData.class));

		// load font
		FreeTypeFontGenerator FontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("brandonfont.ttf"));

		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;

		param.size = 30;
		FontSmall = FontGenerator.generateFont(param);

		param.size = 40;
		FontNormal = FontGenerator.generateFont(param);

		param.size = 69;
		FontBig = FontGenerator.generateFont(param);

		FontGenerator.dispose();

		// load graphic data
		AssetManager.load("game1/land_light.png", Texture.class);
		AssetManager.load("game1/land_dark.png", Texture.class);
		AssetManager.load("game1/sky.png", Texture.class);
		AssetManager.load("game1/brick_light.png", Texture.class);
		AssetManager.load("game1/brick_dark.png", Texture.class);
		AssetManager.load("game1/win.p", ParticleEffect.class);
		while (!AssetManager.update() && AssetManager.getQueuedAssets() != 0) {
		}

		setScreen(new S1BuildingCastle());
	}

	@Override
	public void dispose () {
		super.dispose();
	}

}
