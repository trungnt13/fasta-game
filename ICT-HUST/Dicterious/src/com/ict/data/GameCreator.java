
package com.ict.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.ict.data.GameData.Game1Data;
import com.ict.data.GameData.Game3Data;
import com.ict.data.GameData.Game4Data;

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

	public ArrayList<Game3Data> generateGame3 () {
		ArrayList<Game3Data> data = new ArrayList<GameData.Game3Data>(mGameData.DataGame3);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		return data;
	}

	public ArrayList<Game4Data> generateGame4 () {
		ArrayList<Game4Data> data = new ArrayList<GameData.Game4Data>(mGameData.DataGame4);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		return data;
	}
}
