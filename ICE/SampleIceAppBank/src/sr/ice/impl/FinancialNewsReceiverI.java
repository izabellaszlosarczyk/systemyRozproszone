package sr.ice.impl;

import FinancialNews.Currency;
import FinancialNews.FinancialNewsReceiver;
import FinancialNews._FinancialNewsReceiverDisp;
import Ice.Current;
import Ice.DispatchInterceptorAsyncCallback;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.Object;
import Ice.OutputStream;
import Ice.Request;
import IceInternal.BasicStream;
import IceInternal.Incoming;

public class FinancialNewsReceiverI extends _FinancialNewsReceiverDisp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7738078490358168098L;

	@Override
	public void interestRate(float rate, Currency curr, Current __current) {
		// TODO Auto-generated method stub
		System.out.println("INTEREST RATE");
		System.out.println(rate);
		System.out.println(curr);
		News.setInterestRate(curr, rate);
	}

	@Override
	public void exchangeRate(float rate, Currency curr1, Currency curr2, Current __current) {
		// TODO Auto-generated method stub
		System.out.println("EXCHANGE RATE");
		System.out.println(rate);
		System.out.println(curr1);	
		News.setExchangeRate(curr1, curr2 , rate);
	}
	

}
