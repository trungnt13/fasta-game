
package org.tntstudio.resources.gson;

import org.tntstudio.TNTRuntimeException;
import org.tntstudio.resources.FilePath;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

@SuppressWarnings("rawtypes")
public class ShaderProgramJson implements Serializer<ShaderProgram> {
	private static final String Vertex = "vertex";
	private static final String Fragment = "fragment";

	@Override
	public void write (Json json, ShaderProgram object, Class knownType) {
		throw new TNTRuntimeException("We not support serialize GameConfiguration from Json");
	}

	@Override
	public ShaderProgram read (Json json, JsonValue obj, Class type) {
		String vertexSource = FilePath.FileHandleResolver.resolve(obj.get(Vertex).asString()).readString();
		String fragSource = FilePath.FileHandleResolver.resolve(obj.get(Fragment).asString()).readString();
		return new ShaderProgram(vertexSource, fragSource);
	}

}
