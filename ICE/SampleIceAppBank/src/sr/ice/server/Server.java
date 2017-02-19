package sr.ice.server;

import FinancialNews.FinancialNewsReceiverPrx;
import FinancialNews.FinancialNewsReceiverPrxHelper;
import FinancialNews.FinancialNewsServerPrx;
import FinancialNews.FinancialNewsServerPrxHelper;
import sr.ice.impl.FinancialNewsReceiverI;
import sr.ice.impl.News;
import sr.ice.server.servants.EvictorServantLocator;

public class Server {

	public void t1(String[] args)
	{
		int status = 0;
		Ice.Communicator communicator = null;

		try {
			communicator = Ice.Util.initialize(args);
			
			System.out.println("Creating adapters...");
			Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");
			
			Ice.ServantLocator locator = new EvictorServantLocator(10);
			adapter.addServantLocator(locator, "");
			adapter.activate();
			
			// news
			System.out.println("Creating news server connection...");
			
			FinancialNewsServerPrx server = FinancialNewsServerPrxHelper.checkedCast(communicator
					.propertyToProxy("NewsServer.Proxy").ice_twoway()
					.ice_timeout(-1).ice_secure(false));
			
			if (server == null) {
				System.err.println("invalid proxy");
				return;
			}

			Ice.ObjectAdapter newsAdapter = communicator.createObjectAdapter(""); //chcemy wykorzystać istniejące połączenie TCP (port efemeryczny jego strony klienckiej),  więc nie określamy żadnego gniazda)
			Ice.Identity ident = new Ice.Identity(); //utwórz instancję Identity (pusta kategoria, dowolna nazwa (najlepiej, by była unikalna))
			ident.name = java.util.UUID.randomUUID().toString();
			ident.category = "";
			FinancialNewsReceiverI callback = new FinancialNewsReceiverI(); //to lokalny serwant odbierający notyfikacje
			FinancialNewsReceiverPrx cprx = FinancialNewsReceiverPrxHelper.checkedCast(newsAdapter.add(callback, ident));
			newsAdapter.activate();
			Ice.Connection connection = server.ice_getConnection();
			connection.setAdapter(newsAdapter);
			System.out.println("Entering event processing loop...");
			server.registerForNews(cprx); 
			
			// okresowy ping
			Thread pingThread = new Thread() {
				@Override
			    public void run() {
			    	while(true) {
			    		System.out.println("Pinging server...");
				    	server.ice_ping();
				    	try {
							sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
			    }
			};
			pingThread.start();
			
			communicator.waitForShutdown();
		} catch (Exception e) {
			e.printStackTrace();
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
