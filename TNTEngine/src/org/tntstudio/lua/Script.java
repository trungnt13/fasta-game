package org.tntstudio.lua;

import com.badlogic.gdx.utils.Disposable;

public  interface Script extends Disposable{
	public abstract Script loadFile (String... fileName) ;

	public abstract Script load (String... data);

	public abstract Script reload () ;

	// ///////////////////////////////////////////////////////////////
	// executor
	// ///////////////////////////////////////////////////////////////

	public abstract FunctionHandler function (String functionName) ;

	/*-------- proxy --------*/
	public abstract Object getScriptObject (String objName) ;
	
	public abstract <T> T getProxy (String table, Class<T> type) ;

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	public abstract  String getGlobalString (String globalName) ;

	public abstract double getGlobalNumber (String globalName) ;

	public abstract boolean getGlobalBoolean (String globalName) ;

	public abstract Object getGlobalJavaObject (String globalName) ;

	public abstract boolean isDisposed();
	// ///////////////////////////////////////////////////////////////
	// helper
	// ///////////////////////////////////////////////////////////////

}
