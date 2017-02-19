package pl.edu.agh.iisg.rozprochy.controller;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.edu.agh.iisg.rozprochy.gra.IGame;
import pl.edu.agh.iisg.rozprochy.gra.IListener;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.FieldTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public class MainController implements IListener {

	private Stage primaryStage;
	private IGame game;
	private String port;
	private String login;
	private String notYourLogin = "";
	private GameController gameController;
	private boolean isActive; //czy moze sie ruszac
	private boolean withBot = false; 
	private Map<String, String> players;
	
	public MainController(Stage primaryStage, IGame game) {
		this.primaryStage = primaryStage;
		this.game = game;
		this.players = new HashMap<String, String>();
	}

	public void initRootLayout() {
		showLogin();
	}

	interface LoginAction {
		void action(String name);
	}
	
	interface ChooseTypeAction {
		void action(boolean type);
	}
	
	interface GameMoveAction {
		void action(int x, int y);
	}
	
	interface GameLogOutAction {
		void action(String name);
	}

	private void showLogin() {
		try {
			this.primaryStage.setTitle("Project");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/pl/edu/agh/iisg/rozprochy/view/LoginView.fxml"));
			BorderPane pane = loader.load();

			LoginController controller = loader.getController();
			LoginAction loginAction = (String name) -> {
				try {
					this.login = name;
					this.game.login(name, this, this.withBot);
				} catch (LoginTakenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TooManyPlayersException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			};
			ChooseTypeAction botAction = (boolean type) -> {
				try {
					this.withBot = type;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			controller.onLogin(loginAction);
			controller.onGameWithBot(botAction);
			Scene scene = new Scene(pane);
			if (!this.players.isEmpty()){
				System.out.println("mam listeeeeee yoyoyoyoyoyyoy");
				for (String key: players.keySet()){
					if (!(key.equals(this.login))){
						notYourLogin = key;
					}
				}
			}
			primaryStage.setTitle("FXML Welcome");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showGame(String mark) {
		try {
			FXMLLoader fxmlLoaderGame = new FXMLLoader();
			fxmlLoaderGame.setLocation(getClass().getResource("/pl/edu/agh/iisg/rozprochy/view/GameView.fxml"));
			Parent root1 = (Parent) fxmlLoaderGame.load();
			Stage stageEdit = new Stage();
			stageEdit.initModality(Modality.APPLICATION_MODAL);
			stageEdit.setTitle("GAME");
			stageEdit.setScene(new Scene(root1));
			
			GameMoveAction gameAction = (int x, int y) -> {
				try {
					this.game.markField(x, y, this.login);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (FieldTakenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			
			GameLogOutAction logOut = (String out) -> {
				try {
					this.game.logout(this.login);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};

			this.gameController = fxmlLoaderGame.getController();
			this.gameController.setMoveAction(gameAction);
			this.gameController.setLogoutAction(logOut);
			this.gameController.setActive(this.isActive);
			this.gameController.setData(mark, this.login, "PORT POBRANY Z LGCTRL");
			this.gameController.setDialogStage(stageEdit);
			if (!(notYourLogin.isEmpty())){
				this.gameController.setCompetitorLoginTextField(notYourLogin);
			}
			stageEdit.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showGameWithBoot(String mark) {
		try {
			FXMLLoader fxmlLoaderGame = new FXMLLoader();
			fxmlLoaderGame.setLocation(getClass().getResource("/pl/edu/agh/iisg/rozprochy/view/GameView.fxml"));
			Parent root1 = (Parent) fxmlLoaderGame.load();
			Stage stageEdit = new Stage();
			stageEdit.initModality(Modality.APPLICATION_MODAL);
			stageEdit.setTitle("GAME");
			stageEdit.setScene(new Scene(root1));
			
			GameMoveAction gameAction = (int x, int y) -> {
				try {
					this.game.markField(x, y, this.login);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (FieldTakenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};

			this.gameController = fxmlLoaderGame.getController();
			this.gameController.setMoveAction(gameAction);
			this.gameController.setActive(this.isActive);
			this.gameController.setData(mark, this.login, "PORT POBRANY Z LGCTRL");
			this.gameController.setDialogStage(stageEdit);
			stageEdit.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void loggedIn(String mark) throws RemoteException {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	if (!(MainController.this.withBot))showGame(mark);
		    	else showGameWithBoot(mark);
		    }
		});
	}
	
	@Override
	public void secondPlayerConnected(String name) throws RemoteException {
		System.out.println("Second player " + name);
		this.gameController.setCompetitorLoginTextField(name);
	}

	@Override
	public void gameOver(String name) throws RemoteException {
		// TODO Auto-generated method stub
		this.gameController.finish(this.login.equals(name));
	}
	
	@Override
	public void gameOverWithNoWinner() throws RemoteException {
		// TODO Auto-generated method stub
		this.gameController.finishWithNoWinner();
	}

	@Override
	public void playerMove(int x, int y, String mark) throws RemoteException {
		this.gameController.setValue(x, y, mark);
	}

	@Override
	public void currentActivePlayer(String name) throws RemoteException {
		System.out.println("Jaki mam login:" + this.login);
		this.isActive = this.login.equals(name);
		if(this.gameController != null) {
			this.gameController.setActive(this.isActive);
		}
		if (!(this.login.equals(name))){
			secondPlayerConnected("(" +name+ ")");
		}
	}

	@Override
	public void playerDisConnected(String out) throws RemoteException {
		this.gameController.secondPlayerDisconnected();
	}

}
