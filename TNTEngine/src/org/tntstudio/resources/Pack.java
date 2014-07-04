
package org.tntstudio.resources;

import java.util.ArrayList;
import java.util.Comparator;

import org.tntstudio.interfaces.LifecycleListener;

/** Three param to serialize from json <li>Screen: name of coordinate skill <li>
 * Priority: order will this pack beloaded <li>Contexts: list of context will be loaded in this pack
 * @author trungnt13 */
@SuppressWarnings("rawtypes")
public class Pack extends Loadable implements LifecycleListener {
	public final String Screen;
	int Priority;
	final ArrayList<Context> mAllContexts;

	public int mNotLoadedAssets;
	private int mTotalAssets;

	/** Sort by the highest value first */
	public static final Comparator<Pack> PackComparator = new Comparator<Pack>() {
		@Override
		public int compare (Pack o1, Pack o2) {
			if (o1.Priority > o2.Priority)
				return -1;
			else if (o1.Priority < o2.Priority) return 1;
			return 0;
		}
	};

	public Pack (String screenName, int priority, ArrayList<Context> contexts) {
		Screen = screenName;
		Priority = priority;
		mAllContexts = contexts;

		mNotLoadedAssets = contexts == null ? 0 : contexts.size();
		for (Context context : contexts) {
			mTotalAssets += context.File.length;
		}
	}

	public Context findContext (String filePath) {
		for (Context co : mAllContexts) {
			if (co.File.equals(filePath)) return co;
		}
		return null;
	}

	@Override
	protected void loadInternal () {
		for (Context c : mAllContexts)
			c.load();
	}

	@Override
	protected void unloadInternal () {
		for (Context c : mAllContexts)
			c.unload();
	}

	@Override
	public boolean isLoaded () {
		for (Context c : mAllContexts) {
			if (!c.isLoaded()) return false;
		}
		return true;
	}

	void update () {
		mNotLoadedAssets = 0;

		for (Context c : mAllContexts)
			mNotLoadedAssets += c.notLoaded();

		Assets.CurrentAssets.needLoading(mNotLoadedAssets, mTotalAssets);
	}

	// ///////////////////////////////////////////////////////////////
	// lifecycle methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public String toString () {
		StringBuilder tmp = new StringBuilder();
		tmp.append("\n");
		tmp.append(Screen + " -Priority:" + Priority + " -Size:" + mAllContexts.size());
		tmp.append("   -Not Loaded:");
		for (Context c : mAllContexts) {
			if (!c.isLoaded()) tmp.append(c.toString() + " | ");
		}
		tmp.append("\n");
		return tmp.toString();
	}

	@Override
	public void dispose () {
		for (Context c : mAllContexts)
			c.dispose();

		mAllContexts.clear();
		mNotLoadedAssets = -1;
	}

	@Override
	public void resume () {
		for (Context c : mAllContexts)
			c.resume();
	}

	@Override
	public void pause () {
		for (Context c : mAllContexts)
			c.pause();
	}

	// ///////////////////////////////////////////////////////////////
	// helper method to apply param to context
	// ///////////////////////////////////////////////////////////////

	String getFilePathContain (String filePath, Class type) {
		for (Context c : mAllContexts) {
			if (c.Type.equals(type)) {
				for (FilePath s : c.File) {
					if (s.filePath.contains(filePath)) return s.filePath;
				}
			}
		}
		return null;
	}

	void applyDefaultParameters (AssetsDefinition def) {
		for (Context c : mAllContexts) {
			if (c.Params == null) c.Params = def.findDefaultParams(c.Type);
		}
	}
}
