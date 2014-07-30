
package com.ict.entities;

import java.util.ArrayDeque;

import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.entities.G1Brick.BrickDropListener;

public class G1BrickManager extends EntitiesManager implements BrickDropListener {
	private static final float DISTANCE = 6 * DicteriousGame.ScreenHeight / 7;

	/** For adding brick */
	private CraftingStatusListener mStatusListener;
	private float mSpeed;
	private final ArrayDeque<String> mData = new ArrayDeque<String>();
	private float mCurrentX = 0;
	private float mCurrentY = DicteriousGame.ScreenHeight - DISTANCE;
	private boolean isWaitingBrickDropDone = false;

	/** For removing brick */
	private int numberOfRemoveBrick = 0;

	private Status mCurrentStatus = Status.Waiting;
	private final ArrayDeque<Status> mStatusList = new ArrayDeque<G1BrickManager.Status>();

	@Override
	public void show (float viewWidth, float viewHeight) {
		super.show(viewWidth, viewHeight);
	}

	@Override
	public void update (float delta) {
		super.update(delta);
		if (mCurrentStatus == Status.Adding) {
			if (mData.size() == 0) {
				mCurrentStatus = Status.Waiting;
				if (mStatusListener != null) mStatusListener.statusChanged(Status.Adding);
			} else if (!isWaitingBrickDropDone) {
				// check if word cross outside screen too much
				String keyword = mData.removeFirst();
				Vector2 brickSize = G1Brick.getBrickSize(keyword);
				if (mCurrentX + brickSize.x - DicteriousGame.ScreenWidth > brickSize.x / 2) {
					mCurrentY += brickSize.y;
					mCurrentX = 0;
				}

				// create brick
				G1Brick b = new G1Brick();
				b.show(1, 1);
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
		} else if (mCurrentStatus == Status.Removing) {

		} else if (mCurrentStatus == Status.Waiting && mStatusList.size() > 0) {
			mCurrentStatus = mStatusList.removeFirst();
			if (mStatusListener != null) mStatusListener.statusChanged(Status.Waiting);
		}
	}

	@Override
	public void dropDone (G1Brick brick) {
		isWaitingBrickDropDone = false;
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

			if (params.length > 3) mStatusListener = (CraftingStatusListener)params[3];

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

	public static interface CraftingStatusListener {
		public void statusChanged (Status oldStatus);
	}

	public static enum Status {
		Adding, Removing, Waiting
	}

}
