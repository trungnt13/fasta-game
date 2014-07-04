
package org.tntstudio.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;
import com.esotericsoftware.spine.attachments.AttachmentLoader;

public final class Spine implements Disposable {
	/*-------- static field --------*/
	private static final SkeletonJson SKELETON_JSON = new SkeletonJson();
	private static final SkeletonBinary SKELETON_BINARY = new SkeletonBinary();

	public static final String JsonAlias = ".json";
	public static final String BinAlias = ".skel";

	/*-------- general information --------*/
	private final AttachmentLoader mAttachmentLoader;
	private final String mJsonData;
	private final FileHandle mBinFile;
	public final String Name;

	/*-------- skeleton data management --------*/
	private final ObjectMap<Float, SkeletonData> mSkeletonMap = new ObjectMap<Float, SkeletonData>();

	// ///////////////////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////////////////
	public Spine (TextureAtlas atlas, FileHandle data) {
		/*-------- load attachment --------*/
		mAttachmentLoader = new AtlasAttachmentLoader(atlas);

		/*-------- load data --------*/
		Name = data.nameWithoutExtension();

		if (data.name().contains(JsonAlias)) {
			mJsonData = data.readString();
			mBinFile = null;
		} else if (data.name().contains(BinAlias)) {
			mBinFile = data;
			mJsonData = null;
		} else {
			mBinFile = null;
			mJsonData = null;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// main methods
	// ///////////////////////////////////////////////////////////////

	public final Skeleton createSkeleton (float scale) {
		SkeletonData val = mSkeletonMap.get(scale);
		if (val == null) {
			val = loadDirectly(scale);
			mSkeletonMap.put(scale, val);
		}
		return new Skeleton(val);
	}

	private final SkeletonData loadDirectly (float scale) {
		if (mJsonData != null) {
			SKELETON_JSON.setAttachmentLoader(mAttachmentLoader);
			SKELETON_JSON.setScale(scale);
			return SKELETON_JSON.readSkeletonData(Name, mJsonData);
		} else if (mBinFile != null) {
			SKELETON_BINARY.setAttachmentLoader(mAttachmentLoader);
			SKELETON_BINARY.setScale(scale);
			return SKELETON_BINARY.readSkeletonData(mBinFile);
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void dispose () {
		for (SkeletonData data : mSkeletonMap.values()) {
			data.getAnimations().clear();
			data.getEvents().clear();
			data.getBones().clear();
			data.getSkins().clear();
			data.getSlots().clear();
		}
		mSkeletonMap.clear();
	}
}
