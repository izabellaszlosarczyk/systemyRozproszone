module Account {
  exception Unlogged {};

  class Investment {
  	float rate;
  	int balance;
  	int period;
  	
  	float getRate();
  	int getBalance();
  	int getPeriod();
  };
  
  class Loan {
  	float interestRate;
  	int amount;
  	int period;
  	
  	float getInterestRate();
  	int getAmount();
  	int getPeriod();
  };

  sequence<Investment> Investments;
  sequence<Loan> Loans;

  interface CustomerAccount
  {
  	string login();
  	void logout(string token) throws Unlogged;
  	Investments getInvestmentsList(string token) throws Unlogged;
  	Loans getLoansList(string token) throws Unlogged;
	["amd"] float calculateLoan(string token, int period, int amount) throws Unlogged; //period w dniach
	["amd"] float calculateInvestment(string token, int period, int amount) throws Unlogged; //period w dniach
  };

};