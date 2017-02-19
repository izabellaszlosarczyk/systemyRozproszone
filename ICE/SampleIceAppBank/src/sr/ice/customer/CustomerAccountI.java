package sr.ice.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Account.AMD_CustomerAccount_calculateInvestment;
import Account.AMD_CustomerAccount_calculateLoan;
import Account.Investment;
import Account.InvestmentPrx;
import Account.Loan;
import Account.Unlogged;
import Account._CustomerAccountDisp;
import Ice.Current;

public class CustomerAccountI extends _CustomerAccountDisp {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5765900627898961387L;
	private String token;
	private List<Loan> loans = new ArrayList<Loan>();
	private List<Investment> investments = new ArrayList<Investment>();

	public CustomerAccountI() {
		//generate loans
		Random random = new Random();
		int maxLoansCount = random.nextInt(3);
		for(int i = 0; i < maxLoansCount; i++) {
			float interestRate = random.nextFloat()*0.4f; //maksymalnie 0.4f
			int amount = random.nextInt(1000000) + 1000; // 
			int period = random.nextInt(3650-90) + 90; //od 3 miesiecy do 10 lat
			loans.add(new LoanI(interestRate, amount, period));
		}
		
		int maxInvestmentsCount = random.nextInt(3);
		for(int i = 0; i < maxInvestmentsCount; i++) {
			float rate = random.nextFloat()*0.4f; //maksymalnie 0.4f
			int balance = random.nextInt(1000000) + 1000; // 
			int period = random.nextInt(365-10) + 10; //od 10 dni do roku
			investments.add(new InvestmentI(rate, balance, period));
		}
	}
	
	private boolean checkToken(String token) {
		return this.token != null && this.token.equals(token);
	}

	@Override
	public String login(Current __current) {
		System.out.println("Login");
		token = java.util.UUID.randomUUID().toString();
		return token;
	}

	@Override
	public void logout(String token, Current __current) throws Unlogged {
		System.out.println("Logout");
		if(!checkToken(token)) {
			throw new Unlogged();
		}
		token = null;
	}

	@Override
	public void calculateLoan_async(AMD_CustomerAccount_calculateLoan __cb, String token, int period, int amount,
			Current __current) throws Unlogged {
		System.out.println("Calculated loan start");
		if(!checkToken(token)) {
			throw new Unlogged();
		}
		
		// TODO Auto-generated method stub
		System.out.println("Calculated loan");
		Random random = new Random();
		float interestRate = random.nextFloat()*0.4f;
		float rateWithCCurr = 0.0f;
		HashMap<String, Float> interestRates = new HashMap<String, Float>();
		for (HashMap.Entry<String, Float> entry : interestRates.entrySet()) {
			 if ((entry.getKey()).equalsIgnoreCase("euro")){
		    	 rateWithCCurr =  rateWithCCurr+(entry.getValue());
		    }
		    // ...
		}
		System.out.println("For euro:"+ rateWithCCurr);
		HashMap<String, Float> exchangeRates = new HashMap<String, Float>();
		for (HashMap.Entry<String, Float> entry : exchangeRates.entrySet()) {
			 if ((entry.getKey()).equalsIgnoreCase("usd")){
		    	 rateWithCCurr =  rateWithCCurr+(entry.getValue());
		    }
		    // ...
		}
		System.out.println("For usd:"+ rateWithCCurr);
		__cb.ice_response(interestRate);
	}

	@Override
	public void calculateInvestment_async(AMD_CustomerAccount_calculateInvestment __cb, String token, int period,
			int amount, Current __current) throws Unlogged {
		System.out.println("Calculated investment start");
		if(!checkToken(token)) {
			throw new Unlogged();
		}
		
		// TODO Auto-generated method stub
		System.out.println("Calculated investment");
		Random random = new Random();
		float interestRate = random.nextFloat()*0.4f;
		__cb.ice_response(interestRate );
	}

	@Override
	public Investment[] getInvestmentsList(String token, Current __current) throws Unlogged {
		if(!checkToken(token)) {
			throw new Unlogged();
		}
		
		Investment[] array = new Investment[investments.size()];
		for(int i = 0; i < investments.size(); i++) {
			System.out.println(investments.get(i));
			array[i] = investments.get(i);
		}
		
		System.out.println(array.length);
		
		return array;
	}

	@Override
	public Loan[] getLoansList(String token, Current __current) throws Unlogged {
		if(!checkToken(token)) {
			throw new Unlogged();
		}
		
		return (Loan[]) loans.toArray();
	}

}
