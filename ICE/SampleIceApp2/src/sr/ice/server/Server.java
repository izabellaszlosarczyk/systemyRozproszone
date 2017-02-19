// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package sr.ice.server;

import Demo.*;
import Ice.Identity;
import sr.ice.impl.CalcI;
import sr.ice.server.servants.ServantLocator1;
import sr.ice.server.servants.ServantLocator2;
import sr.ice.server.servants.ServantLocator3;
import sr.ice.server.servants.ServantLocator4;
import sr.ice.server.servants.ServantLocator5;

public class Server
{
	public void t1(String[] args)
	{
		int status = 0;
		Ice.Communicator communicator = null;

		try
		{
			communicator = Ice.Util.initialize(args);
			Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");  
			
			//Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1", 
			//		"tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
			int N = 6;
			
			Ice.ServantLocator locatorK1 = new ServantLocator1("K1");
			Ice.ServantLocator locatorK2 = new ServantLocator2("K2");
			Ice.ServantLocator locatorK3 = new ServantLocator3("K3", N);
			Ice.ServantLocator locatorK4 = new ServantLocator4("K4");
			Ice.ServantLocator locatorK5 = new ServantLocator5("K5", N);
			adapter.addServantLocator(locatorK1, "K1");
			adapter.addServantLocator(locatorK2, "K2");
			adapter.addServantLocator(locatorK3, "K3");
			adapter.addServantLocator(locatorK4, "K4");
			adapter.addServantLocator(locatorK5, "K5");

           //. Aktywacja adaptera i przej�cie w p�tl� przetwarzania ��da�
			adapter.activate();
			System.out.println("Entering event processing loop...");
			communicator.waitForShutdown();
		}
		catch (Exception e)
		{
			System.err.println(e);
			status = 1;
		}
		if (communicator != null)
		{
			// Clean up
			try
			{
				communicator.destroy();
			}
			catch (Exception e)
			{
				System.err.println(e);
				status = 1;
			}
		}
		System.exit(status);
	}


	public static void main(String[] args)
	{
		Server app = new Server();
		app.t1(args);
	}
}
