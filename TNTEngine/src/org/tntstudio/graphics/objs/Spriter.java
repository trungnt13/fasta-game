
package org.tntstudio.graphics.objs;

import org.tntstudio.TNTRuntimeException;
import org.tntstudio.graphics.Animator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;

public class Spriter extends SpriteBase implements Animator {

	public class TrackEntry extends Array<SpriteRelativeAnimation> {
		public void draw (Batch batch) {
			for (SpriteRelativeAnimation ani : this) {
				if (ani.isActive()) ani.draw(batch);
			}
		}
	}

	public static final String ROOTNAME = "root";
	SpriteRelativeAnimation root;
	Array<TrackEntry> tracks = new Array<Spriter.TrackEntry>();
	ObjectMap<String, SpriteRelativeAnimation> map = new ObjectMap<String, SpriteRelativeAnimation>();

	public Spriter () {
	}

	public Spriter bind (SpriteRelativeAnimation ani, int trackIndex, boolean active, float distanceX, float distanceY) {
		if (root == null) {
			root = ani;
			root.setActive(true);
			bindAnimation(ani, trackIndex);
			setDistanceInstance(ani, true, 0, 0);
		} else {
			bindAnimation(ani, trackIndex);
			setDistanceInstance(ani, active, distanceX, distanceY);
		}
		active();
		return this;
	}

	void bindAnimation (SpriteRelativeAnimation spriteAnimation, int trackIndex) {
		if (trackIndex >= tracks.size) {
			for (int i = tracks.size; i <= trackIndex; i++) {
				tracks.add(new TrackEntry());
			}
		}
		tracks.get(trackIndex).add(spriteAnimation);
		map.put(spriteAnimation.name, spriteAnimation);
		active();
	}

	void setDistanceInstance (SpriteRelativeAnimation spriteAnimation, boolean isDependent, float distanceX, float distanceY) {
		if (map.containsKey(spriteAnimation.name)) {
			spriteAnimation.bindOriginRoot(root, isDependent, distanceX, distanceY);
		}
	}

	public SpriteRelativeAnimation getAnimation (String name) {
		return map.get(name);
	}

	@Override
	public void setBounds (float x, float y, float width, float height) {
		if (root == null) throw new TNTRuntimeException("must @bindRoot() to set measures");
		float percentWidth = width / root.getWidth();
		float percentHeight = height / root.getHeight();
		root.setBounds(x, y, width, height);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setPosition(x + ani.getRelativeX(), y + ani.getRelativeY());
			ani.setSize(ani.getWidth() * percentWidth, ani.getHeight() * percentHeight);
		}
	}

	@Override
	public void setSize (float width, float height) {
		if (root == null) throw new TNTRuntimeException("must @bindRoot() to set measures");
		float percentWidth = width / root.getWidth();
		float percentHeight = height / root.getHeight();
		root.setSize(width, height);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setSize(ani.getWidth() * percentWidth, ani.getHeight() * percentHeight);
		}
	}

	@Override
	public void setPosition (float x, float y) {
		if (root == null) throw new TNTRuntimeException("must @bindRoot() to set measures");
		root.setPosition(x, y);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setPosition(x + ani.getRelativeX(), y + ani.getRelativeY());
		}
	}

	@Override
	public void setX (float x) {
		if (root == null) throw new TNTRuntimeException("must @bindRoot() to set measures");
		root.setX(x);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setX(x + ani.getRelativeX());
		}
	}

	@Override
	public void setY (float y) {
		if (root == null) throw new TNTRuntimeException("must @bindRoot() to set measures");
		root.setY(y);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setX(y + ani.getRelativeY());
		}
	}

	@Override
	public void translate (float xAmount, float yAmount) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.translate(xAmount, yAmount);
		}
	}

	@Override
	public void translateX (float xAmount) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.translateX(xAmount);
		}
	}

	@Override
	public void translateY (float yAmount) {
		root.translateY(yAmount);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.translateY(yAmount);
		}
	}

	@Override
	public void setOrigin (float originX, float originY) {
		float dOX = root.getOriginX() - originX;
		float dOY = root.getOriginY() - originY;
		root.setOrigin(originX, originY);
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setOrigin(ani.getRelativeOX() + dOX, ani.getRelativeOY() + dOY);
		}
	}

	@Override
	public void setRotation (float degree) {
		for (SpriteRelativeAnimation ani : map.values())
			ani.setRotation(degree);
	}

	@Override
	public void rotate (float degree) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.rotate(degree);
		}
	}

	@Override
	public void setScale (float scaleXY) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setScale(scaleXY);
		}
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setScale(scaleX, scaleY);
		}
	}

	@Override
	public void scale (float amount) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.scale(amount);
		}
	}

	@Override
	public void setColor (float r, float g, float b, float a) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setColor(r, g, b, a);
		}
	}

	@Override
	public void setColor (Color color) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setColor(color);
		}
	}

	@Override
	public float[] getVertices () {
		return null;
	}

	@Override
	public float getX () {
		float x = Integer.MAX_VALUE;
		for (SpriteRelativeAnimation ani : map.values()) {
			if (ani.getX() < x) x = ani.getX();
		}
		return x;
	}

	@Override
	public float getCenterX () {
		return root.getX() + root.getOriginX();
	}

	@Override
	public float getY () {
		float y = Integer.MAX_VALUE;
		for (SpriteRelativeAnimation ani : map.values())
			if (ani.getY() < y) y = ani.getY();
		return y;
	}

	@Override
	public float getCenterY () {
		return root.getY() + root.getOriginY();
	}

	@Override
	public float getWidth () {
		float maxWidth = 0;
		for (SpriteRelativeAnimation ani : map.values())
			if (ani.getX() + ani.getWidth() > maxWidth) maxWidth = ani.getX() + ani.getWidth();
		return maxWidth - getX();
	}

	@Override
	public float getHeight () {
		float maxHeight = 0;
		for (SpriteRelativeAnimation ani : map.values())
			if (ani.getY() + ani.getHeight() > maxHeight) maxHeight = ani.getY() + ani.getHeight();
		return maxHeight - getY();
	}

	@Override
	public float getOriginX () {
		return root.getOriginX();
	}

	@Override
	public float getOriginY () {
		return getOriginY();
	}

	@Override
	public float getRotation () {
		return root.getRotation();
	}

	@Override
	public float getScaleX () {
		return root.getScaleX();
	}

	@Override
	public float getScaleY () {
		return root.getScaleY();
	}

	@Override
	public Color getColor () {
		return root.getColor();
	}

	@Override
	public void setColor (float a) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.setColor(a);
		}
	}

	// TODO: add hanle array of Bounding herer
	@Override
	public Array<FloatArray> getBounding () {
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
// return new float[] {x, y, x + width, y, x + width, y + height, x, y + height};
		return null;
	}

	@Override
	public Animator start () {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.start();
		}
		return this;
	}

	@Override
	public Animator start (float playback, boolean isLoop) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.start(playback, isLoop);
		}
		return this;
	}

	@Override
	public boolean isReversed () {
		return root.isReversed();
	}

	@Override
	public Animator setLoop (boolean loop) {
		root.setLoop(loop);
		return this;
	}

	@Override
	public Animator setPlaybackSpeed (float playback) {
		root.setPlaybackSpeed(playback);
		return this;
	}

	@Override
	public Animator start (float playback) {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.start(playback);
		}
		return this;
	}

	@Override
	public Animator stop () {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.stop();
		}
		return this;
	}

	@Override
	public Animator pause () {
		for (SpriteRelativeAnimation ani : map.values()) {
			ani.pause();
		}
		return this;
	}

	@Override
	public boolean isRunning () {
		return root.isRunning();
	}

	@Override
	public boolean isLooping () {
		return root.isLooping();
	}

	@Override
	public float getPlaybackSpeed () {
		return root.getPlaybackSpeed();
	}

	@Override
	public float getStateTime () {
		return root.getStateTime();
	}

	@Override
	public float getTotalDuration () {
		float maxDuration = 0f;
		for (SpriteRelativeAnimation ani : map.values()) {
			if (ani.isActive() && ani.getTotalDuration() > maxDuration) maxDuration = ani.getTotalDuration();
		}
		return maxDuration;
	}

	@Override
	protected void updateInternal (float delta) {
		for (SpriteRelativeAnimation ani : map.values()) {
			if (ani.isActive()) ani.update(delta);
		}
	}

	@Override
	protected void drawInternal (Batch batch) {
		for (TrackEntry track : tracks)
			track.draw(batch);
	}

}
