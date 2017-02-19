package sr.ice.server.servants;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import sr.ice.impl.CalcI;

public class ServantLocator4 implements Ice.ServantLocator
{
	private String id = null;
	private static CalcI servant = new CalcI(4);
	
	public ServantLocator4(String id)
	{
		this.id = id;
		System.out.println("## ServantLocator4(" + id + ") ##");
	}

	public Object locate(Current curr, LocalObjectHolder cookie) throws UserException 
	{
		System.out.println("## ServantLocator4 #" +id + " .locate() ##");
		return servant;
	}

	public void finished(Ice.Current curr, Ice.Object servant, java.lang.Object cookie) throws UserException 
	{
		System.out.println("## ServantLocator4 #" +id + " .finished() ##");
	}

	public void deactivate(String category)
	{
		System.out.println("## ServantLocator4 #" +id + " .deactivate() ##");
	}
}
