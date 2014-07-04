
package org.tntstudio.lua;

import com.badlogic.gdx.utils.Array;

/** Helper class to handle function in lua
 * @author trungnt13 */
abstract class FunctionHandler {

	abstract FunctionHandler start ();

	public abstract FunctionHandler string (String... s);

	public abstract FunctionHandler bool (boolean... b);

	public abstract FunctionHandler integer (int... i);

	public abstract FunctionHandler number (double... i);

	public abstract FunctionHandler obj (Object... objects);

	public abstract void call ();

	public abstract Array<Integer> call (int numberOfReturns);
}
