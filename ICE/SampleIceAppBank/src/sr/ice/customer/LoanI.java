package sr.ice.customer;

import Account.Loan;
import Ice.Current;

public class LoanI extends Loan {
	public LoanI(float interestRate, int amount, int period) {
		super(interestRate, amount, period);
	}

	@Override
	public float getInterestRate(Current __current) {
		return this.interestRate;
	}

	@Override
	public int getAmount(Current __current) {
		return this.amount;
	}

	@Override
	public int getPeriod(Current __current) {
		return this.period;
	}

}
