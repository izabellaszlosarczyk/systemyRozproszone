package pl.edu.agh.iisg.rozprochy;

import java.rmi.RemoteException;

import pl.edu.agh.iisg.rozprochy.controller.GameController;
import pl.edu.agh.iisg.rozprochy.controller.LoginController;
import pl.edu.agh.iisg.rozprochy.gra.IGame;
import pl.edu.agh.iisg.rozprochy.gra.IListener;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public class SomeLogic {
	public static int isOver(int table[][], int x, int y){
		x = (x > 0)? x : 3;
		y = (y > 0)? y : 3;
		if ((table[(x+1)%3][y%3] == 1)&& (table[x%3][y%3] == 1) && (table[(x-1)%3][y%3] == 1) )return 1;
		if ((table[x%3][(y+1)%3] == 1)&& (table[x%3][y%3] == 1) && (table[x%3][(y-1)%3] == 1) )return 1;
		if ((table[(x+1)%3][(y+1)%3] == 1)&& (table[x%3][y%3] == 1) && (table[(x-1)%3][(y-1)%3] == 1) )return 1;
		return 0;
	}
}
