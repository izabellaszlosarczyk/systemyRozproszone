package sr.ice.server.servants;

import Ice.Current;
import Ice.Identity;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import sr.ice.impl.CalcI;
import sr.ice.server.utils.Serializer;

import java.util.HashMap;
import java.util.Map.Entry;

import Demo.*;



public class ServantLocator1 implements Ice.ServantLocator
{
	private String id = null;
	private String category = null;
	
	private HashMap<Identity, Object> objectsMap = new HashMap<Identity, Object>();
	
	public ServantLocator1(String id)
	{
		this.id = id;
		System.out.println("## ServantLocator1(" + id + ") ##");
	}
	
	private String identitySerializationPath(Identity identity) {
		return "./serializables/serializable_" + identity.name + "_" + identity.category + ".ser";
	}

	public Object locate(Current curr, LocalObjectHolder cookie) throws UserException 
	{
		System.out.println("## ServantLocator1 #" +id + " .locate() ##");
		
		Identity identity = curr.id;
		if(objectsMap.containsKey(identity)) {
			return objectsMap.get(identity);
		} else {
			String path = identitySerializationPath(identity);
			Object servant = (Object) Serializer.deserialize(path);
			if(servant == null) {
				servant = new CalcI(1);
				Serializer.serialize(servant, path); //save it to file
			}
			objectsMap.put(identity, servant);
			return servant;
		}
	}

	public void finished(Ice.Current curr, Ice.Object servant, java.lang.Object cookie) throws UserException 
	{	
		System.out.println("## ServantLocator1 #" +id + " .finished() ##");
	}

	public void deactivate(String category)
	{
		for(Entry<Identity, Object> entry : objectsMap.entrySet()) {
			String path = identitySerializationPath(entry.getKey());
			Serializer.serialize(entry.getValue(), path);
		}
		System.out.println("## ServantLocator1 #" +id + " .deactivate() ##");
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
