
package org.tntstudio.resources;

import static org.tntstudio.Const.Assets.DefaultLoaderParameters;
import static org.tntstudio.Const.Assets.DefaultResourceAlias;
import static org.tntstudio.Const.Assets.DefaultSupportingAssets;
import static org.tntstudio.Const.Assets.PriorityRange;

import java.util.ArrayList;

import org.tntstudio.Const.ErrorCode;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.LoadingScreen;
import org.tntstudio.core.Screen;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.Spine;
import org.tntstudio.interfaces.AssetsListener;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.interfaces.PackLoadedListener;
import org.tntstudio.resources.AssetsDefinition.ParamDescription;
import org.tntstudio.resources.Graphics.GraphicsCreator;
import org.tntstudio.resources.loaders.SpineLoader;
import org.tntstudio.utils.Error;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class Assets implements LifecycleListener, AssetErrorListener {
	static Assets CurrentAssets = null;

	private final AssetManager mAssetManager;

	public final AssetsCreator Creator = new AssetsCreator();
	// resolve simple class name into full Class
	private final ObjectMap<String, Class<?>> mClassNameResolver = new ObjectMap<String, Class<?>>();

	/*-------- resource classification --------*/

	private Class<?>[] mResourceType;
	private Class<?>[] mResourceParams;

	/*-------- screen loading param --------*/

	private boolean isStartLoadingProcess = false;

	private int mNotLoadedAssets = 0;
	private int mTotalNeedAssets = 0;

	private final Array<Pack> mNeedUnloadedAssets = new Array<Pack>();
	private final Array<Pack> mNeedLoadedAssets = new Array<Pack>();

	private Pack mCurrentLoadingPack = null;

	private AssetsListener mListener;

	private final Array<Pack> mLastLoadedData = new Array<Pack>();

	private ObjectMap<Pack, PackLoadedListener> mPackListenerMap = new ObjectMap<Pack, PackLoadedListener>();

	/*-------- pack and context assets --------*/
	private final ObjectMap<String, Pack> mScreenResourceMap = new ObjectMap<String, Pack>();

	/*-------- constructor --------*/
	public Assets () {
		CurrentAssets = this;

		mAssetManager = new AssetManager(FilePath.FileHandleResolver);

		mAssetManager.setErrorListener(this);
		mAssetManager.setLoader(Spine.class, new SpineLoader(FilePath.FileHandleResolver));

		Texture.setAssetManager(mAssetManager);
	}

	/*-------- laoding process manager --------*/
	public void startLoadingProcess (AssetsListener listener, ArrayList<String> unloadData, ArrayList<String> loadData) {
		this.mListener = listener;
		isStartLoadingProcess = true;
		/*-------- clear old data --------*/
		mNeedUnloadedAssets.clear();
		mNeedLoadedAssets.clear();

		mNotLoadedAssets = 0;
		mTotalNeedAssets = 0;

		/*-------- add new info --------*/

		for (String s : unloadData) {
			Pack unload = mScreenResourceMap.get(s);
			if (unload != null) mNeedUnloadedAssets.add(unload);
		}

		boolean isContainDefault = false;
		for (String s : loadData) {
			if (s.equals(DefaultResourceAlias)) isContainDefault = true;

			Pack load = mScreenResourceMap.get(s);
			if (load != null) mNeedLoadedAssets.add(load);
		}

		if (!isContainDefault) mNeedLoadedAssets.add(mScreenResourceMap.get(DefaultResourceAlias));
		/*-------- sort data in order --------*/

		mNeedUnloadedAssets.sort(Pack.PackComparator);
		mNeedLoadedAssets.sort(Pack.PackComparator);

		mLastLoadedData.clear();
		mLastLoadedData.addAll(mNeedLoadedAssets);

	}

	public void registerPackListener (Screen screen, PackLoadedListener listner) {
		Pack p = mScreenResourceMap.get(screen.Name());
		if (p != null) mPackListenerMap.put(p, listner);
	}

	public void unregisterPackListener (Screen screen) {
		Pack p = mScreenResourceMap.get(screen.Name());
		if (p != null) mPackListenerMap.remove(p);
	}

	public void deletePack (Screen screen) {
		Pack p = mScreenResourceMap.get(screen.Name());
		if (p == null) return;

		p.unload();

		mNeedLoadedAssets.removeValue(p, true);
		mPackListenerMap.remove(p);
		mScreenResourceMap.remove(p.Screen);
		mLastLoadedData.removeValue(p, true);
	}

	public boolean isScreenLoaded (Screen screen) {
		return isScreenLoaded(screen.Name());
	}

	public boolean isScreenLoaded (String screen) {
		boolean isLoaded = true;
		Pack def = mScreenResourceMap.get(DefaultResourceAlias);
		if (def != null) isLoaded &= def.isLoaded();

		Pack p = mScreenResourceMap.get(screen);
		if (p == null) return isLoaded;
		isLoaded &= p.isLoaded();

// D.out("Assets Status : " + mAssetManager.getLoadedAssets() + " " + mAssetManager.getQueuedAssets());
// for (String s : mAssetManager.getAssetNames()) {
// D.out(s + " & " + mAssetManager.isLoaded(s));
// }
// for (Pack pack : mLastLoadedData) {
// D.out(pack + " - Not loaded - " + pack.isLoaded());
// }
// D.ln();
		return isLoaded;
	}

	public void update () {
		if (!isStartLoadingProcess) return;
		mNotLoadedAssets = 0;
		mTotalNeedAssets = 0;

// D.out("Assets Status : " + mAssetManager.getLoadedAssets() + " " + mAssetManager.getQueuedAssets());

		// update until need load pack size = 0
		if (mNeedLoadedAssets.size > 0) {
// D.out("Current  " + mCurrentLoadingPack);
// if mCurrentLoadingPack = null or it is loaded, change to new one
			if (mCurrentLoadingPack == null || mCurrentLoadingPack.isLoaded()) {
				// dequeue until find the unload pack
				while (mNeedLoadedAssets.size > 0 && (mCurrentLoadingPack = mNeedLoadedAssets.removeIndex(0)).isLoaded()) {
					// still need to involve listener here
				}
				if (mCurrentLoadingPack != null) mCurrentLoadingPack.load();
// D.out("Picked  " + mCurrentLoadingPack);
			}
		}

		for (Pack p : mLastLoadedData)
			p.update();

// D.out("Assets Status : " + mAssetManager.getLoadedAssets() + " " + mAssetManager.getQueuedAssets());
// D.out("Loading Status : " + mNotLoadedAssets + " " + mTotalNeedAssets + " " + mNeedLoadedAssets.size);

		// finish loading
		boolean update = mAssetManager.update();

		// check listener
		if (mCurrentLoadingPack != null && mCurrentLoadingPack.isLoaded()) {
			PackLoadedListener listener = mPackListenerMap.remove(mCurrentLoadingPack);
			if (listener != null) listener.packCompletedLoading();
		}

		if (update && mNotLoadedAssets == 0 && mNeedLoadedAssets.size == 0) {
			if (mListener != null) {
				mListener.loadingCompleted();
				// reset old data
				mListener = null;
				isStartLoadingProcess = false;

				// unload old screen data after all data is loaded
				for (Pack assets : mNeedUnloadedAssets)
					assets.unload();
				mNeedUnloadedAssets.clear();
			}
		}
		// load on progress
		else if (mListener != null) {
			mListener.loadingOnProcess(mTotalNeedAssets - mNotLoadedAssets, mTotalNeedAssets);
		}

// D.ln();
	}

	/*-------- assets manager --------*/

	<T> void load (String file, Class<T> type) {
		mAssetManager.load(file, type);
	}

	<T> void load (String file, Class<T> type, AssetLoaderParameters<T> param) {
		mAssetManager.load(file, type, param);
	}

	public void unload (String file) {
		mAssetManager.unload(file);
	}

	public <T> T get (String file, Class<T> type) {
		/*-------- process file path --------*/
		String filePath = null;

		// current pack (get only the first one)
		for (Pack p : mLastLoadedData) {
			filePath = p.getFilePathContain(file, type);
			if (filePath != null) break;
		}

		if (filePath == null) filePath = file;

		/*-------- get resource  --------*/
		try {
			return mAssetManager.get(filePath, type);
		} catch (Exception e) {
			// check if file exist
			if (!FilePath.FileHandleResolver.resolve(filePath).file().exists()) return null;
			// exist start loading process
			mAssetManager.load(filePath, type);
			while (!mAssetManager.isLoaded(filePath))
				mAssetManager.update();
			return mAssetManager.get(filePath, type);
		}
	}

	public <T> boolean isLoaded (String file, Class<T> type) {
		return mAssetManager.isLoaded(file, type);
	}

	public <T> boolean isLoaded (String file) {
		return mAssetManager.isLoaded(file);
	}

	public final Class<? extends AssetLoaderParameters<?>> getParamType (Class<?> type) {
		for (int i = 0; i < mResourceType.length; i++) {
			if (mResourceType[i].equals(type)) return (Class<? extends AssetLoaderParameters<?>>)mResourceParams[i];
		}
		return null;
	}

	public final Class<?> getClassFromName (String name) {
		return mClassNameResolver.get(name);
	}

	// ///////////////////////////////////////////////////////////////
	// methods to communicate with Pack and Context
	// ///////////////////////////////////////////////////////////////

	void needLoading (int needLoadData, int totalData) {
		mNotLoadedAssets += needLoadData;
		mTotalNeedAssets += totalData;
	}

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void error (AssetDescriptor asset, Throwable throwable) {
		Top.tgame.putError(new Error(ErrorCode.ASSETS_ERROR, asset, throwable, null));
	}

	@Override
	public void dispose () {
		mAssetManager.clear();
		mNeedLoadedAssets.clear();
		mNeedUnloadedAssets.clear();
		mScreenResourceMap.clear();
		
		//this is important, clear last static data
		CurrentAssets = null;
	}

	@Override
	public void resume () {
// /*-------- if last loaded data == null dont need to do anythings--------*/
// if (mLastLoadedData == null) return;
// mNeedLoadedAssets.clear();
//
// /*-------- checking process --------*/
// boolean isNeedLoadingNow = false;
// for (Pack p : mLastLoadedData) {
// // if one data not loaded, everything need to reload now
// if (!p.isLoaded()) {
// isNeedLoadingNow = true;
// mNeedLoadedAssets.add(p);
// }
// }
//
// /*-------- sort data in order --------*/
// if (isNeedLoadingNow) {
// mNeedLoadedAssets.sort(Pack.PackComparator);
// isStartLoadingProcess = true;
// } else
// mNeedLoadedAssets.clear();
	}

	@Override
	public void pause () {

	}

	// ///////////////////////////////////////////////////////////////
	// class
	// ///////////////////////////////////////////////////////////////
	/** @author trungnt13 */
	public final class AssetsCreator {
		private AssetsCreator () {
		}

		public GraphicsCreator initAssets (Class<?>[] resourcesType, Class<?>[] coordinateParamLists, String... resourceFile) {
			/*-------- load class list --------*/
			if (resourcesType == null || coordinateParamLists == null || resourcesType.length != coordinateParamLists.length) {
				mResourceType = DefaultSupportingAssets;
				mResourceParams = DefaultLoaderParameters;
			} else {
				mResourceType = resourcesType;
				mResourceParams = coordinateParamLists;
			}

			for (Class c : resourcesType) {
				mClassNameResolver.put(c.getSimpleName(), c);
			}

			for (Class c : coordinateParamLists) {
				mClassNameResolver.put(c.getSimpleName(), c);
			}

			for (Class c : org.tntstudio.Const.FastClassNameResolverSupport) {
				mClassNameResolver.put(c.getSimpleName(), c);
			}
			/*-------- load pack --------*/
			for (String s : resourceFile) {
				AssetsDefinition definition = Top.json.fromJson(AssetsDefinition.class, Gdx.files.internal(s).readString());
				Pack[] pack = definition.Packs;
				for (Pack p : pack) {
					String currentName = p.Screen;
					// more priority for default
					if (currentName.equals(DefaultResourceAlias)) {
						p.Priority += PriorityRange;
					} else {
						Class type = Top.tgame.getClassTypeForScreenName(currentName);
						if (type == null)
							throw new TNTRuntimeException(
								"No legal BaseScreen or LoadingScreen or Resource alias (PopUpScreen is not allowed) are found for the name: "
									+ p.Screen);
						// more and more priority for loading (carefull here, loadingType must be checked first)
						else if (LoadingScreen.class.isAssignableFrom(type))
							p.Priority += 2 * PriorityRange;
						// less priority for normal screen
						else if (BaseScreen.class.isAssignableFrom(type)) p.Priority -= PriorityRange;
					}
					// apply default parameter
					p.applyDefaultParameters(definition);
					mScreenResourceMap.put(currentName, p);
				}
			}

			/*-------- check default pack always have --------*/
			if (mScreenResourceMap.get(DefaultResourceAlias) == null) {
				mScreenResourceMap.put(DefaultResourceAlias, new Pack(DefaultResourceAlias, 0, new ArrayList<Context>()));
			}

			return Graphics.CurrentGraphics.Creator;
		}
	}

	public static final class ClassJson implements Serializer<Class> {
		@Override
		public void write (Json json, Class object, Class knownType) {
			json.writeValue(object.getName());
		}

		@Override
		public Class read (Json json, JsonValue jsonData, Class type) {
			String value = jsonData.asString();
			// check if resource type
			Class tmp = CurrentAssets.getClassFromName(value);
			if (tmp != null) return tmp;
			// otherwise return normal class
			try {
				return Class.forName(value);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	}

	public static final class ContextJson implements Serializer<Context> {
		private static final String File = "File";
		private static final String Params = "Params";
		private static final String Type = "Type";

		@Override
		public void write (Json json, Context src, Class knownType) {
			json.writeObjectStart();
			json.writeValue(File, json.toJson(src.File, String[].class));
			json.writeValue(Type, json.toJson(src.Type, src.Type.getClass()));
			if (src.Params != null) json.writeValue(Params, json.toJson(src.Params, src.Params.getClass()));
			json.writeObjectEnd();
		}

		@Override
		public Context read (Json json, JsonValue obj, Class clazz) {
			FilePath[] file = json.fromJson(FilePath[].class, obj.get(File).toString());
			Class type = json.fromJson(Class.class, obj.get(Type).asString());

			AssetLoaderParameters<?> params = null;
			JsonValue pr = obj.get(Params);
			if (pr != null) {
				params = json.fromJson(CurrentAssets.getParamType(type), pr.toString());
			}

			return new Context(file, type, params);
		}
	}

	public static final class PackJson implements Serializer<Pack> {
		private static final String Screen = "Screen";
		private static final String Priority = "Priority";
		private static final String Data = "Data";

		@Override
		public void write (Json json, Pack object, Class knownType) {
			json.writeObjectStart();
			json.writeValue(Screen, object.Screen);
			json.writeValue(Priority, object.Priority);
			json.writeValue(Data, object.mAllContexts);
			json.writeObjectEnd();
		}

		@Override
		public Pack read (Json json, JsonValue obj, Class type) {
			String screen = obj.get(Screen).asString();
			int priority = obj.get(Priority).asInt();
			final ArrayList<Context> data = new ArrayList<Context>();
			JsonValue val = obj.get(Data);
			for (int i = 0; i < val.size; i++) {
				data.add(json.fromJson(Context.class, val.get(i).toString()));
			}

			return new Pack(screen, priority, data);
		}
	}

	public static final class ParamDescriptionJson implements Serializer<ParamDescription> {
		private static final String Type = "Type";
		private static final String Params = "Params";

		@Override
		public void write (Json json, ParamDescription src, Class knownType) {
			json.writeObjectStart();
			json.writeValue(Type, json.toJson(src.Type, Class.class));
			json.writeValue(Params, json.toJson(src.Params, src.Params.getClass()));
			json.writeObjectEnd();
		}

		@Override
		public ParamDescription read (Json json, JsonValue obj, Class clazz) {
			Class type = json.fromJson(Class.class, obj.get(Type).asString());
			Class paramType = CurrentAssets.getParamType(type);
			AssetLoaderParameters param = json.fromJson(paramType, obj.get(Params).toString());

			return new ParamDescription(type, param);
		}

	}

	public static final class AssetsDefinitionJson implements Serializer<AssetsDefinition> {
		private static final String DefParams = "DefParams";
		private static final String Packs = "Packs";

		@Override
		public void write (Json json, AssetsDefinition src, Class knownType) {
			json.writeObjectStart();
			json.writeValue(DefParams, json.toJson(src.DefParams, ParamDescription[].class));
			json.writeValue(Packs, json.toJson(src.Packs, Pack[].class));
			json.writeObjectEnd();
		}

		@Override
		public AssetsDefinition read (Json json, JsonValue obj, Class type) {

			JsonValue p = obj.get(Packs);

			Pack[] packs = new Pack[p.size];
			for (int i = 0; i < p.size; i++) {
				packs[i] = json.fromJson(Pack.class, p.get(i).toString());
			}

			p = obj.get(DefParams);
			ParamDescription[] defParam = new ParamDescription[p.size];
			for (int i = 0; i < p.size; i++) {
				defParam[i] = json.fromJson(ParamDescription.class, p.get(i).toString());
			}

			return new AssetsDefinition(packs, defParam);
		}

	}

}
