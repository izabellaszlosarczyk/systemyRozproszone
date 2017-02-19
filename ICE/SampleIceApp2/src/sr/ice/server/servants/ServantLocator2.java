package sr.ice.server.servants;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import sr.ice.impl.CalcI;

public class ServantLocator2 implements Ice.ServantLocator
{
	private String id = null;
	
	public ServantLocator2(String id)
	{
		this.id = id;
		System.out.println("## ServantLocator2(" + id + ") ##");
	}

	public Object locate(Current curr, LocalObjectHolder cookie) throws UserException 
	{
		System.out.println("## ServantLocator2 #" +id + " .locate() ##");
		
		CalcI servant = new CalcI(2);
		return servant;
	}

	public void finished(Ice.Current curr, Ice.Object servant, java.lang.Object cookie) throws UserException 
	{
		System.out.println("## ServantLocator2 #" +id + " .finished() ##");
	}

	public void deactivate(String category)
	{
		System.out.println("## ServantLocator2 #" +id + " .deactivate() ##");
	}
}
