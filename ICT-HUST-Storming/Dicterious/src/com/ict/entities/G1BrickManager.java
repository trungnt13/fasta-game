
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
	private static final float AUTO_TRANSLATE_SPEED = 130;
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
	private float mCenterOfRemoveX = 0;
	private float mCenterOfRemoveY = 0;

	/** status */
	private Status mCurrentStatus = Status.Waiting;
	private final ArrayDeque<Status> mStatusList = new ArrayDeque<G1BrickManager.Status>();

	/** auto adjust bricks param */
	private float mTranslateDistance = 0;

	// ///////////////////////////////////////////////////////////////
	// helper
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

	public final boolean isCrossTheWinLine(){
		return false;
	}
	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		super.show();
	}

	@Override
	public void update (float delta) {
		super.update(delta);
		/*-------- now update --------*/
		/** adding bricks */
		if (mCurrentStatus == Status.Adding) {
			if (mData.size() == 0) {
				mCurrentStatus = Status.Waiting;
				if (mStatusListener != null) mStatusListener.addingBricksDone();
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
		}
		/** remove bricks */
		else if (mCurrentStatus == Status.Removing && mEntities.size() > 0) {
			for (int i = 0; i < Math.min(numberOfRemoveBrick, mEntities.size()); i++) {
				G1Brick brick = (G1Brick)mEntities.get(mEntities.size() - 1 - i);
				mCenterOfRemoveX += brick.mCurrentSprite.getX() + brick.mCurrentSprite.getWidth() / 2;
				mCenterOfRemoveY += brick.mCurrentSprite.getY() + brick.mCurrentSprite.getHeight() / 2;
				brick.postEvent("fall", this);
			}
			if (mStatusListener != null)
				mStatusListener.startingRemoveBricks(mCenterOfRemoveX / numberOfRemoveBrick, mCenterOfRemoveY / numberOfRemoveBrick);
			mCurrentStatus = Status.Waiting;
		}
		/** waiting */
		else if (mCurrentStatus == Status.Waiting && mStatusList.size() > 0) {
			mCurrentStatus = mStatusList.removeFirst();
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
		numberOfRemoveBrick--;
		if (numberOfRemoveBrick == 0) {
			if (mStatusListener != null) mStatusListener.removingBricksDone();
			;
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
			// get number of remove
			if (params[1] instanceof String)
				numberOfRemoveBrick = ((String)params[1]).split(" ").length;
			else if (params[1] instanceof Integer) numberOfRemoveBrick = (Integer)params[1];
			System.out.println("Removing: " + numberOfRemoveBrick);

			// reset information
			mCenterOfRemoveX = 0;
			mCenterOfRemoveY = 0;

			// set status
			mStatusList.addLast(Status.Removing);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	public static interface BrickStatusListener {
		public void removingBricksDone ();

		public void addingBricksDone ();

		public void startingRemoveBricks (float centerX, float centerY);
	}

	public static enum Status {
		Adding, Removing, Waiting
	}

}
