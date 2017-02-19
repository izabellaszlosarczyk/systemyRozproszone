package sr.ice.impl;

import java.util.HashMap;

import FinancialNews.Currency;

public class News {
	
	private static HashMap<String, Float> interestRates = new HashMap<String, Float>();
	private static HashMap<String, Float> exchangeRates = new HashMap<String, Float>(); 
	
	public static void setInterestRate(Currency curr, float rate) {
		System.out.println("setInterestRate - News HashMap");
		interestRates.put(curr.toString(), rate);
	}
	 
	public static void setExchangeRate(Currency curr1, Currency curr2, float rate) {
		System.out.println("setInterestRate- News HashMap");
		exchangeRates.put(curr1.toString() + curr2.toString(), rate);
	}

	public static HashMap<String, Float> getInterestRates() {
		return interestRates;
	}
	
	public static HashMap<String, Float> getExchangeRates() {
		return exchangeRates;
	}

	
	
}
