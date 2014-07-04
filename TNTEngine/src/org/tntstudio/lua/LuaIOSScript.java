package org.tntstudio.lua;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaObject;

import com.badlogic.gdx.utils.Array;

public class LuaIOSScript extends LuaScript{

	private final IOSFunctionHandler mFunctionHandler = new IOSFunctionHandler();
	
	LuaIOSScript () {
	}
	@Override
	public void dispose () {
	}

	@Override
	public boolean isDisposed () {
		return false;
	}

	@Override
	public Script loadFile (String... fileName) {
		return this;
	}

	@Override
	public Script load (String... data) {
		return this;
	}

	@Override
	public Script reload () {
		return this;
	}

	@Override
	public FunctionHandler function (String functionName) {
		return mFunctionHandler;
	}

	@Override
	public LuaObject getScriptObject (String objName) {
		return null;
	}

	@Override
	public <T> T getProxy (String table, Class<T> type) {
		return null;
	}

	@Override
	public String getGlobalString (String globalName) {
		return null;
	}

	@Override
	public double getGlobalNumber (String globalName) {
		return 0;
	}

	@Override
	public boolean getGlobalBoolean (String globalName) {
		return false;
	}

	@Override
	public Object getGlobalJavaObject (String globalName) {
		return null;
	}

	// ///////////////////////////////////////////////////////////////
	// function handler
	// ///////////////////////////////////////////////////////////////

	public class IOSFunctionHandler extends FunctionHandler{

		@Override
		FunctionHandler start () {
			return this;
		}

		@Override
		public FunctionHandler string (String... s) {
			return this;
		}

		@Override
		public FunctionHandler bool (boolean... b) {
			return this;
		}

		@Override
		public FunctionHandler integer (int... i) {
			return this;
		}

		@Override
		public FunctionHandler number (double... i) {
			return this;
		}

		@Override
		public FunctionHandler obj (Object... objects) {
			return this;
		}

		public FunctionHandler func (JavaFunction... functions) {
			return this;
		}

		@Override
		public void call () {
			
		}

		@Override
		public Array<Integer> call (int numberOfReturns) {
			return null;
		}
	}

}
