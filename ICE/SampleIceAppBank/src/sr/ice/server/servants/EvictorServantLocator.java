package sr.ice.server.servants;

import Ice.Current;
import Ice.Identity;
import Ice.LocalObjectHolder;
import Ice.Object;
import sr.ice.customer.CustomerAccountI;
import sr.ice.server.servants.evictor.EvictorBase;

public class EvictorServantLocator  extends EvictorBase {
	public EvictorServantLocator(int size) {
		super(size);
	}
	
	@Override
	public Object add(Current c, LocalObjectHolder cookie) {
		// w konstruktorze random..
		Identity identity = c.id;
		System.out.println("New client");
		System.out.println(identity.name + " " + identity.category);
		return new CustomerAccountI();
	}

	@Override
	public void evict(Object servant, java.lang.Object cookie, Ice.Identity id) {
		System.out.println("Deleting " + id.name);
		//TODO: evict servant
	}
}