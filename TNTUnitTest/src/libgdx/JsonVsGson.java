
package libgdx;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class JsonVsGson {

	public static class Test {
		public String Name = "TrungNT";
	}

	public static class Path {
		public String Name = "TrungAnh4ever";

		@Override
		public String toString () {
			return "Path " + Name;
		}
	}

	public static class TestSerialize implements Json.Serializer<Test> {
		@Override
		public void write (Json json, Test object, Class knownType) {
			System.out.println("---Write");

			System.out.println(object);
			System.out.println(knownType);
			json.writeObjectStart();
			json.writeValue("Name1", object.Name);
			json.writeValue("Name2", "Anh");
			json.writeObjectEnd();
		}

		@Override
		public Test read (Json json, JsonValue jsonData, Class type) {
			System.out.println("---Read");
			Test t = new Test();
			if (jsonData.isObject()) {
				t.Name = jsonData.get("Name1").asString() + jsonData.get("Name2").asString();
			}
			return t;
		}
	}

	public static class PathSerialize implements Json.Serializer<Path> {

		@Override
		public void write (Json json, Path object, Class knownType) {
			json.writeValue(object.Name);
		}

		@Override
		public Path read (Json json, JsonValue jsonData, Class type) {
			Path p = new Path();
			p.Name = jsonData.asString();
			return p;
		}

	}

	public static class File {
		final HashMap<String, Path> files = new HashMap<String, JsonVsGson.Path>();

		@Override
		public String toString () {
			return files.toString();
		}
	}

	public static class FileString {
		final HashMap<String, String> files = new HashMap<String, String>();

		@Override
		public String toString () {
			return files.toString();
		}
	}

	public static class FileStringSerialize implements Serializer<FileString> {

		@Override
		public void write (Json json, FileString object, Class knownType) {

		}

		@Override
		public FileString read (Json json, JsonValue jsonData, Class type) {
			System.out.println("-------------- ");
			JsonValue obj = jsonData.get("files");
			JsonValue val = null;
			for (int i = 0; i < obj.size; i++) {
				System.out.println(obj.get(i).name());
				System.out.println(obj.get(i).asString());
			}
			return null;
		}

	}

	public static void main (String[] args) {
		String data = null;
		Json json = new Json();

		/*-------- test --------*/

		json.setSerializer(Test.class, new TestSerialize());
		System.out.println(data = json.prettyPrint(json.toJson(new Test())));

		System.out.println(json.fromJson(Test.class, data).Name);

		/*-------- path --------*/

		File f = new File();
		f.files.put("Trung1", new Path());
		f.files.put("Trung2", new Path());
		f.files.put("Anh1", new Path());
		f.files.put("Anh2", new Path());

		json.setSerializer(Path.class, new PathSerialize());
		json.setSerializer(FileString.class, new FileStringSerialize());

		System.out.println(data = json.prettyPrint(f));

		FileString f1;
		System.out.println(f1 = json.fromJson(FileString.class, data));
		System.out.println(f1.files.get("Trung1").getClass());

		File f2;
		System.out.println(f2 = json.fromJson(File.class, data));
		System.out.println(f2.files.get("Trung1").getClass());

	}
}
