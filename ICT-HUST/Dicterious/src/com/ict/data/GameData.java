
package com.ict.data;

import java.util.ArrayList;

public class GameData {

	ArrayList<Game1Data> DataGame1;
	ArrayList<Game3Data> DataGame3;
	ArrayList<Game4Data> DataGame4;

	public static final class Game1Data {
		public String reading;
		public Question question;

		public static class Question {
			public String question;
			public String answer;
		}
	}

	public static final class Game3Data {
		public ArrayList<String> sentence;
		public int error;
	}

	public static final class Game4Data {
		public ArrayList<String> sentence;
		public ArrayList<Integer> phrase;
		public boolean answer;
	}
}
