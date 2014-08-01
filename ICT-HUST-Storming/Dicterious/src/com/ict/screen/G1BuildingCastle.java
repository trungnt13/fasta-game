
package com.ict.screen;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.ict.DicteriousGame;
import com.ict.data.GameData.GameData1;
import com.ict.data.GameResult;
import com.ict.data.GameStatus;
import com.ict.data.I;
import com.ict.entities.EntityManager;
import com.ict.entities.G1Background;
import com.ict.entities.G1BrickManager;
import com.ict.entities.G1BrickManager.BrickStatusListener;
import com.ict.entities.G1Question;
import com.ict.utils.eMath;

public class G1BuildingCastle extends ScreenAdapter implements BrickStatusListener {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final int CenterTextPadding = 40;
	public static final float GamePlayTime = 90;
	public static final int INITIAL_SPEED = 400;
	private static boolean isExplosionEffectScaled = false;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	/** graphic stuff */
	private SpriteBatch mBatch;
	private final G1Background mBackground = new G1Background();
	private final G1BrickManager mBrickManager = new G1BrickManager();
	private final G1Question mQuestion = new G1Question();
	private final EntityManager mManager = new EntityManager() {
		@Override
		public void postEvent (Object... params) {

		}
	};

	/** Question states */
	private ArrayDeque<GameData1> mGameData = DicteriousGame.GameGenerator.genGame1();
	private QuestionState mQuestionState = QuestionState.None;
	private int mCurrentSpeed = INITIAL_SPEED;

	private String mCurrentReading;
	private GameData1.Question mCurrentQuestion;

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

	/** for effect */
	private ParticleEmitter mWinEffect;
	private ParticleEffect mExplosionEffect;

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		/** init graphics */
		mBatch = DicteriousGame.Batch;
		mBatch.setProjectionMatrix(DicteriousGame.ScreenViewport);

		mBackground.setName("background");
		mBrickManager.setName("brick");
		mQuestion.setName("question");

		mManager.add(mBackground);
		mManager.add(mBrickManager);
		mManager.add(mQuestion);
		mManager.show();

		/** center text */
		mCenterBackground = DicteriousGame.AssetManager.get(I.G1.Box, Texture.class);

		/** test */
		mBackground.postEvent("maxtime", GamePlayTime);

		/** set input */
		DicteriousGame.InputMultiplexer.addProcessor(mInput);

		/** effect */
		mWinEffect = DicteriousGame.AssetManager.get(I.G1.ParticleWin, ParticleEffect.class).findEmitter("star");
		// fire1 fire2 light1 light2 wave peices1 peices2 spark smallSpark smallWave bigFire smallLight
		ParticleEffect tmpEffect = DicteriousGame.AssetManager.get(I.ParticleExplosion, ParticleEffect.class);
		if (!isExplosionEffectScaled) {
			isExplosionEffectScaled = true;
			tmpEffect.scaleEffect(3f);
		}
		mExplosionEffect = new ParticleEffect();
		mExplosionEffect.getEmitters().add(tmpEffect.findEmitter("fire1"));
		mExplosionEffect.getEmitters().add(tmpEffect.findEmitter("light1"));
		mExplosionEffect.getEmitters().add(tmpEffect.findEmitter("wave"));
		mExplosionEffect.getEmitters().add(tmpEffect.findEmitter("bigFire"));

		/*-------- reset everything --------*/
		mCurrentSpeed = INITIAL_SPEED;
		mQuestionState = QuestionState.None;
		mResult = GameResult.None;
		isGamePaused = false;
		mStatus = GameStatus.Preparing;

	}

	@Override
	public void hide () {
		DicteriousGame.InputMultiplexer.removeProcessor(mInput);
	}

	@Override
	public void pause () {
		isGamePaused = true;
	}

	@Override
	public void resume () {
		isGamePaused = false;
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/** Game update */
		if (!isGamePaused) {
			mManager.update(delta);
			mWinEffect.update(delta);
			mExplosionEffect.update(delta);
			// check game win or lose
			checkGameWinLose();

			// preparing game
			if (mStatus == GameStatus.Preparing) {
				setCenterText("You need to build the castle before the sky goes dark", true, false);
				mStatus = GameStatus.None;
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						setCenterText("", false, false);
						mStatus = GameStatus.Playing;
						mQuestionState = QuestionState.PrintSpeed;
					}
				}, 1.3f);
			}
			// playing game
			else if (mStatus == GameStatus.Playing) {
				if (mQuestionState == QuestionState.PrintSpeed) {
					mQuestionState = QuestionState.None;
					Timer.schedule(new Timer.Task() {
						int i = 0;

						@Override
						public void run () {
							if (i == 0) {
								setCenterText(mCurrentSpeed + "WPM", false, true);
							} else if (i == 1) {
								setCenterText("Ready!", false, true);
							} else if (i == 2) {
								setCenterText("GO!", false, true);
							} else if (i == 3) {
								setCenterText("", false, true);
								mQuestionState = QuestionState.ShowQuestion;
							}
							i++;
						}
					}, 0, 0.5f, 4);
				} else if (mQuestionState == QuestionState.ShowQuestion && mGameData.size() > 0) {
					GameData1 current = mGameData.removeFirst();
					mCurrentReading = current.reading;
					mCurrentQuestion = current.question;

					mBrickManager.postEvent("add", mCurrentSpeed, mCurrentReading, this);
					mCurrentSpeed += 10;
					mQuestionState = QuestionState.None;
				} else if (mQuestionState == QuestionState.WaitForAnswer) {
					// nothing to do here now
				} else if (mQuestionState == QuestionState.ShowRightAnswerEffect) {
					System.out.println("Answer right");
					Vector2 tmp = mQuestion.getPositionOfAnswer(mCurrentQuestion.answer);
					showWinEffect(tmp.x, tmp.y);
					mQuestion.postEvent("hide");
					setCenterText("", false, false);
					mQuestionState = QuestionState.PrintSpeed;// dont worry next loop win check win lose in advance
				} else if (mQuestionState == QuestionState.ShowWrongAnswerEffect) {
					System.out.println("Answer wrong");
					mQuestion.postEvent("hide");
					setCenterText("", false, false);
					mBrickManager.postEvent("remove", mCurrentReading, this);
					mQuestionState = QuestionState.None;
				}
			}
			// game completed
			else if (mStatus == GameStatus.Completed) {
				if (mResult == GameResult.Lose) {
					mQuestion.postEvent("hide");
					setCenterText("Sorry! You LOSE!", true, false);
				} else if (mResult == GameResult.Win) {
					mQuestion.postEvent("hide");
					setCenterText("Congratulation! You WIN!", true, false);
				}
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						DicteriousGame.Game.setScreen(DicteriousGame.SMapSelect);
						System.out.println("Back to MapSelectScreen from G1BuildingCastle");
					}
				}, 5f);
				mStatus = GameStatus.None;
			}
		}

		/** Game render */
		mBatch.begin();
		mManager.render(mBatch);

		if (mCenterText.length() > 0) {
			if (isEnbaleCenterBackground)
				mBatch.draw(mCenterBackground, CenterTextPadding - 20, mCenterTextY - mCenterHeight - 20, DicteriousGame.ScreenWidth
					- CenterTextPadding * 2 + 40, mCenterHeight + 40);
			DicteriousGame.FontBig.drawWrapped(mBatch, mCenterText, mCenterTextX, mCenterTextY, DicteriousGame.ScreenWidth
				- CenterTextPadding * 2);
		}

		mWinEffect.draw(mBatch);
		mExplosionEffect.draw(mBatch);

		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// brick control methods
	// ///////////////////////////////////////////////////////////////
	@Override
	public void removingBricksDone () {
		System.out.println("REmove brick done");
		mQuestionState = QuestionState.PrintSpeed;
	}

	@Override
	public void addingBricksDone () {
		System.out.println("Add brick Done");
		setQuestion(mCurrentQuestion.question);
		mQuestionState = QuestionState.WaitForAnswer;
	}

	@Override
	public void startingRemoveBricks (float centerX, float centerY) {
		System.out.println("Start removing: " + centerX + " " + centerY);
		showBigExplosion(centerX, centerY);
	}

	// ///////////////////////////////////////////////////////////////
	// helper private
	// ///////////////////////////////////////////////////////////////
	private void setCenterText (String centerText, boolean isEnbaleCenterBackground, boolean isAlightCenter) {
		mCenterText = centerText;
		this.isEnbaleCenterBackground = isEnbaleCenterBackground;

		// if text available
		if (mCenterText.length() > 0) {
			TextBounds bound = DicteriousGame.FontBig.getWrappedBounds(centerText, DicteriousGame.ScreenWidth - CenterTextPadding
				* 2);
			mCenterHeight = bound.height;
			mCenterTextY = 2 * DicteriousGame.ScreenHeight / 3 + mCenterHeight;

			// check is align
			if (!isAlightCenter || isEnbaleCenterBackground)
				mCenterTextX = CenterTextPadding;
			else {
				mCenterTextX = DicteriousGame.ScreenWidth / 2 - bound.width / 2;
			}
		}
	}

	private void setQuestion (String text) {
		setCenterText(text, true, false);
		mQuestion.postEvent("show", mCenterTextY - mCenterHeight);
	}

	private void showWinEffect (float x, float y) {
		mWinEffect.setPosition(x, y);
		mWinEffect.start();
	}

	private void showBigExplosion (float x, float y) {
		mExplosionEffect.setPosition(x, y);
		mExplosionEffect.start();
	}

	private final void checkGameWinLose () {
		if (mBrickManager.isCrossTheWinLine()) {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Win;
		}

		else if (mBackground.isTimeUp() || mGameData.size() == 0) {
			mStatus = GameStatus.Completed;
			mResult = GameResult.Lose;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// Input
	// ///////////////////////////////////////////////////////////////
	private final InputAdapter mInput = new InputAdapter() {
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			Vector2 projected = eMath.convertToScreenCoordiate(screenX, screenY);
			System.out.println(projected);

			if (mQuestionState == QuestionState.WaitForAnswer) {
				String touched = mQuestion.whichOneTouched(projected.x, projected.y);
				System.out.println("Touched: " + touched + " " + mCurrentQuestion.answer);
				if (mCurrentQuestion.answer.contains(touched)) {
					System.out.println("Shit right");
					mQuestionState = QuestionState.ShowRightAnswerEffect;
				} else if (touched.length() > 0) {
					System.out.println("Shit wrong");
					mQuestionState = QuestionState.ShowWrongAnswerEffect;
				}
			}
			return false;
		};
	};

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////
	private static enum QuestionState {
		PrintSpeed, ShowQuestion, WaitForAnswer, ShowRightAnswerEffect, ShowWrongAnswerEffect, None
	}

}
