
package com.esotericsoftware.spine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.esotericsoftware.spine.Skin.Key;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;

public final class SkelUtils {
	public static void scale (SkeletonData skel, float newScale) {
		float scale = newScale / skel.originScale;
		skel.originScale = newScale;

		// resize bone
		final Array<BoneData> bones = skel.bones;
		for (BoneData bone : bones) {
			bone.length *= scale;
			bone.x *= scale;
			bone.y *= scale;
		}

		// resize attachment
		for (Skin s : skel.skins) {
			scaleSkin(s, scale);
		}

		// scale animation
// for (Animation a : skel.animations) {
// for (Timeline t : a.getTimelines()) {
// if (t instanceof TranslateTimeline || t instanceof ScaleTimeline) {
// TranslateTimeline trans = (TranslateTimeline)t;
// for (int i = 0; i < trans.frames.length / 3; i++) {
// trans.frames[3 * i + 1] *= scale;
// trans.frames[3 * i + 2] *= scale;
// }
// }
// }
// }
	}

	private static void scaleSkin (Skin skin, float scale) {
		final ObjectMap<Key, Attachment> attachments = skin.attachments;
		final Values<Attachment> values = attachments.values();
		for (Attachment a : values) {
			if (a instanceof RegionAttachment) {
				final RegionAttachment att = (RegionAttachment)a;
				att.setX(att.getX() * scale);
				att.setY(att.getY() * scale);
				att.setWidth(att.getWidth() * scale);
				att.setHeight(att.getHeight() * scale);
				att.updateOffset();
			} else if (a instanceof BoundingBoxAttachment) {
				final BoundingBoxAttachment att = (BoundingBoxAttachment)a;
				scaleFloatArray(att.getVertices(), scale);
			} else if (a instanceof MeshAttachment) {
				final MeshAttachment att = (MeshAttachment)a;
				att.setMesh(scaleFloatArray(att.getVertices(), scale), att.getTriangles(), att.getUVS());
				att.setWidth(att.getWidth() * scale);
				att.setHeight(att.getHeight() * scale);
			}
		}
	}

	private static float[] scaleFloatArray (float[] array, float scale) {
		for (int j = 0; j < array.length; j++) {
			array[j] *= scale;
		}
		return array;
	}

}
