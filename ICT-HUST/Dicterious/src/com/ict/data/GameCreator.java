
package com.ict.data;

import java.util.ArrayDeque;
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

	public ArrayDeque<Game1Data> generateGame1 () {
		ArrayList<Game1Data> data = new ArrayList<GameData.Game1Data>(mGameData.DataGame1);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		ArrayDeque<Game1Data> results = new ArrayDeque<GameData.Game1Data>();
		for (Game1Data game1Data : data) {
			results.add(game1Data);
		}
		return results;
	}

	public ArrayDeque<Game3Data> generateGame3 () {
		ArrayList<Game3Data> data = new ArrayList<GameData.Game3Data>(mGameData.DataGame3);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		ArrayDeque<Game3Data> results = new ArrayDeque<GameData.Game3Data>();
		for (Game3Data game1Data : data) {
			results.add(game1Data);
		}
		return results;
	}

	public ArrayDeque<Game4Data> generateGame4 () {
		ArrayList<Game4Data> data = new ArrayList<GameData.Game4Data>(mGameData.DataGame4);
		for (int i = 0; i < data.size(); i++) {
			Collections.swap(data, i, mRand.nextInt(data.size()));
		}
		ArrayDeque<Game4Data> results = new ArrayDeque<GameData.Game4Data>();
		for (Game4Data game1Data : data) {
			results.add(game1Data);
		}
		return results;
	}
}
