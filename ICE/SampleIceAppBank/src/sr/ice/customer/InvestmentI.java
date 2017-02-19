package sr.ice.customer;

import Account.Investment;
import Ice.Current;

public class InvestmentI extends Investment {
	public InvestmentI(float rate, int balance, int period) {
		super(rate, balance, period);
	}

	@Override
	public float getRate(Current __current) {
		return this.rate;
	}

	@Override
	public int getBalance(Current __current) {
		return this.balance;
	}

	@Override
	public int getPeriod(Current __current) {
		return this.period;
	}

}
