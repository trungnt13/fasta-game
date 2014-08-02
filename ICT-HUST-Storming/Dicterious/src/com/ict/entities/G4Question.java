
package com.ict.entities;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.data.GameData.GameData4;
import com.ict.data.I;
import com.ict.utils.eMath;

public class G4Question extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	private static final float START_TEXT_X = 30;
	private static final float START_TEXT_Y = 700;

	private static final float START_BUTTON_X = 60;
	private static final float START_BUTTON_Y = 300;

	private static final float LINE_PADDING = 13;
	private static final float SPACING = 8;

	// ///////////////////////////////////////////////////////////////
	// Main
	// ///////////////////////////////////////////////////////////////
	private ArrayDeque<GameData4> mGameData = DicteriousGame.GameGenerator.genGame4();
	private GameData4 mCurrentData;

	/** for drawing text */
	private Texture mHighlight;

	/** virtual brick for drawing highlighted text */
	private final ArrayList<VirtualBrick> mBricks = new ArrayList<G4Question.VirtualBrick>();
	private final ArrayList<VirtualBrick> mTmps = new ArrayList<G4Question.VirtualBrick>();

	/** true false button */
	private Sprite mTrue;
	private Sprite mFalse;

	private boolean isGameStart;

	private AnswerListener mListener;

	private int mTotalNumberOfQuestion;

	// ///////////////////////////////////////////////////////////////
	// helper
	// ///////////////////////////////////////////////////////////////

	public int getTotalNumberOfQuestion () {
		return mTotalNumberOfQuestion;
	}

	private ArrayList<VirtualBrick> safeClone () {
		mTmps.clear();
		mTmps.addAll(mBricks);
		return mTmps;
	}

	private void generateVirtualBrick (String text, ArrayList<Integer> position) {
		mBricks.clear();

		float maxWidth = DicteriousGame.ScreenWidth - START_TEXT_X;

		String[] splitted = text.split(" ");
		float currentX = START_TEXT_X;
		float currentY = START_TEXT_Y;

		for (int i = 0; i < splitted.length; i++) {
			VirtualBrick b = new VirtualBrick();
			b.text = splitted[i];
			TextBounds bound = DicteriousGame.FontNormal.getBounds(b.text);
			b.width = bound.width;
			b.height = bound.height;

			if (b.width + currentX > maxWidth) {
				currentX = START_TEXT_X;
				currentY -= LINE_PADDING + b.height;
			}
			b.x = currentX;
			b.y = currentY;
			currentX += b.width + SPACING;

			for (int j : position)
				if (j == i) b.highlighted = true;

			mBricks.add(b);
		}
	}

	private void generateVirtualBrick (String text, int... position) {
		mBricks.clear();

		float maxWidth = DicteriousGame.ScreenWidth - START_TEXT_X;

		String[] splitted = text.split(" ");
		float currentX = START_TEXT_X;
		float currentY = START_TEXT_Y;

		for (int i = 0; i < splitted.length; i++) {
			VirtualBrick b = new VirtualBrick();
			b.text = splitted[i];
			TextBounds bound = DicteriousGame.FontNormal.getBounds(b.text);
			b.width = bound.width;
			b.height = bound.height;

			if (b.width + currentX > maxWidth) {
				currentX = START_TEXT_X;
				currentY -= LINE_PADDING + b.height;
			}
			b.x = currentX;
			b.y = currentY;
			currentX += b.width + SPACING;

			for (int j : position)
				if (j == i) b.highlighted = true;

			mBricks.add(b);
		}
	}

	private void showData () {
		mCurrentData = mGameData.removeFirst();
		generateVirtualBrick(mCurrentData.sentence, mCurrentData.phrase);
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		mTotalNumberOfQuestion = mGameData.size();

		/** center text */
		mHighlight = DicteriousGame.AssetManager.get(I.G1.Box, Texture.class);

		/** true false */
		mTrue = new Sprite(DicteriousGame.AssetManager.get(I.G4.True, Texture.class));
		mTrue.setPosition(START_BUTTON_X, START_BUTTON_Y);

		mFalse = new Sprite(DicteriousGame.AssetManager.get(I.G4.False, Texture.class));
		mFalse.setPosition(DicteriousGame.ScreenWidth - START_BUTTON_X - mFalse.getWidth(), START_BUTTON_Y);
	}

	@Override
	public void update (float delta) {
		if (isGameStart) {
			if (Gdx.input.justTouched()) {
				Vector2 projected = eMath.convertToScreenCoordiate(Gdx.input.getX(), Gdx.input.getY());
				// right answer
				if ((mTrue.getBoundingRectangle().contains(projected) && mCurrentData.answer)
					|| (mFalse.getBoundingRectangle().contains(projected) && !mCurrentData.answer)) {
					if (mListener != null) {
						mListener.rightAnswer();
						if (mGameData.size() == 0) {
							mListener.outOfQuestion();
							isGameStart = false;
							mBricks.clear();
						} else
							showData();
					}
				}
				// wrong answer
				else if ((mTrue.getBoundingRectangle().contains(projected) && !mCurrentData.answer)
					|| (mFalse.getBoundingRectangle().contains(projected) && mCurrentData.answer)) {
					if (mListener != null) mListener.wrongAnswer();
					if (mGameData.size() == 0) {
						mListener.outOfQuestion();
						isGameStart = false;
						mBricks.clear();
					} else
						showData();
				}
			}
		}
	}

	@Override
	public void render (Batch batch) {
		/** draw text */
		safeClone();
		for (VirtualBrick b : mTmps) {
			if (b.highlighted)
				batch.draw(mHighlight, b.x - SPACING, b.y - b.height - SPACING, b.width + 2 * SPACING, b.height + 2 * SPACING);
			DicteriousGame.FontNormal.draw(batch, b.text, b.x, b.y);
		}

		/** draw button */
		if (mBricks.size() > 0) {
			mTrue.draw(batch);
			mFalse.draw(batch);
		}
	}

	@Override
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("start")) {
			if (mGameData.size() > 0) {
				isGameStart = true;
				showData();
				if (params.length > 1) mListener = (AnswerListener)params[1];
			}
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper datatype
	// ///////////////////////////////////////////////////////////////

	private class VirtualBrick {
		public String text;
		public boolean highlighted = false;
		public float x;
		public float y;
		public float width;
		public float height;
	}

	public static interface AnswerListener {
		public void rightAnswer ();

		public void wrongAnswer ();

		public void outOfQuestion ();
	}
}
