package pl.edu.agh.iisg.rozprochy.gra;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IListener extends Remote {
	void loggedIn(String mark) throws RemoteException;
	void secondPlayerConnected(String name) throws RemoteException;
	void gameOver(String winner) throws RemoteException;
	void playerMove(int x, int y, String value) throws RemoteException;
	void currentActivePlayer(String name) throws RemoteException;
	void gameOverWithNoWinner() throws RemoteException;
	
	void playerDisConnected(String out) throws RemoteException;
}
