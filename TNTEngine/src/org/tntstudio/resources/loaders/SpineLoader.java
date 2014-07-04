
package org.tntstudio.resources.loaders;

import org.tntstudio.graphics.Spine;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("rawtypes")
public class SpineLoader extends SynchronousAssetLoader<Spine, SpineLoader.SpineParameter> {

	public SpineLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Spine load (AssetManager assetManager, String fileName, FileHandle file, SpineParameter param) {
		TextureAtlas atlas = null;
		if (param != null && param.atlasFile != null)
			atlas = assetManager.get(param.atlasFile, TextureAtlas.class);
		else {
			String atlasFile = null;
			if (fileName.contains(Spine.JsonAlias))
				atlasFile = fileName.replace(Spine.JsonAlias, ".atlas");
			else if (fileName.contains(Spine.BinAlias)) atlasFile = fileName.replace(Spine.BinAlias, ".atlas");
			atlas = assetManager.get(atlasFile, TextureAtlas.class);
		}
		return new Spine(atlas, file);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SpineParameter param) {
		Array<AssetDescriptor> deps = null;
		deps = new Array<AssetDescriptor>();
		if (param != null && param.atlasFile != null)
			deps.add(new AssetDescriptor<TextureAtlas>(param.atlasFile, TextureAtlas.class));
		else {
			String atlasFile = null;
			if (fileName.contains(Spine.JsonAlias))
				atlasFile = fileName.replace(Spine.JsonAlias, ".atlas");
			else if (fileName.contains(Spine.BinAlias)) atlasFile = fileName.replace(Spine.BinAlias, ".atlas");
			deps.add(new AssetDescriptor<TextureAtlas>(atlasFile, TextureAtlas.class));
		}

		return deps;
	}

	public static class SpineParameter extends AssetLoaderParameters<Spine> {
		public String atlasFile;
	}
}
