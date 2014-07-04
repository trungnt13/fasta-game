package org.tntstudio.lua;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.keplerproject.luajava.LuaObject;
import org.tntstudio.resources.FilePath;
import org.tntstudio.utils.io.IOUtils;

import com.badlogic.gdx.Gdx;

public abstract class LuaScript implements Script{
	@Override
	public LuaObject getScriptObject (String objName) {
		return null;
	}
	
	static String readStringFromFile (String filePath) {
		if (Gdx.app == null) {
			try {
				return IOUtils.toString(new FileInputStream(new File(filePath)));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		} else {
			return FilePath.FileHandleResolver.resolve(filePath).readString();
		}
		return null;
	}
	
}
