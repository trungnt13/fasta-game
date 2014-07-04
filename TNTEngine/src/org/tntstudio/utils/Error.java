
package org.tntstudio.utils;

import org.tntstudio.Const.ErrorCode;

public final class Error {
	public ErrorCode Code;
	public Object Msg;
	public Throwable Exception;
	public Runnable Solution;

	public Error (ErrorCode code, Object msg, Throwable t, Runnable s) {
		Code = code;
		Msg = msg;
		Exception = t;
		Solution = s;
	}
}
