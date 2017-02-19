package pl.edu.agh.iisg.rozprochy.gra;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import pl.edu.agh.iisg.rozprochy.gra.exceptions.FieldTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public interface IGame extends Remote {
	Map<String, String> login(String name, IListener listener, boolean withBot) throws RemoteException, LoginTakenException, TooManyPlayersException;
	void logout(String name) throws RemoteException;
	void markField(int x, int y, String name) throws RemoteException, FieldTakenException;
}
