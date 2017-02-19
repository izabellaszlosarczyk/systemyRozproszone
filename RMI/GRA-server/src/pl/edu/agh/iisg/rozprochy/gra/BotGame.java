package pl.edu.agh.iisg.rozprochy.gra;

import java.rmi.RemoteException;
import java.util.Random;

import pl.edu.agh.iisg.rozprochy.gra.exceptions.FieldTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public class BotGame extends Thread implements IListener {

	private String name;
	private IGame game;
	private String mark;
	private String board[][];
	private int free[] = new int[9];
	
	public BotGame(String name, IGame game) {
		this.name = name;
		this.game = game;
		this.board = new String[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				this.board[i][j] = "";
			}
		}
		for (int k = 0; k < 9; k = k + 1){
			free[k] = 1;
		}
		
	}
	
	public void lag() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
    public void run(){
    	lag();
    	
		//logujemy
		try {
			System.out.println("Dodaje bota o imieniu: " + this.name);
			this.game.login(this.name, this, false);
		} catch (RemoteException | LoginTakenException | TooManyPlayersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void loggedIn(String mark) throws RemoteException {
		this.mark = mark;
	}

	@Override
	public void secondPlayerConnected(String name) throws RemoteException {
		// nic nie robimy
	}

	@Override
	public void gameOver(String winner) throws RemoteException {
		// nic nie robimy
	}

	@Override
	public void playerMove(int x, int y, String value) throws RemoteException {
		System.out.println("BOT: X: " + x + " Y: " + y + " value: " + value);
		this.board[x][y] = value;
	}

	@Override
	public void currentActivePlayer(String name) throws RemoteException {
		// TODO Auto-generated method stub
		if(this.name.equals(name)) {
			//nasz ruch
			int button = 0, flaga = 0, x = button%3,  y = Math.floorDiv(button, 3);
			//for (int tmp = 0; tmp < 3; tmp = tmp + 1)lag();
			while (!board[x][y].isEmpty() || (flaga == 0)){
				button = (button + 1)%9;
				x = button%3;
				y = Math.floorDiv(button, 3);
				lag();
				if (board[x][y].isEmpty()== true){
					flaga = 1;
					
				}
			}
			System.out.println("BOT: x " + x + " y " + y + " value: " + board[x][y]);
			if (flaga == 1){
				//wykonaj ruch
				try {
					this.game.markField(x, y, this.name);
					this.board[x][y] = this.mark;
				} catch (FieldTakenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	@Override
	public void gameOverWithNoWinner() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDisConnected(String out) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
