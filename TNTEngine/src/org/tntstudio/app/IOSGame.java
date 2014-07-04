
package org.tntstudio.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;

import org.robovm.cocoatouch.foundation.NSAutoreleasePool;
import org.robovm.cocoatouch.foundation.NSBundle;
import org.robovm.cocoatouch.uikit.UIApplication;
import org.tntstudio.Const;
import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.core.GameCore;
import org.tntstudio.core.Top;
import org.tntstudio.resources.GameConfiguration;
import org.tntstudio.utils.io.IOUtils;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

@SuppressWarnings("unchecked")
public class IOSGame {

	public static final void startNewIOSGame (String[] argv) {
		Top.init();
		Security.addProvider(new com.android.org.bouncycastle.jce.provider.BouncyCastleProvider());

		GameDelegate iosApp = new GameDelegate();
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, iosApp.getClass());
		pool.drain();
	} // end: startNewIOSGame

	public static class GameDelegate extends IOSApplication.Delegate implements Game {
		@Override
		protected IOSApplication createApplication () {
			String data = null;
			final String internalPath = NSBundle.getMainBundle().getBundlePath();
			try {
				FileInputStream file = new FileInputStream(new File(internalPath, Const.ManifestFilePath));
				data = IOUtils.toString(file);
				file.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			final GameConfiguration gameConfig = Top.json.fromJson(GameConfiguration.class, data);
			IOSApplicationConfiguration appConfig = gameConfig.getIOSConfiguration();

			/*-------- game core --------*/
			GameCore gameCore = null;
			try {
				gameCore = (GameCore)gameConfig.gameCore.newInstance();
				gameCore.setGameApplication(this);
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

			/*-------- init now --------*/
			return new IOSApplication(gameCore, appConfig);
		}

		public void sendMessages (int gameMessageID, Object... params) {

		};
	}
}
