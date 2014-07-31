
package com.ict.screen;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.ict.DicteriousGame;
import com.ict.data.GameData.Game1Data;
import com.ict.entities.G1Background;
import com.ict.entities.G1BrickManager;
import com.ict.entities.G1BrickManager.CraftingStatusListener;
import com.ict.entities.G1BrickManager.Status;

public class S1BuildingCastle extends ScreenAdapter implements CraftingStatusListener {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final float MAX_WALL_HEIGHT = 1000;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	private SpriteBatch batch;
	private final G1Background mBackground = new G1Background();
	private boolean isInit = false;

	/*-------- game result --------*/
	private GameResult mGameResult = GameResult.None;

	/*-------- for adding question --------*/

	private final ArrayDeque<Game1Data> mGameData = DicteriousGame.GameData.generateGame1();
	private GameStatus mGameStatus = GameStatus.Preparing;
	private final G1BrickManager mBrickManager = new G1BrickManager();

	private int mCurrentSpeed = 130;
	private QuestionState mQuestionState = QuestionState.WaitForAnswer;

	private boolean isGamePause = false;

	private String mQuestionOnScreen = "";

	private String mTextOnScreenCenter = "";
	private float xOfCenterText = 0;
	private float yOfCenterText = 0;

	/*-------- for ui --------*/
	private Stage mStage;
	private Group mQuestionGroup;
	private ParticleEmitter mEmitter;
	private final ClickListener mAnswerClickListener = new ClickListener() {
		@Override
		public void clicked (InputEvent event, float x, float y) {
			String button = event.getListenerActor().getName();
			mQuestionGroup.setVisible(false);
			if (mGameData.size() > 0) {
				Game1Data data = mGameData.removeFirst();
				// clear showed data
				S1BuildingCastle.this.setQuestionOnScreen("");
				// change state
				if (data.question.answer.equals(button))
					mQuestionState = QuestionState.ShowAnswerRightEffect;
				else
					mQuestionState = QuestionState.ShowAnswerWrongEffect;
			}
		}
	};

	@Override
	public void show () {
		if (!isInit) {
			isInit = true;

			// init drawing
			batch = DicteriousGame.Batch;
			batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight));

			// init ui
			mStage = new Stage(new ScalingViewport(Scaling.stretch, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight));
			TextButtonStyle style = new TextButtonStyle();
			style.up = new TextureRegionDrawable(new TextureRegion(DicteriousGame.AssetManager.get("game1/brick_light.png",
				Texture.class)));
			style.down = new TextureRegionDrawable(new TextureRegion(DicteriousGame.AssetManager.get("game1/brick_dark.png",
				Texture.class)));
			style.font = DicteriousGame.FontNormal;

			mQuestionGroup = new Group();

			TextButton btrue = new TextButton("True", style);
			btrue.setName("T");
			btrue.addListener(mAnswerClickListener);
			btrue.setX(30);

			TextButton bfalse = new TextButton("False", style);
			bfalse.setName("F");
			bfalse.addListener(mAnswerClickListener);
			bfalse.setX(DicteriousGame.ScreenWidth - 30 - bfalse.getWidth());

			TextButton bng = new TextButton("NG", style);
			bng.setName("NG");
			bng.addListener(mAnswerClickListener);
			bng.setX(DicteriousGame.ScreenWidth / 2 - bng.getWidth() / 2);

			mQuestionGroup.addActor(bng);
			mQuestionGroup.addActor(bfalse);
			mQuestionGroup.addActor(btrue);
			mQuestionGroup.setVisible(false);

			mStage.addActor(mQuestionGroup);

			Gdx.input.setInputProcessor(mStage);

			// init emitter
			mEmitter = DicteriousGame.AssetManager.get("game1/win.p", ParticleEffect.class).findEmitter("star");
			mEmitter.setPosition(DicteriousGame.ScreenWidth / 2, DicteriousGame.ScreenHeight / 2);
			mEmitter.start();
		} else {

		}
		// init background
		mBackground.show(DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mBackground.postEvent("maxtime", 90f);

		// init brick
		mBrickManager.show(DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update stuff
		if (!isGamePause) {
			mBackground.update(delta);
			mBrickManager.update(delta);
			mEmitter.update(delta);

			/** Game is preparing */
			if (mGameStatus == GameStatus.Preparing) {
				mGameStatus = GameStatus.None;
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						mGameStatus = GameStatus.Playing;
						mQuestionState = QuestionState.AddSpeedAnnouncement;
					}
				}, 1f);
			}
			/** Game is playing */
			else if (mGameStatus == GameStatus.Playing) {
				// show info about question
				if (mQuestionState == QuestionState.AddSpeedAnnouncement) {
					mQuestionState = QuestionState.None;
					Timer.schedule(new Timer.Task() {
						int i = 0;

						@Override
						public void run () {
							if (i == 0)
								setTextOnScreenCenter(mCurrentSpeed + "WPM");
							else if (i == 1)
								setTextOnScreenCenter("Ready!");
							else if (i == 2) {
								setTextOnScreenCenter("Go!");
							} else {
								setTextOnScreenCenter("");
								mQuestionState = QuestionState.AddQuestion;
							}
							i++;
						}
					}, 0, 0.5f, 4);
				}
				// add bricks
				else if (mQuestionState == QuestionState.AddQuestion) {
					mQuestionState = QuestionState.None;
					mBrickManager.postEvent("add", mCurrentSpeed, mGameData.getFirst().reading, this);
					mCurrentSpeed += 10;
				}
				// add question done, wait for user's answer
				else if (mQuestionState == QuestionState.WaitForAnswer) {
					setQuestionOnScreen(mGameData.getFirst().question.question);
					mQuestionState = QuestionState.None;
				}
				// show effect
				else if (mQuestionState == QuestionState.ShowAnswerRightEffect) {
					System.out.println("Right Answer");
					// TODO: Play answer right effect
					mQuestionState = QuestionState.AddSpeedAnnouncement;
					checkWinTheGame();
					checkLoseTheGame();
				} else if (mQuestionState == QuestionState.ShowAnswerWrongEffect) {
					System.out.println("Wrong Answer");
					// TODO: Play answer wrong effect
					mBrickManager.postEvent("remove", 10);
					checkWinTheGame();
					checkLoseTheGame();
				}
			}
			/** Game is completing */
			else if (mGameStatus == GameStatus.Completed) {
				if (mGameResult == GameResult.Lose) {
					System.out.println("Lose");
					// TODO: Game Lose
				} else if (mGameResult == GameResult.Win) {
					System.out.println("Win");
					// TODO: Game Win
				}
			}
		}
		// draw stuff
		batch.begin();
		mBackground.render(batch);
		mBrickManager.render(batch);
		if (mQuestionOnScreen.length() > 0)
			DicteriousGame.FontNormal.drawWrapped(batch, mQuestionOnScreen, 30, 950, DicteriousGame.ScreenWidth - 30);
		if (mTextOnScreenCenter.length() > 0)
			DicteriousGame.FontBig.draw(batch, mTextOnScreenCenter, xOfCenterText, yOfCenterText);
		mEmitter.draw(batch);
		batch.end();

		// draw ui
		mStage.draw();
	}

	@Override
	public void statusChanged (Status oldStatus) {
		if (oldStatus == Status.Adding) {
			mQuestionState = QuestionState.WaitForAnswer;
		} else if (oldStatus == Status.Removing) {
			mQuestionState = QuestionState.AddSpeedAnnouncement;
		}
	}

	@Override
	public void pause () {
		isGamePause = true;
	}

	@Override
	public void resume () {
		isGamePause = false;
	}

	// ///////////////////////////////////////////////////////////////
	// helper private methodsk
	// ///////////////////////////////////////////////////////////////
	private void checkWinTheGame () {
		if (mBrickManager.getWallHeight() > MAX_WALL_HEIGHT) {
			mGameStatus = GameStatus.Completed;
			mGameResult = GameResult.Win;
		}
	}

	private void checkLoseTheGame () {
		if (mBackground.isTimeUp() || (mBrickManager.getWallHeight() < MAX_WALL_HEIGHT && mGameData.size() == 0)) {
			mGameStatus = GameStatus.Completed;
			mGameResult = GameResult.Lose;
		}
	}

	private void setTextOnScreenCenter (String text) {
		mTextOnScreenCenter = text;
		if (text.length() > 0) {
			TextBounds bound = DicteriousGame.FontBig.getBounds(text);
			xOfCenterText = DicteriousGame.ScreenWidth / 2 - bound.width / 2;
			yOfCenterText = DicteriousGame.ScreenHeight / 2 + bound.height;
		}
	}

	private void setQuestionOnScreen (String question) {
		mQuestionOnScreen = question;

		if (question.length() > 0) {
			float height = DicteriousGame.FontNormal.getWrappedBounds(question, DicteriousGame.ScreenWidth - 30).height;

			mQuestionGroup.setY(950 - height - mQuestionGroup.getHeight() - 150);
			mQuestionGroup.setVisible(true);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	private static enum QuestionState {
		AddSpeedAnnouncement, AddQuestion, WaitForAnswer, ShowAnswerRightEffect, ShowAnswerWrongEffect, None
	}

}
