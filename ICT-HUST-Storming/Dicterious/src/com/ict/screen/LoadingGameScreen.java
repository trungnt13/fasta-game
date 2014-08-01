
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.google.gson.Gson;
import com.ict.DicteriousGame;
import com.ict.data.GameCreator;
import com.ict.data.GameData;
import com.ict.data.I;

public class LoadingGameScreen extends ScreenAdapter {

	private boolean isInit = false;

	@Override
	public void show () {

		/** create batch */
		DicteriousGame.Batch = new SpriteBatch();
		DicteriousGame.ScreenViewport.setToOrtho2D(0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);

		/** load data for showing progress */
	}

	@Override
	public void render (float delta) {
		if (!isInit) {
			isInit = true;
			/** load fonts */
			FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("brandonfont.ttf"));
			FreeTypeFontParameter fontParam = new FreeTypeFontParameter();

			fontParam.size = 30;
			DicteriousGame.FontSmall = fontGen.generateFont(fontParam);

			fontParam.size = 49;
			DicteriousGame.FontNormal = fontGen.generateFont(fontParam);

			fontParam.size = 58;
			DicteriousGame.FontBig = fontGen.generateFont(fontParam);

			fontGen.dispose();

			/** load game data */
			Gson gson = new Gson();
			String data = Gdx.files.internal("data.json").readString();
			GameData gameData = gson.fromJson(data, GameData.class);
			DicteriousGame.GameGenerator = new GameCreator(gameData);

			/** load graphics resources */
			DicteriousGame.AssetManager.load(I.MainMenu.Background, Texture.class);
			DicteriousGame.AssetManager.load(I.MainMenu.MapReview, Texture.class);
			DicteriousGame.AssetManager.load(I.MainMenu.StoriesReview, Texture.class);

			DicteriousGame.AssetManager.load(I.G1.BrickDark, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.BrickLight, Texture.class);

			DicteriousGame.AssetManager.load(I.G1.LandDark, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.LandLight, Texture.class);

			DicteriousGame.AssetManager.load(I.G1.Cloud2, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.Cloud1, Texture.class);

			DicteriousGame.AssetManager.load(I.G1.Sky, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.ParticleWin, ParticleEffect.class);

			DicteriousGame.AssetManager.load(I.G1.Box, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.True, Texture.class);
			DicteriousGame.AssetManager.load(I.G1.False, Texture.class);

			ParticleEffectParameter param = new ParticleEffectParameter();
			param.atlasFile = "explosion.pack";
			DicteriousGame.AssetManager.load(I.ParticleExplosion, ParticleEffect.class, param);

			/** io init */
			Gdx.input.setInputProcessor(DicteriousGame.InputMultiplexer);
			DicteriousGame.Layout = new Stage(new ScalingViewport(Scaling.stretch, DicteriousGame.ScreenWidth,
				DicteriousGame.ScreenHeight));
			DicteriousGame.InputMultiplexer.addProcessor(DicteriousGame.Layout);

			/** create screen */
			DicteriousGame.SMainMenu = new MainMenuScreen();
			DicteriousGame.SMapSelect = new MapSelectScreen();
			DicteriousGame.SStoriesSelect = new StoriesSelectScreen();
			DicteriousGame.SUserProfile = new UserProfileScreen();
		}

		// loading done
		if (DicteriousGame.AssetManager.update() && DicteriousGame.AssetManager.getProgress() > 1) {
			DicteriousGame.Game.setScreen(DicteriousGame.SMainMenu);
		}

	}
}
