
package org.tntstudio.resources.gson;

import org.tntstudio.resources.FilePath;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

@SuppressWarnings("rawtypes")
public class FilePathJson implements Serializer<FilePath> {

	@Override
	public void write (Json json, FilePath object, Class knownType) {
		json.writeValue(object.filePath);
	}

	@Override
	public FilePath read (Json json, JsonValue jsonData, Class type) {
		if (jsonData.isString()) return new FilePath(jsonData.asString());
		return new FilePath(jsonData.getString("filePath"));
	}
}
