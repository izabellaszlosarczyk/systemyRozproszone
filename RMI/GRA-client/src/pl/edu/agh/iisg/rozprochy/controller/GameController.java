package pl.edu.agh.iisg.rozprochy.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pl.edu.agh.iisg.rozprochy.SomeLogic;
import pl.edu.agh.iisg.rozprochy.controller.MainController.GameLogOutAction;
import pl.edu.agh.iisg.rozprochy.controller.MainController.GameMoveAction;
import pl.edu.agh.iisg.rozprochy.controller.MainController.LoginAction;

public class GameController {

	private Stage stage;
	private String your = "a";
	private String login;
	private String port;
	private StringProperty competitorLogin = new SimpleStringProperty();
	private int tmpTable[][] = new int[3][3];

	@FXML
	private Button b1;
	@FXML
	private Button b2;
	@FXML
	private Button b3;
	@FXML
	private Button b4;
	@FXML
	private Button b5;
	@FXML
	private Button b6;
	@FXML
	private Button b7;
	@FXML
	private Button b8;
	@FXML
	private Button b9;

	@FXML
	private Button cancelButton;
	@FXML
	private Button refreshButton;
	@FXML
	private Button readyButton;
	@FXML
	private Button undoButton;

	@FXML
	private Text yourChar;
	@FXML
	private Text competitorName;

	@FXML
	private Text competitorChar1;
	@FXML
	private Text winner;

	private List<Button> buttons;
	private GameMoveAction gameAction;
	private GameLogOutAction logOutAction;

	@FXML
	private void initialize() {
		buttons = new ArrayList<Button>();
		buttons.add(b1);
		buttons.add(b2);
		buttons.add(b3);
		buttons.add(b4);
		buttons.add(b5);
		buttons.add(b6);
		buttons.add(b7);
		buttons.add(b8);
		buttons.add(b9);
		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);

			button.setText("");
			button.setStyle("-fx-font: 45 arial; -fx-base: #D9B88F;");
			button.setVisible(true);

			final int index = i;
			competitorName.textProperty().bindBidirectional(competitorLogin);

			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					button.setText(GameController.this.your);
					if ((GameController.this.your).toLowerCase().contains("x")) {
						button.setStyle("-fx-font: 45 arial; -fx-base: #62BF1E;");
					} else {
						button.setStyle("-fx-font: 45 arial; -fx-base: #BF3731;");
					}
					int x = index % 3;
					int y = Math.floorDiv(index, 3);
					GameController.this.tmpTable[x][y] = 1;
					// checkResult(0, 0);

					gameAction.action(x, y);
				}
			});
		}

		String tmp = "(" + this.login + ")" + " twój znak to: " + this.your;
		String tmp3 = "";
		if ((this.your).toLowerCase().contains("x")) {
			tmp3 = "O";
		} else {
			tmp3 = "X";
		}
		String tmp2 = " znak przeciwnika: " + tmp3;
		yourChar.setText(tmp);
		competitorChar1.setText(tmp2);
		readyButton.setText("ready");
		readyButton.setStyle("-fx-base: #BF3731;");
		this.winner.setVisible(false);

	}

	@FXML
	private void handleCancelAction(ActionEvent event) {
		this.logOutAction.action("logout");
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleRefreshAction(ActionEvent event) {
		initialize();
	}

	@FXML
	private void handleUndoAction(ActionEvent event) {
		// initialize();
	}

	@FXML
	private void handleReadyAction(ActionEvent event) {
		// initialize();
		readyButton.setStyle("-fx-base: #62BF1E;");
		readyButton.setText(this.competitorLogin.get() + " twoj ruch");
	}

	public void setDialogStage(Stage stageEdit) {
		this.stage = stageEdit;
	}

	public void buttonSetX(Button b, String znak) {
		b.setText(znak);
	}

	public void buttonSet0(Button b, String znak) {
		b.setText(znak);
	}

	public void setData(String znak, String login, String port) {
		this.your = znak;
		System.out.println("no i co mamy:" + login);
		this.login = login;
		this.port = port;
		this.competitorLogin.setValue("---");
		initialize();
	}

	public String getPort() {
		return this.port;
	}

	public String getLogin() {
		return this.login;
	}

	public String getYourChar() {
		return this.your;
	}

	public void setYourChar(String c) {
		this.your = c;
	}

	public String getCompetitorLogin() {
		return competitorLogin.getValue();
	}

	public void setCompetitorLogin(String competitorLogin) {
		this.competitorLogin.setValue(competitorLogin);
	}

	public void setCompetitorLoginTextField(String competitorLogin) {
		this.competitorName.setText(competitorLogin);
	}

	public void checkResult(int x, int y) {
		// if (SomeLogic.isOver(this.tmpTable, x, y) == 1)
		// finish();
	}

	public void finish(boolean isWinner) {
		int k = 0;
		for (int i = 0; i < 10000; i = i + 1) {
			k = k * 2;
		}
		if (isWinner) {
			this.winner.setText("Wygrana gracza " + this.login);
		} else {
			this.winner.setText("Przegrana gracza " + this.login);
		}
		this.winner.setVisible(true);

		for (Button button : buttons) {
			button.setVisible(false);
		}
		undoButton.setVisible(false);
		readyButton.setVisible(true);
	}

	public void setActive(boolean isActive) {
		System.out.println(isActive);
		for (Button button : buttons) {
			button.setDisable(!isActive);
		}
	}

	public void finishWithNoWinner() {
		int k = 0;
		for (int i = 0; i < 10000; i = i + 1) {
			k = k * 2;
		}
		this.winner.setText("REEEEEEEEEEMIS");

		this.winner.setVisible(true);

		for (Button button : buttons) {
			button.setVisible(false);
		}
		undoButton.setVisible(false);
		readyButton.setVisible(true);
	}

	public void setMoveAction(GameMoveAction gameAction) {
		this.gameAction = gameAction;
	}

	public void setValue(int x, int y, String mark) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Button button = GameController.this.buttons.get(y * 3 + x);
				button.setText(mark.toLowerCase());
				if ("x".equals(mark.toLowerCase())) {
					button.setStyle("-fx-font: 45 arial; -fx-base: #62BF1E;");
				} else {
					button.setStyle("-fx-font: 45 arial; -fx-base: #BF3731;");
				}
			}
		});
	}

	public void secondPlayerDisconnected() {
		this.winner.setText("Drugi gracz odłączył się!");
		this.winner.setVisible(true);

		for (Button button : buttons) {
			button.setVisible(false);
		}
		undoButton.setVisible(false);
		readyButton.setVisible(true);

	}

	public void setLogoutAction(GameLogOutAction logOut) {
		this.logOutAction = logOut;
	}
}
