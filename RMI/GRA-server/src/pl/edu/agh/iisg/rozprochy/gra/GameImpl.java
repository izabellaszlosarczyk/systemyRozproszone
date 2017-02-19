package pl.edu.agh.iisg.rozprochy.gra;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.iisg.rozprochy.gra.exceptions.FieldTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public class GameImpl implements IGame {
	private Map<String, String> players;// gracz na X albo Y
	private Map<String, IListener> listeners;// gracz na X albo Y
	private String currentPlayer;
	private String board[][] = new String[3][3];
	private BotGame boot;
	private boolean isGameOver = false;

	public GameImpl() {
		players = new HashMap<String, String>();
		listeners = new HashMap<String, IListener>();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = "";
			}
		}
	}

	public synchronized void setBotForPlayer() throws RemoteException {
		this.boot = new BotGame("Marek", this);
		this.boot.start();
	}

	private synchronized boolean checkForGameover(int x, int y, String mark) {
		x = (x > 0) ? x : 3;
		y = (y > 0) ? y : 3;
		if ((board[(x + 1) % 3][y % 3].equals(mark)) && (board[x % 3][y % 3].equals(mark))
				&& (board[(x - 1) % 3][y % 3].equals(mark)))
			return true;
		if ((board[x % 3][(y + 1) % 3].equals(mark)) && (board[x % 3][y % 3].equals(mark))
				&& (board[x % 3][(y - 1) % 3].equals(mark)))
			return true;
		if ((board[(x + 1) % 3][(y + 1) % 3].equals(mark)) && (board[x % 3][y % 3].equals(mark))
				&& (board[(x - 1) % 3][(y - 1) % 3].equals(mark)))
			return true;
		int freespaces = 9;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (this.board[i][j].length() > 0)
					freespaces = freespaces - 1;
			}
		}

		return false;
	}

	private synchronized boolean checkForGameoverWithNoWinner(int x, int y, String mark) {
		int freespaces = 9;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (this.board[i][j].length() > 0)
					freespaces = freespaces - 1;
			}
		}
		if (freespaces == 0)
			return true;
		return false;
	}

	public synchronized String getNextPlayer() {
		for (String key : listeners.keySet()) {
			if (!key.equals(currentPlayer)) {
				return key;
			}
		}
		return null;
	}

	public synchronized void activePlayerSetAndNotify(String name) throws RemoteException {
		this.currentPlayer = name;
		System.out.println("Nowy aktywny player: " + name);
		for (String key : listeners.keySet()) {
			listeners.get(key).currentActivePlayer(currentPlayer);
		}
	}

	public synchronized void notifyPlayersOnMove(int x, int y) {
		for (String key : listeners.keySet()) {
			try {
				listeners.get(key).playerMove(x, y, board[x][y]);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void notifyPlayersOnLogout(String out) {
		for (String key : listeners.keySet()) {
			try {
				listeners.get(key).playerDisConnected(out);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void notifyPlayersOnGameOver(String winner) {
		for (String key : listeners.keySet()) {
			try {
				listeners.get(key).gameOver(winner);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void notifyPlayersOnGameOverWithNoWinner() {
		for (String key : listeners.keySet()) {
			try {
				listeners.get(key).gameOverWithNoWinner();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void markField(int x, int y, String name) throws RemoteException, FieldTakenException {
		if (!isGameOver) {
			System.out.println(x + " " + y + ", " + name);
			if (board[x][y].length() > 0) {
				throw new FieldTakenException();
			}

			board[x][y] = players.get(name).toLowerCase();
			notifyPlayersOnMove(x, y);
			activePlayerSetAndNotify(getNextPlayer());
			if (checkForGameover(x, y, players.get(name).toLowerCase())) {
				this.isGameOver = true;
				notifyPlayersOnGameOver(name);
			}
			if (checkForGameoverWithNoWinner(x, y, players.get(name).toLowerCase())) {
				this.isGameOver = true;
				notifyPlayersOnGameOverWithNoWinner();
			}
		}
	}

	@Override
	public synchronized void logout(String name) throws RemoteException {
		players.remove(name);
		listeners.remove(name);

		for (String key : players.keySet()) {
			listeners.get(key).playerDisConnected(name);
		}
	}

	@Override
	public synchronized Map<String, String> login(String name, IListener listener, boolean withBot)
			throws RemoteException, LoginTakenException, TooManyPlayersException {
		if (players.containsKey(name)) {
			throw new LoginTakenException();
		}
		if (players.size() == 2) {
			throw new TooManyPlayersException();
		}

		System.out.println(name + " chce sie zalogowac");

		String mark = null;
		switch (players.size()) {
		case 0:
			mark = "X";
			currentPlayer = name;
			break;
		case 1:
			mark = "O";
			break;
		}
		System.out.println("Dodaje gracza " + name + " jako " + mark);
		System.out.println(listener);
		players.put(name, mark);
		listeners.put(name, listener);
		listener.loggedIn(mark);

		for (String key : listeners.keySet()) {
			try {
				if (!key.equals(name)) {
					listeners.get(key).secondPlayerConnected(name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		activePlayerSetAndNotify(currentPlayer);

		System.out.println("dodac bota?");
		if (withBot) {
			System.out.println("dodaje bota");
			setBotForPlayer();
		}

		for (String key : players.keySet()) {
			System.out.println("printuje:");
			System.out.println(key);
		}

		return players;
	}
}
