
package com.ict.screen;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Timer;
import com.ict.DicteriousGame;
import com.ict.data.GameData.Game1Data;
import com.ict.entities.EntitiesManager;
import com.ict.entities.G1Background;
import com.ict.entities.G1BrickManager;
import com.ict.entities.G1BrickManager.CraftingStatusListener;
import com.ict.entities.G1BrickManager.Status;

public class S1BuildingCastle extends ScreenAdapter implements CraftingStatusListener {
	private SpriteBatch batch;
	private final EntitiesManager mEntitiesManager = new EntitiesManager();
	private boolean isInit = false;

	private final ArrayDeque<Game1Data> mGameData = DicteriousGame.GameData.generateGame1();
	private GameStatus mGameStatus = GameStatus.Preparing;
	private final G1BrickManager mBrickManager = new G1BrickManager();

	private int mCurrentSpeed = 130;
	private boolean isAddingBricks = false;
	private QuestionState mQuestionState = QuestionState.WaitForAnswer;

	private boolean isGamePause = false;

	@Override
	public void show () {
		if (!isInit) {
			isInit = true;

			batch = DicteriousGame.Batch;
			batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight));

			G1Background background = new G1Background();
			background.setName("background");
			mEntitiesManager.add(background);
		} else {

		}
		mEntitiesManager.show(DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mEntitiesManager.findEntityByName("background").postEvent("maxtime", 90f);

		mBrickManager.show(DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update stuff
		if (!isGamePause) {
			mEntitiesManager.update(delta);
			mBrickManager.update(delta);

			if (mGameStatus == GameStatus.Preparing) {
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						mGameStatus = GameStatus.Playing;
						mQuestionState = QuestionState.AddSpeedAnnouncement;
					}
				}, 1f);
			} else if (mGameStatus == GameStatus.Playing) {
				if (!isAddingBricks) {
					if (mQuestionState == QuestionState.AddSpeedAnnouncement) {
						mBrickManager.postEvent("add", 100, mCurrentSpeed + "WPM Ready!", this);
					} else if (mQuestionState == QuestionState.AddQuestion) {
						mBrickManager.postEvent("add", mCurrentSpeed, mGameData.getFirst().question, this);
						mCurrentSpeed += 10;
					}
					isAddingBricks = true;
				}
				// add question done, wait for user's answer
				if (mQuestionState == QuestionState.WaitForAnswer) {

				}
			} else if (mGameStatus == GameStatus.Completed) {

			}
		}
		// draw stuff
		batch.begin();
		mEntitiesManager.render(batch);
		mBrickManager.render(batch);
		batch.end();
	}

	@Override
	public void statusChanged (Status oldStatus) {
		isAddingBricks = false;
		if (mQuestionState == QuestionState.AddSpeedAnnouncement)
			mQuestionState = QuestionState.AddQuestion;
		else if (mQuestionState == QuestionState.AddQuestion) mQuestionState = QuestionState.WaitForAnswer;
	}

	@Override
	public void pause () {
		isGamePause = true;
	}

	@Override
	public void resume () {
		isGamePause = false;
	}

	private static enum QuestionState {
		AddSpeedAnnouncement, AddQuestion, WaitForAnswer
	}
}
