import sys, traceback, Ice

Ice.loadSlice('ClientAccount.ice')
import Account

class LoanI(Account.Loan):
    def getInterestRate(self):
        return self.interestRate

    def getAmount(self):
        return self.amount

    def getPeriod(self):
        return self.period

def LoanValueFactory(type):
    print "TYP:"
    print type
    if type == Account.Loan.ice_staticId():
        return LoanI()

class Client(Ice.Application):
    def printMenu(self):
    	print "Wpisz [litera]:"
        print "[a]  oblicz pozyczke"
        print "[b]  oblicz lokate"
        print "[c]  lista lokat"
        print "[d]  lista pozyczek"
        print "[q]  wyjdz"

    def menu(self):
        key = None

        while(key != 'q'):
            self.printMenu()
            sys.stdout.write("# ")
            sys.stdout.flush()
            key = sys.stdin.readline().strip()

            if key == 'a':
            	print "Liczba dni pozyczki: "
            	days = sys.stdin.readline().strip()
            	print "Wartosc pozyczki: "
            	amount = sys.stdin.readline().strip()
                self.countLoan(int(days), int(amount))
            if key == 'b':
            	print "Liczba dni lokaty: "
            	days = sys.stdin.readline().strip()
            	print "Wartosc lokaty: "
            	amount = sys.stdin.readline().strip()
                self.countInvestment(int(days), int(amount))
            if key == 'c':
            	print "Lista lokat:"
                self.investmentsList()
            if key == 'd':
            	print "Lista pozyczek"
                self.loansList()
        self.logout()
    	print "Dziekuje!"

    def logout(self):
        self.account.logout(self.token)

    def countLoan(self, days, amount):
        self.account.begin_calculateLoan(token=self.token, period=days, amount=amount, _response=self.countLoanCB, _ex=self.countLoanEx)

    def countLoanCB(self, loan):
        print "Rata dla pozyczki: {0}".format(loan)

    def countLoanEx(self, ex):
        print "Error"
        print ex

    def countInvestment(self, days, amount):
        self.account.begin_calculateInvestment(token=self.token, period=days, amount=amount, _response=self.countInvestmentCB, _ex=self.countInvestmentEx)

    def countInvestmentCB(self, rate):
        print "Stopa procentowa dla lokaty: {0}".format(rate)

    def countInvestmentEx(self, ex):
        print "Error"
        print ex

    def loansList(self):
        print "NOT IMPLEMENTED"
        #self.account.getLoansList(self.token)

    def investmentsList(self):
        print "NOT IMPLEMENTED"
        #self.account.getInvestmentsList(self.token)

    def run(self, args):
        if len(args) > 1:
            print(self.appName() + ": too many arguments")
            return 1

        status = 0
        try:
            #self.ic = Ice.initialize(sys.argv)
            login = "login1"
            base = self.communicator().stringToProxy("{0}:tcp -h 0.0.0.0 -p 10000".format(login))
            self.account = Account.CustomerAccountPrx.checkedCast(base.ice_twoway().ice_timeout(-1).ice_secure(False))
            self.token = self.account.login()

            if not self.account:
                raise RuntimeError("Invalid proxy")
        except:
            traceback.print_exc()
            status = 1

        self.menu()

        if self.communicator():
            # Clean up
            try:
                self.communicator().destroy()
            except:
                traceback.print_exc()
                status = 1
        return status

app = Client()
sys.exit(app.main(sys.argv, "config.client"))
