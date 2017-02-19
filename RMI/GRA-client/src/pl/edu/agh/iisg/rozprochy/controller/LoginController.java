package pl.edu.agh.iisg.rozprochy.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.edu.agh.iisg.rozprochy.SomeLogic;
import pl.edu.agh.iisg.rozprochy.controller.MainController.ChooseTypeAction;
import pl.edu.agh.iisg.rozprochy.controller.MainController.LoginAction;
import pl.edu.agh.iisg.rozprochy.gra.IGame;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.LoginTakenException;
import pl.edu.agh.iisg.rozprochy.gra.exceptions.TooManyPlayersException;

public class LoginController {

	private Stage primaryStage;
	@FXML
	private Button cancelButton;
	@FXML
	private Button okeyButton;

	@FXML
	private TextField login;

	@FXML
	private Label errorLogin;
	private LoginAction loginAction;
	@FXML
	private RadioButton botButton;
	private boolean f;
	private ChooseTypeAction typeAction;
	private ChooseTypeAction botAction;
	
	public LoginController() {

	}

	public LoginController(Stage primaryStage) {
		this.primaryStage = primaryStage;

	}

	public boolean isValid() {
		int flaga = 0;
		if (login.getText().isEmpty()) {
			flaga = 1;
		} else {
			this.errorLogin.setVisible(false);
		}
		if (flaga == 1) {
			return false;
		}
		return true;
	}

	public void notValid() {
		if (login.getText().isEmpty()) {
			this.errorLogin.setTextFill(Color.RED);
			this.errorLogin.setText("Ustaw login!");
			this.errorLogin.setVisible(true);
		}

	}

	public void notValidLogin() {
		this.errorLogin.setTextFill(Color.RED);
		this.errorLogin.setText("Login zajęty");
		this.errorLogin.setVisible(true);
	}

	public void tooManyPlayers() {
		this.errorLogin.setTextFill(Color.RED);
		this.errorLogin.setText("Za dużo graczy!");
		this.errorLogin.setVisible(true);
	}

	@FXML
	private void initialize() {
		errorLogin.setVisible(false);
	}

	@FXML
	private void handleOkeyAction(ActionEvent event) {
		if (isValid()) {
			//TODO send to game
			loginAction.action(this.login.getText());
		} else {
			notValid();
		}

		System.out.println("Refreshing...");
	}

	@FXML
	private void handleCanelAction(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	public void onLogin(LoginAction loginAction) {
		this.loginAction = loginAction;
	}
	
	@FXML 
	private void handleRadioButton(ActionEvent event) {
		botAction.action(botButton.isSelected());
	}

	public void onGameWithBot(ChooseTypeAction botAction) {
		this.botAction = botAction;
	}

}
