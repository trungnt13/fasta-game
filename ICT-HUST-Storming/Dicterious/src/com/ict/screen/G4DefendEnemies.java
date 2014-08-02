
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.utils.Timer;
import com.ict.DicteriousGame;
import com.ict.data.GameResult;
import com.ict.data.GameStatus;
import com.ict.data.I;
import com.ict.entities.CollisionChecker;
import com.ict.entities.EntityManager;
import com.ict.entities.G4Background;
import com.ict.entities.G4CrossBow;
import com.ict.entities.G4Enemies;
import com.ict.entities.G4Question.AnswerListener;
import com.ict.entities.G4Question;

public class G4DefendEnemies extends ScreenAdapter implements AnswerListener {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////
	/** graphic stuff */
	private SpriteBatch mBatch;
	private final EntityManager mManager = new EntityManager() {
		@Override
		public void postEvent (Object... params) {

		}
	};
	private final G4Background mBackground = new G4Background();
	private final G4CrossBow mCrossBow = new G4CrossBow();
	private final G4Enemies mEnemies = new G4Enemies();
	private final G4Question mQuestion = new G4Question();

	/** game status */
	private GameStatus mStatus = GameStatus.Preparing;
	private GameResult mResult = GameResult.None;
	private boolean isGamePaused = false;

	/** for drawing text */
	private String mCenterText = "";

	private boolean isEnbaleCenterBackground = true;
	private float mCenterTextX;
	private float mCenterTextY;
	private float mCenterHeight;

	private Texture mCenterBackground;

	// ///////////////////////////////////////////////////////////////
	// override screen
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		mBatch = DicteriousGame.Batch;

		/** add entities */
		mManager.add(mBackground);
		mManager.add(mEnemies);
		mManager.add(mCrossBow);
		mManager.add(mQuestion);
		mManager.show();

		/** start game */
		mQuestion.postEvent("start", this);

		/** center text */
		mCenterBackground = DicteriousGame.AssetManager.get(I.G1.Box, Texture.class);
	}

	@Override
	public void hide () {
	}

	@Override
	public void resume () {
		isGamePaused = false;
	}

	@Override
	public void pause () {
		isGamePaused = true;
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*-------- update --------*/
		if (!isGamePaused) {
			mManager.update(delta);
			CollisionChecker.checkCollision(mEnemies.safeClone(), mCrossBow.safeClone());

			if (mStatus == GameStatus.Preparing) {

			} else if (mStatus == GameStatus.Playing) {

			} else if (mStatus == GameStatus.Completed) {
				if (mResult == GameResult.Win) {
					setCenterText("Congratulation! You Win!", true, false);
					mStatus = GameStatus.None;
				} else if (mResult == GameResult.Lose) {
					setCenterText("Congratulation! You Lose!", true, false);
					mStatus = GameStatus.None;
				}
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						DicteriousGame.Game.setScreen(DicteriousGame.SMapSelect);
					}
				}, 5f);
			}
		}

		/*-------- draw --------*/
		mBatch.begin();
		mManager.render(mBatch);

		if (mCenterText.length() > 0) {
			if (isEnbaleCenterBackground)
				mBatch.draw(mCenterBackground, G1BuildingCastle.CenterTextPadding - 20, mCenterTextY - mCenterHeight - 20,
					DicteriousGame.ScreenWidth - G1BuildingCastle.CenterTextPadding * 2 + 40, mCenterHeight + 40);
			DicteriousGame.FontBig.drawWrapped(mBatch, mCenterText, mCenterTextX, mCenterTextY, DicteriousGame.ScreenWidth
				- G1BuildingCastle.CenterTextPadding * 2);
		}

		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// helper methods
	// ///////////////////////////////////////////////////////////////
	private void setCenterText (String centerText, boolean isEnbaleCenterBackground, boolean isAlightCenter) {
		mCenterText = centerText;
		this.isEnbaleCenterBackground = isEnbaleCenterBackground;

		// if text available
		if (mCenterText.length() > 0) {
			TextBounds bound = DicteriousGame.FontBig.getWrappedBounds(centerText, DicteriousGame.ScreenWidth
				- G1BuildingCastle.CenterTextPadding * 2);
			mCenterHeight = bound.height;
			mCenterTextY = 2 * DicteriousGame.ScreenHeight / 3 + mCenterHeight;

			// check is align
			if (!isAlightCenter || isEnbaleCenterBackground)
				mCenterTextX = G1BuildingCastle.CenterTextPadding;
			else {
				mCenterTextX = DicteriousGame.ScreenWidth / 2 - bound.width / 2;
			}
		}
	}

	// ///////////////////////////////////////////////////////////////
	// answer listener
	// ///////////////////////////////////////////////////////////////

	@Override
	public void rightAnswer () {
		System.out.println("Right Answer");
		mCrossBow.attack(mEnemies.safeClone());
	}

	@Override
	public void wrongAnswer () {
		System.out.println("Wrong Answer");
		mCrossBow.refreshSkill();
	}

	@Override
	public void outOfQuestion () {
		System.out.println("Out Of Question");
		if (mEnemies.getNumberOfAttack() > 0) {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Lose;
		} else {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Win;
		}
	}
}
