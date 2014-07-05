
package org.tntstudio.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.tntstudio.Const;
import org.tntstudio.Const.GameState;
import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.action.ActorAccessor;
import org.tntstudio.action.SpriteAccessor;
import org.tntstudio.app.Game;
import org.tntstudio.core.Resources.ResourcesCreator;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.lua.LuaManager;
import org.tntstudio.utils.Error;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PauseableThread;

/** {@link ApplicationListener}
 * @author trungnt13 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class GameCore implements ApplicationListener {
	private BaseScreen mScreen;
	private String mLastScreenName = null;

	private final Array<PopUpScreen> mPopupOfBaseScreenList = new Array<PopUpScreen>();
	// this list contain current drawable popup screen
	private final Array<PopUpScreen> mAllVisiblePopupScreen = new Array<PopUpScreen>();
	// store all popup screen of a game (include not visible PopUpScreen)
	private final ObjectMap<String, PopUpScreen> mAllPopUpScreen = new ObjectMap<String, PopUpScreen>();

	// list of all backup screen
	private final ObjectMap<String, BaseScreen> mBackupScreenMap = new ObjectMap<String, BaseScreen>();
	private final ObjectMap<String, Class<? extends BaseScreen>> mScreenType = new ObjectMap<String, Class<? extends BaseScreen>>();

	private final ObjectMap<String, Class<? extends PopUpScreen>> mPopUpScreenType = new ObjectMap<String, Class<? extends PopUpScreen>>();

	/** <b>(FirstScreenName_SecondScreenName,LoadingScreen)</b> */
	private final ObjectMap<String, LoadingScreen> mTransistionMap = new ObjectMap<String, LoadingScreen>();
	/** <b>(LoadingScreenName,LoadingScreenClass)</b> for involve new instance from class */
	private final ObjectMap<String, Class<? extends LoadingScreen>> mLoadingScreenType = new ObjectMap<String, Class<? extends LoadingScreen>>();

	private GameState mCurrentGameState = GameState.Undefined;
	private GameState mLastGameState = mCurrentGameState;

	final TweenManager mTween = new TweenManager();

	private boolean isInitCalled = false;

	/*-------- simple resource manager --------*/
	private final ObjectMap<String, Object> mStaticResource = new ObjectMap<String, Object>();

	/*-------- error resolver --------*/
	private final Array<Error> mErrorQueue = new Array<Error>(true, 13);
	private final Dequeuer<Error> mErrorDequeuer = new Dequeuer<Error>();
	private final PauseableThread mErrorThread = new PauseableThread(new Runnable() {
		@Override
		public void run () {
			if (mErrorQueue.size > 0) {
				mErrorDequeuer.setData(mErrorQueue);
				mErrorQueue.clear();
				onGameError(mErrorDequeuer);

				for (Error e : mErrorDequeuer.Data) {
					if (e.Solution != null) Gdx.app.postRunnable(e.Solution);
				}
				mErrorDequeuer.Data.clear();
			}

			mErrorThread.onPause();
		}
	});

	/*-------- life cycle manage --------*/
	private final Array<LifecycleListener> mLifecycleList = new Array<LifecycleListener>();
	private final LoadingScreen DefaultLoadingScreen = new LoadingScreen() {

		@Override
		public void onUpdate (float delta) {

		}

		@Override
		public void onRender (SpriteBatch batch) {

		}

		@Override
		public void onInitDrawInfo () {

		}

		@Override
		public void onConfigurationLoading () {
			setAutoChangeScreen(true);
			setDisposeAfterLoading(false);
		}

		@Override
		public Class[] ScreenTransistionClass () {
			return null;
		}

	};

	// ///////////////////////////////////////////////////////////////
	// init methods
	// ///////////////////////////////////////////////////////////////

	/*-------- normal config --------*/

	public Runnable mGameConfigurationRunnable;
	public Runnable mSetFirstScreenRunnable;

	public final ResourcesCreator init (boolean useLua, Class<? extends BaseScreen>[] screenList,
		Class<? extends PopUpScreen>[] popupList, Class<? extends LoadingScreen>[] loadingList) {

		if (isInitCalled) return Top.tres.Creator;

		/*-------- create new instances --------*/
		Top.tres = new Resources();
		Top.tinp = new Input();
		Top.tcus = new CustomerServices();
		if (useLua) Top.lua = LuaManager.newScript();

		/*-------- init now --------*/
		try {
			// get base screen list
			for (Class<? extends BaseScreen> c : screenList) {
				mScreenType.put(c.getSimpleName(), c);
			}
			// get popup screen list
			for (Class<? extends PopUpScreen> c : popupList)
				mPopUpScreenType.put(c.getSimpleName(), c);

			final Method transistionName = LoadingScreen.class.getDeclaredMethod("TransistionName");
			// get loading screen list
			for (Class<? extends LoadingScreen> c : loadingList) {
				LoadingScreen screen = c.newInstance();
				String[] firstName = (String[])transistionName.invoke(screen);

				for (String string : firstName)
					mTransistionMap.put(string, screen);
				mLoadingScreenType.put(c.getSimpleName(), c);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		isInitCalled = true;
		return Top.tres.Creator;
	}

	// ///////////////////////////////////////////////////////////////
	// contructor
	// ///////////////////////////////////////////////////////////////
	private Game mGameApplication;

	protected GameCore () {
		Top.tgame = this;
	}

	/** set {@link Game} for this GameCore in order to involve specific platform's methods */
	public void setGameApplication (Game game) {
		mGameApplication = game;
	}

	Game getGameApplication () {
		return mGameApplication;
	}

	// ///////////////////////////////////////////////////////////////
	// ovveride methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public final void create () {
		if (mGameConfigurationRunnable != null)
			mGameConfigurationRunnable.run();
		else
			throw new TNTRuntimeException("You must have GameManifest.json to run this game");

		/*-------- init screen --------*/
		Screen.init();

		/*-------- init input --------*/
		final InputMultiplexer multiInput = new InputMultiplexer();
		multiInput.addProcessor(0, Top.tres.gFrame());
		multiInput.addProcessor(1, Top.tinp);
		Gdx.input.setInputProcessor(multiInput);

		/*-------- start error thread --------*/
		mErrorThread.start();

		/*-------- init TweenManager default --------*/
		Tween.setWaypointsLimit(5);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		if (!isInitCalled) throw new TNTRuntimeException("Game is not initialized, please call init() function of GameCore first");

		setCurrentGameState(GameState.Ready);
		onGameCreate();

		if (mSetFirstScreenRunnable != null) mSetFirstScreenRunnable.run();
	}

	@Override
	public final void resize (int width, int height) {
		Top.tres.gValidateScreenSize(width, height);

		/*-------- popup --------*/
		for (PopUpScreen p : mAllVisiblePopupScreen)
			p.resize(width, height);

		if (mScreen != null) mScreen.resize(width, height);
	}

	@Override
	public final void dispose () {
		setCurrentGameState(GameState.Disposed);

		/*-------- popup --------*/
		for (PopUpScreen p : mAllVisiblePopupScreen)
			p.dispose();

		if (mScreen != null) mScreen.dispose();

		for (LifecycleListener l : mLifecycleList) {
			l.dispose();
		}
		onGameDestroy();

		System.gc();
	}

	@Override
	public final void pause () {
		setCurrentGameState(GameState.Paused);

		onGamePause();
		for (LifecycleListener l : mLifecycleList) {
			l.pause();
		}

		/*-------- popup --------*/
		for (PopUpScreen p : mAllVisiblePopupScreen)
			p.pause();

		if (mScreen != null) mScreen.pause();
	}

	@Override
	public final void resume () {
		if (mScreen != null && mScreen.isGamePlayScreen())
			setCurrentGameState(GameState.Playing);
		else
			setCurrentGameState(GameState.Ready);

		onGameResume();
		for (LifecycleListener l : mLifecycleList)
			l.resume();

		/*-------- popup --------*/
		for (PopUpScreen p : mAllVisiblePopupScreen)
			p.resume();

		if (mScreen != null) mScreen.resume();
	}

	@Override
	public final void render () {
		final float delta = Gdx.graphics.getDeltaTime();

		mTween.update(delta);
		Top.tres.renderGraphics();
		/*-------- update main scree --------*/
		if (mScreen != null) {
			mScreen.update(delta);
			mScreen.render(delta);
		}
		/*-------- update popup --------*/
		for (PopUpScreen p : mAllVisiblePopupScreen) {
			p.update(delta);
			p.render(delta);
		}

		onGameRender(delta);
		// update static management class
		Top.tres.updateAssets(delta);
	}

	// ///////////////////////////////////////////////////////////////
	// getter setter methods of GameCore
	// ///////////////////////////////////////////////////////////////

	public final GameState getCurrentGameState () {
		return mCurrentGameState;
	}

	public final GameState getLastGameState () {
		return mLastGameState;
	}

	private final void setCurrentGameState (GameState state) {
		mLastGameState = mCurrentGameState;
		mCurrentGameState = state;
	}

	public final Screen getInitedScreen (String name) {
		Screen result = mScreen.Name().equals(name) ? mScreen : null;
		if (result != null) return result;

		result = mBackupScreenMap.get(name);
		if (result != null) return result;

		result = mAllPopUpScreen.get(name);
		return result;
	}

	void put (String key, Object obj) {
		mStaticResource.put(key, obj);
	}

	final <T> T get (String key) {
		return (T)mStaticResource.get(key);
	}

	public final void registerLifecycleListener (LifecycleListener lifecycle) {
		if (!mLifecycleList.contains(lifecycle, true)) mLifecycleList.add(lifecycle);
	}

	public final void unregisterLifecycleListener (LifecycleListener lifecycle) {
		mLifecycleList.removeValue(lifecycle, true);
	}

	public final void putError (Error error) {
		synchronized (mErrorQueue) {
			mErrorQueue.add(error);
			if (mErrorThread.isPaused()) {
				mErrorThread.onResume();
			}
		}
	}

	public final String getGameIdentification () {
		StringBuilder name = new StringBuilder();
		name.append(getClass().getSimpleName());
		for (Class<? extends BaseScreen> c : mScreenType.values()) {
			name.append(".");
			name.append(c.getSimpleName());
		}
		return name.toString();
	}

	public final String getGameName () {
		return getClass().getSimpleName().replace("Core", "");
	}

	public final String getLastScreenName () {
		return mLastScreenName;
	}

	public final String getCurrentScreenName () {
		return mScreen.Name();
	}

	/** @return the currently active {@link Screen}. */
	public final BaseScreen getCurrentScreen () {
		return mScreen;
	}

	public final Class getClassTypeForScreenName (String simpleName) {
		Class tmp = mScreenType.get(simpleName);
		if (tmp != null) return tmp;
		tmp = mLoadingScreenType.get(simpleName);
		if (tmp != null) return tmp;
		tmp = mPopUpScreenType.get(simpleName);
		return tmp;
	}

	public abstract String getCompanyName ();

	// ///////////////////////////////////////////////////////////////
	// sceen transistion methods
	// ///////////////////////////////////////////////////////////////

	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen, if any.
	 * 
	 * @param screen may be {@code null} */
	public final void setScreen (Class<? extends BaseScreen> screenType, ScreenChangeMode mode) {
		final String name = screenType.getSimpleName();
		/*-------- get screen out --------*/
		BaseScreen screen = mBackupScreenMap.get(name);
		if (screen == null) {
			Class<? extends BaseScreen> clazz = mScreenType.get(name);
			if (clazz == null) throw new TNTRuntimeException("Unknown screen!");
			try {
				screen = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		setScreen(screen, mode);
	}

	void setScreen (String name, ScreenChangeMode mode) {
		/*-------- get screen out --------*/
		BaseScreen screen = mBackupScreenMap.get(name);
		if (screen == null) {
			Class<? extends BaseScreen> clazz = mScreenType.get(name);
			if (clazz == null) throw new TNTRuntimeException("Unknown screen!");
			try {
				screen = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		setScreen(screen, mode);
	}

	/** This is main setScreen methods, main logic here */
	public final void setScreen (BaseScreen newScreen, ScreenChangeMode mode) {
		if (newScreen == this.mScreen) return;

		// backup or dispose old screen
		if (this.mScreen != null) {
			// save old screen name
			mLastScreenName = this.mScreen.Name();
			// choose transistion mode
			if (mode == ScreenChangeMode.Hide) {
				mBackupScreenMap.put(newScreen.Name(), newScreen);
				this.mScreen.hide();
			} else {
				this.mScreen.dispose();
				mBackupScreenMap.remove(this.mScreen.Name());
			}
		}

		/*-------- release all current popup screen --------*/
		for (PopUpScreen p : mPopupOfBaseScreenList)
			p.hide();
		mAllVisiblePopupScreen.removeAll(mPopupOfBaseScreenList, true);
		mPopupOfBaseScreenList.clear();

		/*-------- all screen resource is loaded --------*/
		if (Top.tres.isScreenResourcesLoaded(newScreen) || LoadingScreen.class.isAssignableFrom(newScreen.getClass())) {
			// set new screen
			this.mScreen = newScreen;
			this.mScreen.show();
		}
		// screen resource is not loaded, pick coordinate loading screen
		else {
			String oldScreenName = this.mScreen == null ? "" : this.mScreen.Name();
			String newScreenName = newScreen.Name();
			String transistionName = oldScreenName + "_" + newScreenName;

			LoadingScreen loader = null;
			// if new screen is not loaded add default loader
			if (!Top.tres.isScreenResourcesLoaded(newScreen)) loader = mTransistionMap.get(transistionName);

			// no loading screen for transistion
			if (loader == null) {
				// if this is not the first screen of the game
				if (!oldScreenName.equals("")) {
					this.mScreen = newScreen;
					this.mScreen.show();
					return;
				}
				loader = DefaultLoadingScreen;
			}
			// create loader now
			loader.setTransistion(oldScreenName, newScreenName, mode);
			this.mScreen = loader;
			this.mScreen.show();
		}
	}

	/*-------- popup screen methods --------*/

	public final void newFreePopupScreen (Class<? extends PopUpScreen> screenType) {
		PopUpScreen screen = findPopupScreen(screenType.getSimpleName());
		mAllVisiblePopupScreen.add(screen);

		screen.show();
	}

	final void hidePopupScreen (PopUpScreen screen) {
		screen.hide();
		mPopupOfBaseScreenList.removeValue(screen, true);
		mAllVisiblePopupScreen.removeValue(screen, true);
	}

	public final void deletePopupScreen (PopUpScreen screen) {
		screen.hide();
		screen.dispose();
		mPopupOfBaseScreenList.removeValue(screen, true);
		mAllVisiblePopupScreen.removeValue(screen, true);
		mAllPopUpScreen.remove(screen.Name());
	}

	void newPopupScreen (String name) {
		if (this.mScreen == null) return;

		PopUpScreen screen = findPopupScreen(name);
		if (mAllVisiblePopupScreen.contains(screen, true)) throw new TNTRuntimeException("This screen is already Pop up");
		mAllVisiblePopupScreen.add(screen);
		mPopupOfBaseScreenList.add(screen);
		screen.setParentScreen(this.mScreen);

		screen.show();
	}

	private PopUpScreen findPopupScreen (String name) {
		PopUpScreen screen = mAllPopUpScreen.get(name);
		if (screen == null) {
			Class<? extends PopUpScreen> clazz = mPopUpScreenType.get(name);
			if (clazz == null) throw new TNTRuntimeException("Unknown PopupScreen!");
			try {
				screen = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			mAllPopUpScreen.put(name, screen);
		}
		return screen;
	}

	// ///////////////////////////////////////////////////////////////
	// abstract methods
	// ///////////////////////////////////////////////////////////////
	/** Listen to surface size
	 * 
	 * @param width the screen width
	 * @param height the screen height */
	protected void onGameCreate () {

	}

	/** Render all stuff here
	 * 
	 * @param DeltaTime the time each frame repeat */
	protected void onGameRender (float delta) {

	}

	/** Your pause stuff here ,such as: save your preferences */
	protected void onGamePause () {

	}

	/** This method do the same function as resume */
	protected void onGameResume () {

	}

	/** This method will release memory */
	protected void onGameDestroy () {

	}

	/** @param errorCode {@link Const.Error} */
	protected void onGameError (Dequeuer<Error> errors) {

	}

	// ///////////////////////////////////////////////////////////////
	// helper class
	// ///////////////////////////////////////////////////////////////
	public final class Dequeuer<T> {
		private final Array<T> Data = new Array<T>(true, 13);
		int dequeueCount = 0;

		private Dequeuer () {
		}

		private void setData (Array<T> ts) {
			Data.addAll(ts);
			dequeueCount = 0;
		}

		public T dequeue () {
			if (dequeueCount >= Data.size) return null;
			return Data.get(dequeueCount++);
		}
	}

}
