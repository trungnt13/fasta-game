
package com.ict.data;

import java.util.ArrayList;

public class GameData {
	public ArrayList<GameData1> DataGame1;
	public ArrayList<GameData3> DataGame3;
	public ArrayList<GameData4> DataGame4;

	public static class GameData1 {
		public String reading;
		public Question question;

		public static class Question {
			public String question;
			public String answer;
		}
	}

	public static class GameData3 {
		public ArrayList<String> sentence;
		public int error;
	}

	public static class GameData4 {
		public boolean answer;
		public ArrayList<Integer> pharse;
		public ArrayList<String> sentence;
	}
}
