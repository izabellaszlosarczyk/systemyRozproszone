package sr.ice.server.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sr.ice.impl.CalcI;

public class Serializer {
	public static void serialize(Object object) {
		serialize(object, "./serializer.ser");
	}
	
	public static void serialize(Object object, String path) {
		try {
			FileOutputStream fOutputStream = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object deserialize() {
		return deserialize("./serializer.ser");
	}
	
	public static Object deserialize(String path) {
		Object object = null;
		try {
			FileInputStream fInputStream = new FileInputStream(path);
			ObjectInputStream objectInputStream = new ObjectInputStream(fInputStream);
			object = objectInputStream .readObject();
			objectInputStream.close();
		} catch (Exception e) {
			//e.printStackTrace(); 
			//file not found, return null
		}
		return object;
	}
}
