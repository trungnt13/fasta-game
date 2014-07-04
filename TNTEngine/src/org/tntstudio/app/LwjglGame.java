
package org.tntstudio.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.tntstudio.Const;
import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.core.GameCore;
import org.tntstudio.core.Top;
import org.tntstudio.resources.GameConfiguration;
import org.tntstudio.utils.io.IOUtils;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

@SuppressWarnings("unchecked")
public class LwjglGame implements Game {
	public static final void startNewLwjglGame () {
		Top.init();
		Security.addProvider(new BouncyCastleProvider());

		// serialize config
		final GameConfiguration gameConfig = Top.json.fromJson(GameConfiguration.class, readManifestFile());
		LwjglApplicationConfiguration appConfig = gameConfig.getLwjglConfiguration();

		/*-------- game core --------*/
		GameCore gameCore = null;
		try {
			gameCore = (GameCore)gameConfig.gameCore.newInstance();
			gameCore.setGameApplication(new LwjglGame());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		gameCore.mGameConfigurationRunnable = new Runnable() {
			@Override
			public void run () {
				Top.tgame
					.init(gameConfig.useLua, gameConfig.baseScreenList, gameConfig.popupScreenList, gameConfig.loadingScreenList)
					.initResources()
					.initAssets(gameConfig.resourceType, gameConfig.resourceParams, gameConfig.listOfResourceFile)
					.initGraphics(gameConfig.gameWidth, gameConfig.gameHeight, gameConfig.spriteBatchSize, gameConfig.isUseFrame,
						gameConfig.frameWidth, gameConfig.frameHeight, gameConfig.isKeepAspectRatio, gameConfig.spriteCacheSize,
						gameConfig.shapeRendererSize, gameConfig.shader, gameConfig.spriteCacheShader)
					.initAudio(gameConfig.isEnableAudio, gameConfig.musicVolume, gameConfig.soundVolume).initInput()
					.initCustomerServices();
			}
		};
		if (gameConfig.firstScreen != null) {
			gameCore.mSetFirstScreenRunnable = new Runnable() {
				@Override
				public void run () {
					Top.tgame.setScreen(gameConfig.firstScreen, ScreenChangeMode.Destroy);
				}
			};
		}

		new LwjglApplication(gameCore, appConfig);
	}

	public static final void startNewLwjglGame (int screenWidth, int screenHeight) {
		Top.init();
		Security.addProvider(new BouncyCastleProvider());

		// serialize config
		final GameConfiguration gameConfig = Top.json.fromJson(GameConfiguration.class, readManifestFile());
		/*-------- android app --------*/
		LwjglApplicationConfiguration appConfig = gameConfig.getLwjglConfiguration();

		/*-------- game core --------*/
		GameCore gameCore = null;
		try {
			gameCore = (GameCore)gameConfig.gameCore.newInstance();
			gameCore.setGameApplication(new LwjglGame());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		gameCore.mGameConfigurationRunnable = new Runnable() {
			@Override
			public void run () {
				Top.tgame
					.init(gameConfig.useLua, gameConfig.baseScreenList, gameConfig.popupScreenList, gameConfig.loadingScreenList)
					.initResources()
					.initAssets(gameConfig.resourceType, gameConfig.resourceParams, gameConfig.listOfResourceFile)
					.initGraphics(gameConfig.gameWidth, gameConfig.gameHeight, gameConfig.spriteBatchSize, gameConfig.isUseFrame,
						gameConfig.frameWidth, gameConfig.frameHeight, gameConfig.isKeepAspectRatio, gameConfig.spriteCacheSize,
						gameConfig.shapeRendererSize, gameConfig.shader, gameConfig.spriteCacheShader)
					.initAudio(gameConfig.isEnableAudio, gameConfig.musicVolume, gameConfig.soundVolume).initInput()
					.initCustomerServices();
			}
		};
		if (gameConfig.firstScreen != null) {
			gameCore.mSetFirstScreenRunnable = new Runnable() {
				@Override
				public void run () {
					Top.tgame.setScreen(gameConfig.firstScreen, ScreenChangeMode.Destroy);
				}
			};
		}

		new LwjglApplication(gameCore, appConfig);
	}

	private static final String readManifestFile () {
		try {
			File file = new File(Const.ManifestFilePath);
			InputStream stream = null;
			if (!file.exists())
				stream = FileHandle.class.getResourceAsStream("/" + Const.ManifestFilePath);
			else
				stream = new FileInputStream(file);

			String data = IOUtils.toString(stream);
			stream.close();

			return data;
		} catch (Exception e) {
			return null;
		}
	}

	public void sendMessages (int gameMessageID, Object... params) {

	};
}
