
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.ict.entities.G4Question;
import com.ict.entities.G4Question.AnswerListener;

public class G4DefendEnemies extends ScreenAdapter implements AnswerListener {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final int MAX_HP = 1;
	public static final float DURATION_OF_NOTIFICATION = 0.5f;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////
	private boolean isGameEnd = false;

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

	private float mCenterTextX;
	private float mCenterTextY;
	private float mCenterHeight;

	/** draw win lose effect */
	private Texture mBlack;

	/*-------- draw number of right answer --------*/
	private int mNumberOfRithAnswer = 0;
	private Texture mWhiteButton;

	float rax;
	float ray;

	/** duration */
	private String mCurrentNotification;
	private float mNotificationTimeCOunter = 0;
	private final TextBounds mNotificationBound = new TextBounds();

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

		/** black */
		mBlack = DicteriousGame.AssetManager.get(I.Black, Texture.class);

		/*-------- white button --------*/
		mWhiteButton = DicteriousGame.AssetManager.get(I.G4.WhiteButton, Texture.class);
		rax = DicteriousGame.ScreenWidth - G4CrossBow.SKILL_X - mWhiteButton.getWidth();
		ray = G4CrossBow.SKILL_Y;
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

			/** check losed */
			if (mEnemies.getNumberOfAttack() > MAX_HP) {
				mStatus = GameStatus.Completed;
				mResult = GameResult.Lose;
			}

			/** game state */
			if (mStatus == GameStatus.Preparing) {

			} else if (mStatus == GameStatus.Playing) {

			} else if (mStatus == GameStatus.Completed && !isGameEnd) {
				isGameEnd = true;
				mStatus = GameStatus.None;

				if (mResult == GameResult.Win) {
					setCenterText("Congratulation! You Win!", true);
				} else if (mResult == GameResult.Lose) {
					setCenterText("Sorry! You Lose!", true);
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

		/** draw right answer */
		mBatch.draw(mWhiteButton, rax, ray);
		DicteriousGame.FontNormal.setColor(Color.BLACK);
		DicteriousGame.FontNormal.draw(mBatch, mNumberOfRithAnswer + "/" + mQuestion.getTotalNumberOfQuestion(), rax + 5, ray
			+ mWhiteButton.getHeight() / 2 + 15);

		/** draw notification */
		if (mNotificationTimeCOunter > 0) {
			DicteriousGame.FontNormal.draw(mBatch, mCurrentNotification, DicteriousGame.ScreenWidth / 2 - mNotificationBound.width
				/ 2, ray + mNotificationBound.height + 13);
			mNotificationTimeCOunter -= delta;
		}
		DicteriousGame.FontNormal.setColor(Color.WHITE);

		/** draw black */
		if (isGameEnd) {
			mBatch.draw(mBlack, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		}

		if (mCenterText.length() > 0) {
			DicteriousGame.FontBig.drawWrapped(mBatch, mCenterText, mCenterTextX, mCenterTextY, DicteriousGame.ScreenWidth
				- G1BuildingCastle.CenterTextPadding * 2);
		}

		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// helper methods
	// ///////////////////////////////////////////////////////////////
	private void setCenterText (String centerText, boolean isAlightCenter) {
		mCenterText = centerText;

		// if text available
		if (mCenterText.length() > 0) {
			TextBounds bound = DicteriousGame.FontBig.getWrappedBounds(centerText, DicteriousGame.ScreenWidth
				- G1BuildingCastle.CenterTextPadding * 2);
			mCenterHeight = bound.height;
			mCenterTextY = 2 * DicteriousGame.ScreenHeight / 3 + mCenterHeight;

			// check is align
			if (!isAlightCenter)
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
		mNumberOfRithAnswer++;

		mNotificationTimeCOunter = DURATION_OF_NOTIFICATION;
		mCurrentNotification = "Correct!";
		DicteriousGame.FontNormal.getBounds(mCurrentNotification, mNotificationBound);
	}

	@Override
	public void wrongAnswer () {
		System.out.println("Wrong Answer");
		mCrossBow.refreshSkill();

		mNotificationTimeCOunter = DURATION_OF_NOTIFICATION;
		mCurrentNotification = "Wrong!";
		DicteriousGame.FontNormal.getBounds(mCurrentNotification, mNotificationBound);
	}

	@Override
	public void outOfQuestion () {
		System.out.println("Out Of Question");
		if (mEnemies.getNumberOfAttack() > 0 || (mNumberOfRithAnswer / mQuestion.getTotalNumberOfQuestion() < 0.5f)) {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Lose;
		} else {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Win;
		}
	}
}
