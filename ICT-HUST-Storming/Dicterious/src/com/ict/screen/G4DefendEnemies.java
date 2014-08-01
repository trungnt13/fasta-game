
package com.ict.screen;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.data.GameData.GameData4;
import com.ict.data.GameResult;
import com.ict.data.GameStatus;
import com.ict.entities.CollisionChecker;
import com.ict.entities.EntityManager;
import com.ict.entities.G4Background;
import com.ict.entities.G4CrossBow;
import com.ict.entities.G4Enemies;
import com.ict.entities.G4Enemies.Enemy;
import com.ict.utils.eMath;

public class G4DefendEnemies extends ScreenAdapter {
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

	/** game status */
	private GameStatus mStatus = GameStatus.Preparing;
	private GameResult mResult = GameResult.None;
	private boolean isGamePaused = false;

	/** current quest */
	private GameData4 mCurrentQuestion;
	private ArrayDeque<GameData4> mGameData;

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
		mManager.show();

		/** init question */
		mGameData = DicteriousGame.GameGenerator.genGame4();
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
		}

		/*-------- draw --------*/
		mBatch.begin();
		mManager.render(mBatch);

		mBatch.end();

		if (Gdx.input.justTouched()) {
			Enemy e = mEnemies.safeClone().get(0);
			mCrossBow.shotSingle(e.getCenterX(), e.getCenterY());
		}
	}
}
