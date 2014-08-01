
package com.ict.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.ict.data.GameData.GameData1;
import com.ict.data.GameData.GameData3;
import com.ict.data.GameData.GameData4;

public class GameCreator {
	private final GameData mGameData;
	private final Random mRand = new Random();

	public GameCreator (GameData data) {
		this.mGameData = data;
	}

	public ArrayDeque<GameData1> genGame1 () {
		final ArrayList<GameData1> tmp = new ArrayList<GameData1>(mGameData.DataGame1);
		for (int i = 0; i < mGameData.DataGame1.size(); i++) {
			Collections.swap(tmp, i, mRand.nextInt(mGameData.DataGame1.size()));
		}
		final ArrayDeque<GameData1> data = new ArrayDeque<GameData.GameData1>(tmp);
		return data;
	}

	public ArrayDeque<GameData3> genGame3 () {
		final ArrayList<GameData3> tmp = new ArrayList<GameData3>(mGameData.DataGame3);
		for (int i = 0; i < mGameData.DataGame3.size(); i++) {
			Collections.swap(tmp, i, mRand.nextInt(mGameData.DataGame3.size()));
		}
		final ArrayDeque<GameData3> data = new ArrayDeque<GameData.GameData3>(tmp);
		return data;
	}

	public ArrayDeque<GameData4> genGame4 () {
		final ArrayList<GameData4> tmp = new ArrayList<GameData4>(mGameData.DataGame4);
		for (int i = 0; i < mGameData.DataGame4.size(); i++) {
			Collections.swap(tmp, i, mRand.nextInt(mGameData.DataGame4.size()));
		}
		final ArrayDeque<GameData4> data = new ArrayDeque<GameData.GameData4>(tmp);
		return data;
	}
}
