
package org.tntstudio.resources;

import com.badlogic.gdx.assets.AssetLoaderParameters;

/** Define the json form of resource file
 * @author trungnt13 */
@SuppressWarnings("rawtypes")
public final class AssetsDefinition {
	final Pack[] Packs;
	final ParamDescription[] DefParams;

	public AssetsDefinition (Pack[] p, ParamDescription[] pd) {
		Packs = p;
		DefParams = pd;
	}

	public AssetLoaderParameters findDefaultParams (Class type) {
		for (ParamDescription p : DefParams) {
			if (p.Type.equals(type)) return p.Params;
		}
		return null;
	}

	/** @author trungnt13 */
	public static final class ParamDescription {
		final Class Type;
		final AssetLoaderParameters Params;

		public ParamDescription (Class type, AssetLoaderParameters param) {
			Type = type;
			Params = param;
		}
	}
}
