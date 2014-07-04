
package org.tntstudio.lua;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import org.tntstudio.resources.FilePath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/** This class is used for communicating purpose with Lua Code from <b>LuaJava</b>
 * @author trungnt13 */
public final class LuaAndroidScript extends LuaScript {
	// ///////////////////////////////////////////////////////////////
	// params
	// ///////////////////////////////////////////////////////////////

	public final LuaState luaState;
	private final Array<String> mFileList = new Array<String>();
	private final ObjectMap<String, LuaObject> mCachedLuaObj = new ObjectMap<String, LuaObject>();
	private final AndroidFunctionHandler mFunctionHandler = new AndroidFunctionHandler();
	private boolean isDisposed = false;

	// ///////////////////////////////////////////////////////////////
	// initializer
	// ///////////////////////////////////////////////////////////////

	/** Constructor
	 * @param fileName File name with Lua script. */
	LuaAndroidScript () {
		this.luaState = LuaStateFactory.newLuaState();
		this.luaState.openLibs();
	}

	public LuaAndroidScript loadFile (String... fileName) {
		for (String s : fileName) {
			String file = readStringFromFile(s);
			if (file == null) continue;
			this.luaState.LdoString(file);
		}
		mFileList.addAll(fileName);
		return this;
	}

	public LuaAndroidScript load (String... data) {
		for (String s : data) {
			luaState.LdoString(s);
		}
		return this;
	}

	public LuaAndroidScript reload () {
		if (Gdx.app != null) {
			for (String s : mFileList) {
				String file = readStringFromFile(s);
				this.luaState.LdoString(file);
			}
		} else {
			for (String s : mFileList) {
				String file = FilePath.FileHandleResolver.resolve(s).readString();
				this.luaState.LdoString(file);
			}
		}
		return this;
	}

	// ///////////////////////////////////////////////////////////////
	// executor
	// ///////////////////////////////////////////////////////////////

	public FunctionHandler function (String functionName) {
		luaState.getGlobal(functionName);
		return mFunctionHandler.start();
	}

	/*-------- proxy --------*/
	public LuaObject getScriptObject (String objName) {
		LuaObject obj = mCachedLuaObj.get(objName);
		if (obj == null) {
			obj = luaState.getLuaObject(objName);
			mCachedLuaObj.put(objName, obj);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy (String table, Class<T> type) {
		final LuaObject obj = getScriptObject(table);
		try {
			return (T)obj.createProxy(type.getName());
		} catch (ClassNotFoundException e) {
		} catch (LuaException e) {
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	public String getGlobalString (String globalName) {
		this.luaState.getGlobal(globalName);
		return this.luaState.toString(luaState.getTop());
	}

	public double getGlobalNumber (String globalName) {
		this.luaState.getGlobal(globalName);
		return this.luaState.toNumber(luaState.getTop());
	}

	public boolean getGlobalBoolean (String globalName) {
		this.luaState.getGlobal(globalName);
		return this.luaState.toBoolean(luaState.getTop());
	}

	public Object getGlobalJavaObject (String globalName) {
		this.luaState.getGlobal(globalName);
		try {
			return this.luaState.toJavaObject(luaState.getTop());
		} catch (LuaException e) {
			return null;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper
	// ///////////////////////////////////////////////////////////////

	@Override
	public String toString () {
		return luaState.dumpStack();
	}

	@Override
	/** Ends the use of Lua environment. */
	public void dispose () {
		mFileList.clear();
		mCachedLuaObj.clear();
		this.luaState.close();
		isDisposed = true;
	}

	@Override
	public boolean isDisposed () {
		return isDisposed;
	}

	// ///////////////////////////////////////////////////////////////
	// classes
	// ///////////////////////////////////////////////////////////////

	/** Helper class to handle function in lua
	 * @author trungnt13 */
	public class AndroidFunctionHandler extends FunctionHandler {
		private int paramCount = 0;
		private final Array<Integer> returnCode = new Array<Integer>();

		private AndroidFunctionHandler () {
		}

		FunctionHandler start () {
			paramCount = 0;
			returnCode.clear();
			return this;
		}

		public FunctionHandler string (String... s) {
			for (String string : s) {
				luaState.pushString(string);
			}
			paramCount += s.length;
			return this;
		}

		public FunctionHandler bool (boolean... b) {
			for (boolean bool : b) {
				luaState.pushBoolean(bool);
			}
			paramCount += b.length;
			return this;
		}

		public FunctionHandler integer (int... i) {
			for (int j : i) {
				luaState.pushInteger(j);
			}
			paramCount += i.length;
			return this;
		}

		public FunctionHandler number (double... i) {
			for (double db : i) {
				luaState.pushNumber(db);
			}
			paramCount += i.length;
			return this;
		}

		public FunctionHandler obj (Object... objects) {
			for (Object object : objects) {
				luaState.pushJavaObject(object);
			}
			paramCount += objects.length;
			return this;
		}

		public FunctionHandler func (JavaFunction... functions) {
			for (JavaFunction f : functions) {
				try {
					luaState.pushJavaFunction(f);
				} catch (LuaException e) {
				}
			}
			paramCount += functions.length;
			return this;
		}

		public void call () {
			luaState.call(paramCount, 0);
		}

		public Array<Integer> call (int numberOfReturns) {
			luaState.call(paramCount, numberOfReturns);
			for (int i = 0; i < numberOfReturns; i++) {
				returnCode.add(luaState.getTop());
			}
			return returnCode;
		}
	}

}
