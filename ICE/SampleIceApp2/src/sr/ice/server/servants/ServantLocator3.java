package sr.ice.server.servants;

import java.util.Stack;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import sr.ice.impl.CalcI;
import sr.ice.server.servants.evictor.EvictorBase;

public class ServantLocator3 implements Ice.ServantLocator
{
	private String id = null;
	private int size = 1000;
	private Stack<CalcI> servants;
	
	public ServantLocator3(String id, int size)
	{
		this.id = id;
		this.size = size;
		System.out.println("## ServantLocator3(" + id + ") ##");
		servants = new Stack<CalcI>();
		for(int i = 0; i < size; i++) {
			System.out.println("Creating servant..");
			servants.push(new CalcI(3));
		}
	}

	public Object locate(Current curr, LocalObjectHolder cookie) throws UserException 
	{
		System.out.println("## ServantLocator3 #" +id + " .locate() ##");
		if(servants.isEmpty()) {
			// niedoprecyzowane co zrobić, więc dodaje nowy
			servants.push(new CalcI(3));
		}
		return servants.pop();
	}

	public void finished(Ice.Current curr, Ice.Object servant, java.lang.Object cookie) throws UserException 
	{
		servants.push((CalcI) servant);
		System.out.println("Putting back on stack...");
		System.out.println("## ServantLocator3 #" +id + " .finished() ##");
	}

	public void deactivate(String category)
	{
		System.out.println("## ServantLocator3 #" +id + " .deactivate() ##");
	}
}
