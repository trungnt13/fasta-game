
package org.tntstudio.resources;

import org.tntstudio.TNTRuntimeException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class FilePath implements FileHandleResolver {
	public static final String External = "external";
	public static final String Internal = "internal";
	public static final String Classpath = "classpath";
	public static final String Absolute = "absolute";
	public static final String Local = "local";

	public final String filePath;

	public static final FileHandleResolver FileHandleResolver = new FilePath(null);

	public FilePath (String file) {
		filePath = file;
	}

	public FileHandle resolve () {
		FileHandle fileHandle = null;
		String[] tmp = filePath.split(":");

		if (tmp.length == 1) {
			fileHandle = Gdx.files.internal(filePath);
		} else {
			if (tmp[0].equals(External)) {
				fileHandle = Gdx.files.external(tmp[1]);
			} else if (tmp[0].equals(Internal)) {
				fileHandle = Gdx.files.internal(tmp[1]);
			} else if (tmp[0].equals(Classpath)) {
				fileHandle = Gdx.files.classpath(tmp[1]);
			} else if (tmp[0].equals(Local)) {
				fileHandle = Gdx.files.local(tmp[1]);
			} else if (tmp[0].equals(Absolute)) {
				fileHandle = Gdx.files.absolute(tmp[1]);
			} else {
				throw new TNTRuntimeException("Invalid file path " + filePath);
			}
		}
		return fileHandle;
	}

	public FileHandle resolve (String FilePath) {
		FileHandle fileHandle = null;
		String[] tmp = FilePath.split(":");

		if (tmp.length == 1) {
			fileHandle = Gdx.files.internal(FilePath);
		} else {
			if (tmp[0].equals(External)) {
				fileHandle = Gdx.files.external(tmp[1]);
			} else if (tmp[0].equals(Internal)) {
				fileHandle = Gdx.files.internal(tmp[1]);
			} else if (tmp[0].equals(Classpath)) {
				fileHandle = Gdx.files.classpath(tmp[1]);
			} else if (tmp[0].equals(Local)) {
				fileHandle = Gdx.files.local(tmp[1]);
			} else if (tmp[0].equals(Absolute)) {
				fileHandle = Gdx.files.absolute(tmp[1]);
			} else {
				throw new TNTRuntimeException("Invalid file path " + FilePath);
			}
		}
		return fileHandle;
	}
}
