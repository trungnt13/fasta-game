
package com.ict.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.ict.data.GameData.Game1Data;

public class GameCreator {
	private final GameData mGameData;
	private final Random mRand = new Random();

	public GameCreator (GameData data) {
		mGameData = data;
	}

	public ArrayList<Game1Data> generateGame1 () {
		ArrayList<Game1Data> data = new ArrayList<GameData.Game1Data>(mGameData.DataGame1);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		return data;
	}
}
