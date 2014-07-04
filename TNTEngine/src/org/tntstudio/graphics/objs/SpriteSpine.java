
package org.tntstudio.graphics.objs;

import org.tntstudio.graphics.Animator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.SkelUtils;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

/** @author trungnt13 */
public class SpriteSpine extends SpriteBase implements Animator, AnimationStateListener {
	/*-------- static --------*/

	public static final SkeletonRenderer RENDERER = new SkeletonRenderer();
	public static final SkeletonRendererDebug RENDERER_DEBUG = new SkeletonRendererDebug();

	/*-------- local --------*/
	private Skeleton mSkeleton;
	private final SpriteSpineAnimation mAnimation;
	private final AnimationStateData mAnimationData;

	private final SkeletonBounds mSkeletonBounds;
	private boolean isUpdatedBounds = false;

	private boolean isDebug = false;

	/*-------- animation params --------*/
	private float mStateTime;
	private boolean isReversed = false;
	private boolean isRunning = false;

	public SpriteSpine () {
		mAnimationData = new AnimationStateData();
		mAnimationData.setDefaultMix(0.3f);
		mAnimation = new SpriteSpineAnimation(mAnimationData);
		mSkeletonBounds = new SkeletonBounds();
	}

	public SpriteSpine bind (Skeleton skeleton) {
		mSkeleton = skeleton;
		mAnimationData.setSkeletonData(mSkeleton.getData());
		mAnimation.clearTracks();
		updateBounds();

		mSkeleton.setToSetupPose();
		mSkeleton.updateWorldTransform();

		active();
		return this;
	}

	private void updateBounds () {
		if (isUpdatedBounds) return;
		mSkeletonBounds.update(mSkeleton, true);
		isUpdatedBounds = true;
	}

	@Override
	protected void resetInternal () {
	}

	@Override
	protected void disposeInternal () {
	}

	// ///////////////////////////////////////////////////////////////
	// setter
	// ///////////////////////////////////////////////////////////////

	@Override
	public void setBounds (float x, float y, float width, float height) {
		setPosition(x, y);
		setSize(width, height);
	}

	@Override
	public void setSize (float width, float height) {
		updateBounds();
		float scaleX = width / mSkeletonBounds.getWidth();
		float scaleY = height / mSkeletonBounds.getHeight();

		setScale(scaleX, scaleY);

		isUpdatedBounds = false;
	}

	@Override
	public void setPosition (float x, float y) {
		mSkeleton.setX(x);
		mSkeleton.setY(y);

		isUpdatedBounds = false;
	}

	@Override
	public void setX (float x) {
		mSkeleton.setX(x);

		isUpdatedBounds = false;
	}

	@Override
	public void setY (float y) {
		mSkeleton.setY(y);

		isUpdatedBounds = false;
	}

	@Override
	public void translate (float xAmount, float yAmount) {
		translateX(xAmount);
		translateY(yAmount);

		isUpdatedBounds = false;
	}

	@Override
	public void translateX (float xAmount) {
		mSkeleton.setX(mSkeleton.getX() + xAmount);

		isUpdatedBounds = false;
	}

	@Override
	public void translateY (float yAmount) {
		mSkeleton.setY(mSkeleton.getY() + yAmount);

		isUpdatedBounds = false;
	}

	@Override
	public void setOrigin (float originX, float originY) {
		mSkeleton.getRootBone().setX(originX);
		mSkeleton.getRootBone().setX(originY);
		mSkeleton.setToSetupPose();
	}

	@Deprecated
	public void setRotation (float degree) {
		// TODO Auto-generated method stub

	}

	@Deprecated
	public void rotate (float degree) {
	}

	@Override
	public void setScale (float scaleXY) {
		SkelUtils.scale(mSkeleton.getData(), scaleXY);

		isUpdatedBounds = false;
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		SkelUtils.scale(mSkeleton.getData(), (scaleX + scaleY) / 2);

		isUpdatedBounds = false;
	}

	@Override
	public void scale (float amount) {
		final SkeletonData data = mSkeleton.getData();
		SkelUtils.scale(data, data.getOriginScale() + amount);

		isUpdatedBounds = false;
	}

	@Override
	public void setColor (float r, float g, float b, float a) {
		mSkeleton.getColor().set(r, g, b, a);
	}

	@Override
	public void setColor (float a) {
		final Color color = mSkeleton.getColor();
		color.a *= a;
	}

	@Override
	public void setColor (Color color) {
		mSkeleton.getColor().set(color);
	}

	@Override
	public void flip (boolean flipX, boolean flipY) {
		super.flip(flipX, flipY);
		mSkeleton.setFlipX(isFlipX());
		mSkeleton.setFlipY(isFlipY());
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	@Override
	public float[] getVertices () {
		return null;
	}

	@Override
	public Array<FloatArray> getBounding () {
		updateBounds();
		return mSkeletonBounds.getPolygons();
	}

	@Override
	public float getX () {
		return mSkeleton.getX();
	}

	@Override
	public float getCenterX () {
		updateBounds();

		return (mSkeletonBounds.getMinX() + mSkeletonBounds.getMaxX()) / 2;
	}

	@Override
	public float getY () {
		return mSkeleton.getY();
	}

	@Override
	public float getCenterY () {
		updateBounds();
		return (mSkeletonBounds.getMinY() + mSkeletonBounds.getMaxY()) / 2;
	}

	@Override
	public float getWidth () {
		updateBounds();
		return mSkeletonBounds.getWidth();
	}

	@Override
	public float getHeight () {
		updateBounds();
		return mSkeletonBounds.getHeight();
	}

	@Override
	public float getOriginX () {
		return mSkeleton.getRootBone().getX();
	}

	@Override
	public float getOriginY () {
		return mSkeleton.getRootBone().getY();
	}

	@Deprecated
	public float getRotation () {
		return 0;
	}

	@Override
	public float getScaleX () {
		return mSkeleton.getData().getOriginScale();
	}

	@Override
	public float getScaleY () {
		return mSkeleton.getData().getOriginScale();
	}

	@Override
	public Color getColor () {
		return mSkeleton.getColor();
	}

	// ///////////////////////////////////////////////////////////////
	// animation controller
	// ///////////////////////////////////////////////////////////////

	public SpriteSpine setMix (String from, String to, float duration) {
		mAnimationData.setMix(from, to, duration);
		return this;
	}

	public TrackEntry setAnimation (int trackIdx, String animation, boolean loop) {
		return mAnimation.setAnimation(trackIdx, animation, loop);
	}

	public TrackEntry addAnimation (int trackIdx, String animation, boolean loop) {
		return mAnimation.addAnimation(trackIdx, animation, loop, 0);
	}

	public TrackEntry addAnimation (int trackIdx, String animation, boolean loop, float delay) {
		return mAnimation.addAnimation(trackIdx, animation, loop, delay);
	}

	@Override
	public Animator setLoop (boolean loop) {
		return this;
	}

	@Override
	public Animator setPlaybackSpeed (float playback) {
		mAnimation.setTimeScale(playback);
		return this;
	}

	@Override
	public Animator start () {
		start(mStateTime, false);
		return this;
	}

	@Override
	public Animator start (float statetime, boolean reversed) {
		isRunning = true;
		mStateTime = statetime;

		// set state time and reversed
		final Array<TrackEntry> tracks = mAnimation.getTrackList();
		TrackEntry tmpTrack = null;
		for (TrackEntry t : tracks) {
			tmpTrack = t;
			while (tmpTrack != null) {
				tmpTrack.setTime(statetime % tmpTrack.getAnimation().getOriginDuration());
				tmpTrack.setReverse(reversed);
				tmpTrack = tmpTrack.getNext();
			}
		}
		return this;
	}

	@Override
	public Animator start (float statetime) {
		start(statetime, false);
		return this;
	}

	@Override
	public Animator stop () {
		isRunning = false;
		mAnimation.clearTracks();
		mStateTime = 0;
		return this;
	}

	@Override
	public Animator pause () {
		isRunning = false;
		return this;
	}

	@Override
	public boolean isRunning () {
		return isRunning;
	}

	@Override
	public boolean isLooping () {
		return false;
	}

	@Override
	public float getPlaybackSpeed () {
		return mAnimation.getTimeScale();
	}

	@Override
	public boolean isReversed () {
		return isReversed;
	}

	@Override
	public float getStateTime () {
		return mStateTime;
	}

	@Override
	public float getTotalDuration () {
		float maxTotal = 0;
		float tmp = 0;
		final Array<TrackEntry> a = mAnimation.getTrackList();
		for (TrackEntry trackEntry : a) {
			if (trackEntry == null) continue;
			tmp = getDuration(trackEntry);
			if (maxTotal < tmp) maxTotal = tmp;
		}
		return maxTotal;
	}

	private final float getDuration (TrackEntry entry) {
		float duration = 0;
		TrackEntry tmp = entry;
		while (tmp != null) {
			duration += tmp.getEndTime();
			tmp = tmp.getNext();
		}
		return duration;
	}

	// ///////////////////////////////////////////////////////////////
	// processor methods
	// ///////////////////////////////////////////////////////////////
	@Override
	protected void updateInternal (float delta) {
		mSkeleton.update(delta);
		if (isRunning) {
			mAnimation.update(delta);
			mAnimation.apply(mSkeleton);
			mSkeleton.updateWorldTransform();
		}
	}

	@Override
	protected void drawInternal (Batch batch) {
		RENDERER.draw(batch, mSkeleton);
		if (isDebug) RENDERER_DEBUG.draw(mSkeleton);
	}

	public void setDebug (boolean isDebug) {
		this.isDebug = isDebug;
	}

	// ///////////////////////////////////////////////////////////////
	// helper class
	// ///////////////////////////////////////////////////////////////

	private final class SpriteSpineAnimation extends AnimationState {
		public SpriteSpineAnimation (AnimationStateData data) {
			super(data);
		}

		@Override
		public void update (float delta) {
			super.update(delta);
			isUpdatedBounds = false;
			mStateTime += delta * getTimeScale();
		}
	}

	// ///////////////////////////////////////////////////////////////
	// animation state listener
	// ///////////////////////////////////////////////////////////////

	@Override
	public void event (int trackIndex, Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void complete (int trackIndex, int loopCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start (int trackIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void end (int trackIndex) {
		// TODO Auto-generated method stub

	}
}
