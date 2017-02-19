package sr.ice.server.servants;

import Ice.Current;
import Ice.Identity;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import sr.ice.impl.CalcI;
import sr.ice.server.servants.evictor.EvictorBase;
import sr.ice.server.utils.Serializer;
import Demo.*;



public class ServantLocator5  extends EvictorBase {
	private String id = null;
	private int evictCounter = 0;

	public ServantLocator5(String id, int size) {
		super(size);
		this.id = id;
		System.out.println("## ServantLocator1(" + id + ") ##");
	}

	private String identitySerializationPath(int count) {
		return "./serializables/serializable_5_" +  count + ".ser";
	}
	
	@Override
	public Object add(Current c, LocalObjectHolder cookie) {
		return new CalcI(3);
	}

	@Override
	public void evict(Object servant, java.lang.Object cookie, Ice.Identity id) {
		System.out.println("Deleting " + id.name);
		Serializer.serialize((CalcI) servant, identitySerializationPath(evictCounter++));
	}
}