
package org.tntstudio.core;

import java.util.ArrayList;

import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.Const.ScreenState;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.graphics.objs.Sprite;
import org.tntstudio.graphics.objs.SpriteAnimation;
import org.tntstudio.interfaces.AssetsListener;
import org.tntstudio.interfaces.PackLoadedListener;
import org.tntstudio.resources.Pack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@SuppressWarnings("rawtypes")
/**
 * 
 * @author trungnt13
 */
public abstract class LoadingScreen extends BaseScreen implements AssetsListener, PackLoadedListener {
	private float mLoadPercentage = 0;

	private boolean isAutoChangedScreen = true;
	private boolean isDisposedAfterLoading = false;

	// flag show that onShow method is called
	private boolean isOnShowCalled = false;

	private final ArrayList<String> mNeedUnloadPack = new ArrayList<String>();
	private final ArrayList<String> mNeedLoadPack = new ArrayList<String>();

	private boolean isDrawInfoInited = false;

	private Runnable mCompletedAction = null;

	private String FirstName = null;
	private String SecondName = null;
	private ScreenChangeMode ChangeMode = null;

	/** This methods must be called before {@link LoadingScreen#onShow(int, int)}, only {@link GameCore} can use this methods */
	void setTransistion (String firstName, String secondName, ScreenChangeMode changeMode) {
		Top.tres.rRegisterPackLoadedListener(this, this);

		FirstName = firstName;
		SecondName = secondName;
		ChangeMode = changeMode;

		/*-------- reset all flag --------*/
		isOnShowCalled = false;
		isDrawInfoInited = false;

		mNeedLoadPack.clear();
		mNeedUnloadPack.clear();

		mLoadPercentage = 0;
	}

	@Override
	public void onCreate () {
	}

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		if (isOnShowCalled) return;
		isOnShowCalled = true;

		// immediately call packCompletedLoading
		if (Top.tres.isScreenResourcesLoaded(this)) packCompletedLoading();

		// call configuration
		onConfigurationLoading();

		// prepare loading data
		if (ChangeMode == ScreenChangeMode.Destroy) {
			if (!mNeedUnloadPack.contains(FirstName)) mNeedUnloadPack.add(FirstName);
		}
		if (!mNeedLoadPack.contains(SecondName)) mNeedLoadPack.add(SecondName);
		if (!mNeedLoadPack.contains(Name())) mNeedLoadPack.add(Name());

		Top.tres.rStartLoadingProcess(this, mNeedUnloadPack, mNeedLoadPack);
	}

	@Override
	public void onHide (ScreenState mode) {
	}

	@Override
	public void onDisposed () {
		Top.tres.rUnregisterPackLoadedListener(this);
		Top.tres.rDeletePack(this);
	}

	@Override
	public boolean isGamePlayScreen () {
		return false;
	}

	public float getProgress () {
		return mLoadPercentage;
	}

	// ///////////////////////////////////////////////////////////////
	// abstract screen methods
	// ///////////////////////////////////////////////////////////////

	/** This methods call only 1 time, the first initilization of {@link LoadingScreen}, take a look at
	 * <p>
	 * {@link LoadingScreen#setAutoChangeScreen(boolean)}
	 * <p>
	 * {@link LoadingScreen#setDisposeAfterLoading(boolean)}
	 * <p>
	 * {@link LoadingScreen#setLoadingCompletedAction(Runnable)} */
	public abstract void onConfigurationLoading ();

	/** This method is called after the {@link Pack} of this {@link LoadingScreen} is <b>loaded</b>. All drawing stuffs are init
	 * here, such as: {@link SpriteAnimation}, {@link Sprite}... */
	public abstract void onInitDrawInfo ();

	@Override
	void render (float delta) {
		if (!isDrawInfoInited) return;
		super.render(delta);
	}

	/** normal render methods, render everything here */
	public abstract void onRender (SpriteBatch batch);

	/** Update everything concerning logic here */
	public abstract void onUpdate (float delta);

	// ///////////////////////////////////////////////////////////////
	// control methods
	// ///////////////////////////////////////////////////////////////
	/** {@link Runnable} is called when loading is finished */
	protected void setLoadingCompletedAction (Runnable action) {
		mCompletedAction = action;
	}

	/** Automatically change to new screen after load finish, otherwise you have to call {@link LoadingScreen#changeScreen()}
	 * yourself */
	protected void setAutoChangeScreen (boolean isAuto) {
		isAutoChangedScreen = isAuto;
	}

	/** if isDispose is true, this screen only be used one time. */
	protected void setDisposeAfterLoading (boolean isDispose) {
		isDisposedAfterLoading = isDispose;
	}

	/** Add more {@link Pack} for loading */
	protected void addLoadPack (String pack) {
		if (!mNeedLoadPack.contains(pack)) mNeedLoadPack.add(pack);
	}

	/** Add more {@link Pack} for unloading */
	protected void addUnloadPack (String pack) {
		if (!mNeedUnloadPack.contains(pack)) mNeedUnloadPack.add(pack);
	}

	/** Immediately change to new screen if loading is completed */
	protected void changeScreen () {
		if (mLoadPercentage == 1f){
			Top.tgame.setScreen(SecondName, isDisposedAfterLoading ? ScreenChangeMode.Destroy : ScreenChangeMode.Hide);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// assets listener methods
	// ///////////////////////////////////////////////////////////////
	/*-------- pack load completed --------*/
	@Override
	public final void packCompletedLoading () {
		if (isOnShowCalled) {
			onInitDrawInfo();
			isDrawInfoInited = true;
		}
	}

	/*-------- assets listener --------*/

	@Override
	public final void loadingCompleted () {
		mLoadPercentage = 1f;

		// execute completed action
		if (mCompletedAction != null) mCompletedAction.run();
		if (isAutoChangedScreen) changeScreen();
	}

	@Override
	public final void loadingOnProcess (int currentLoaded, int maxAssets) {
		mLoadPercentage = (float)currentLoaded / (float)maxAssets;
	}

	@Deprecated
	/** Loading screen cant have PopUp On it */
	public final void newPopupScreen (Class<? extends PopUpScreen> screenType) {
	}

	// ///////////////////////////////////////////////////////////////
	// abstract method
	// ///////////////////////////////////////////////////////////////
	/** List of available Transistion for this Loading Screen (lengh must dividable for 2), such as:
	 * <p>
	 * [Screen1, Screen2, Screen2, Screen1]
	 * <p>
	 * transistion: <b>Screen1->Screen2</b> and <b>Screen2->Screen1</b> will use this Screen */
	public final String[] TransistionName () {
		Class[] clazz = ScreenTransistionClass();
		if (clazz.length % 2 != 0) throw new TNTRuntimeException("Transistion length must dividable for 2");

		String[] result = new String[clazz.length / 2];
		for (int i = 0; i < result.length; i++) {
			String first = getScreenName(clazz[i * 2]);
			String second = getScreenName(clazz[i * 2 + 1]);
			result[i] = first + "_" + second;
		}
		return result;
	}

	private final String getScreenName (Class c) {
		if (c == null) return "";
		return c.getSimpleName();
	}

	/** This method defines the list of transistions use this screen. Such as:
	 * <p>
	 * [Screen1, Screen2, Screen2, Screen1]
	 * <p>
	 * transistion: <b>Screen1->Screen2</b> and <b>Screen2->Screen1</b> will use this Screen */
	public abstract Class[] ScreenTransistionClass ();

	protected Class[] genClassArray (Class... clazz) {
		return clazz;
	}
}
