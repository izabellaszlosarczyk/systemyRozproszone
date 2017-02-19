package pl.edu.agh.iisg.rozprochy;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import pl.edu.agh.iisg.rozprochy.controller.GameController;
import pl.edu.agh.iisg.rozprochy.controller.MainController;
import pl.edu.agh.iisg.rozprochy.gra.IGame;
import pl.edu.agh.iisg.rozprochy.gra.IListener;

public class Main extends Application {

	private MainController l;
	private Stage primaryStage;

	public void start(Stage primaryStage) {
		try {
			Properties prop = new Properties();
			FileInputStream input = new FileInputStream("config.properties");
			prop.load(input);
			String registryName = prop.getProperty("rmiRegistry");
			String gameRemoteName = prop.getProperty("gameRemoteName");
			String port = prop.getProperty("port");
			String url = registryName + ":" + port + "/" + gameRemoteName;

			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("app");

			Registry registry = LocateRegistry.getRegistry();
			Object o = Naming.lookup(url);
			System.out.println(o.getClass().getName());

			this.l = new MainController(primaryStage, (IGame) o);
			this.l.initRootLayout();
			
			FXMLLoader fxmlLoaderGame = new FXMLLoader();
			fxmlLoaderGame.setLocation(getClass().getResource("/pl/edu/agh/iisg/rozprochy/view/GameView.fxml"));

			IListener listener = (IListener) UnicastRemoteObject.exportObject(this.l, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		launch(args);
	}
}
