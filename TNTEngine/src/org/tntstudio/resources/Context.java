
package org.tntstudio.resources;

import org.tntstudio.interfaces.LifecycleListener;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.MusicLoader.MusicParameter;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;

@SuppressWarnings("rawtypes")
public final class Context extends Loadable implements LifecycleListener {

	public final FilePath[] File;
	public final Class Type;
	AssetLoaderParameters Params;

	public Context (FilePath[] file, Class type, AssetLoaderParameters param) {
		this.File = file;
		this.Type = type;

		this.Params = param;
	}

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	/*-------- loadable --------*/

	@SuppressWarnings("unchecked")
	protected void loadInternal () {
		if (Params == null) {
			for (FilePath s : File)
				Assets.CurrentAssets.load(s.filePath, Type);
		} else {
			if (Params.getClass().equals(MusicParameter.class) || Params.getClass().equals(SoundParameter.class))
				Params.loadedCallback = Audio.CurrentAudio;
			for (FilePath s : File)
				Assets.CurrentAssets.load(s.filePath, Type, Params);
		}
	}

	protected void unloadInternal () {
		for (FilePath s : File)
			Assets.CurrentAssets.unload(s.filePath);
	}

	public boolean isLoaded () {
		for (FilePath s : File)
			if (!Assets.CurrentAssets.isLoaded(s.filePath)) return false;
		return true;
	}

	int notLoaded () {
		int i = 0;
		for (FilePath s : File)
			if (!Assets.CurrentAssets.isLoaded(s.filePath)) i++;
		return i;
	}

	/*-------- lifecycle --------*/

	@Override
	public void dispose () {
		unload();
	}

	@Override
	public void resume () {
	}

	@Override
	public void pause () {
	}

	@Override
	public String toString () {
		StringBuilder builder = new StringBuilder();
		for (FilePath f : File) {
			builder.append(f.filePath + " | ");
		}
		builder.append(" Type:" + Type.getSimpleName() + " | ");
		builder.append(" Params:" + Params);
		return builder.toString();
	}
}
