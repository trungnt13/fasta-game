
package com.ict.entities;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.entities.G1Brick.BrickDropListener;
import com.ict.entities.G1Brick.BrickFallListener;
import com.ict.entities.G1Brick.BrickStatus;

public class G1BrickManager extends EntityManager implements BrickDropListener, BrickFallListener {
	// ///////////////////////////////////////////////////////////////
	// static params
	// ///////////////////////////////////////////////////////////////

	private static final float DISTANCE = DicteriousGame.ScreenHeight;
	private static final float AUTO_TRANSLATE_SPEED = 50;
	private static final float WALL_HEIGHT_LIMIT = 400;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	/** For adding brick */
	private BrickStatusListener mStatusListener;
	private float mSpeed;
	private final ArrayDeque<String> mData = new ArrayDeque<String>();
	private boolean isWaitingBrickDropDone = false;

	/** For removing brick */
	private int numberOfRemoveBrick = 0;

	private Status mCurrentStatus = Status.Waiting;
	private final ArrayDeque<Status> mStatusList = new ArrayDeque<G1BrickManager.Status>();

	/** auto adjust bricks param */
	private float mTranslateDistance = 0;

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	private final G1Brick getLastBrick () {
		if (mEntities.size() > 0) return (G1Brick)mEntities.get(mEntities.size() - 1);
		return null;
	}

	private final G1Brick getLastNoneDropBrick () {
		for (int i = mEntities.size() - 1; i >= 0; i--) {
			G1Brick tmp = (G1Brick)mEntities.get(i);
			if (tmp.getStatus() != BrickStatus.Drop) return tmp;
		}
		return null;
	}

	@Override
	public void show () {
		super.show();
	}

	@Override
	public void update (float delta) {
		super.update(delta);
		/*-------- now update --------*/
		if (mCurrentStatus == Status.Adding) {
			if (mData.size() == 0) {
				mCurrentStatus = Status.Waiting;
				if (mStatusListener != null) mStatusListener.statusChanged(Status.Adding);
			} else if (!isWaitingBrickDropDone) {
				G1Brick lastBrick = getLastBrick();
				float mCurrentY = 0;
				float mCurrentX = 0;

				if (lastBrick != null) {
					mCurrentY = lastBrick.mCurrentSprite.getY();
					mCurrentX = lastBrick.mCurrentSprite.getX() + lastBrick.mCurrentSprite.getWidth();
				}
				// check if word cross outside screen too much
				String keyword = mData.removeFirst();
				Vector2 brickSize = G1Brick.getBrickSize(keyword);
				if (mCurrentX + brickSize.x - DicteriousGame.ScreenWidth > brickSize.x / 2) {
					mCurrentY += brickSize.y;
					mCurrentX = 0;
				}

				// create brick
				G1Brick b = new G1Brick();
				b.show();
				b.postEvent("drop", keyword, mCurrentX, mCurrentY, mSpeed, this);
				add(b);

				// update info
				mCurrentX += brickSize.x;
				if (mCurrentX > DicteriousGame.ScreenWidth) {
					mCurrentX = 0;
					mCurrentY += brickSize.y;
				}

				// turn on waiting for brick drop
				isWaitingBrickDropDone = true;
			}
		} else if (mCurrentStatus == Status.Removing && mEntities.size() > 0) {
			for (int i = 0; i < Math.min(numberOfRemoveBrick, mEntities.size()); i++) {
				mEntities.get(mEntities.size() - 1 - i).postEvent("fall", this);
			}
			mCurrentStatus = Status.Waiting;
		} else if (mCurrentStatus == Status.Waiting && mStatusList.size() > 0) {
			mCurrentStatus = mStatusList.removeFirst();
			if (mStatusListener != null) mStatusListener.statusChanged(Status.Waiting);
		}

		/*-------- check reach limit of wall --------*/
		G1Brick lastNoneDropBrick = getLastNoneDropBrick();
		if (mEntities.size() > 0 && lastNoneDropBrick != null) {
			if (lastNoneDropBrick.mCurrentSprite.getY() > WALL_HEIGHT_LIMIT) {
				mTranslateDistance += delta * getLastBrick().mCurrentSprite.getHeight();
			}
		}

		/*-------- auto adjust translate --------*/
		if (mTranslateDistance > 0) {
			ArrayList<Entity> tmp = safeClone();
			float translateAmount = AUTO_TRANSLATE_SPEED * delta;
			mTranslateDistance -= translateAmount;
			for (Entity entity : tmp) {
				((G1Brick)entity).translateY(-translateAmount);
			}
		} else
			mTranslateDistance = 0;
	}

	@Override
	public void dropDone (G1Brick brick) {
		isWaitingBrickDropDone = false;
	}

	@Override
	public void fallDone (G1Brick brick) {
		remove(brick);
		if (mEntities.size() > 0) {
			G1Brick lastEntities = (G1Brick)mEntities.get(mEntities.size() - 1);
		} else {
		}
		numberOfRemoveBrick--;
		if (numberOfRemoveBrick == 0) {
			if (mStatusListener != null) mStatusListener.statusChanged(Status.Removing);
		}
	}

	/** @param params <br>
	 *           question1_speed_text : post question in words per minite for given string. <br>
	 *           break_[numberOfWords] : breaks given number of words */
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("add")) {
			int wordDropSpeed = (Integer)params[1];
			mSpeed = DISTANCE / (60f / wordDropSpeed);

			String data = (String)params[2];
			mData.clear();
			for (String s : data.split(" "))
				mData.add(s);

			if (params.length > 3) mStatusListener = (BrickStatusListener)params[3];

			mStatusList.addLast(Status.Adding);
		} else if (eventType.contains("remove")) {
			if (params[1] instanceof String)
				numberOfRemoveBrick = ((String)params[1]).split("_").length;
			else if (params[1] instanceof Integer) numberOfRemoveBrick = (Integer)params[1];

			mStatusList.addLast(Status.Removing);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	public static interface BrickStatusListener {
		public void statusChanged (Status oldStatus);
	}

	public static enum Status {
		Adding, Removing, Waiting
	}

}
