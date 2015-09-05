package view;

import java.io.File;

import controller.Controller;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.Friend;

public class EchoBotGUI extends Application {
	
	private Controller myController;
	
	private Stage myStage;
	
	private Scene myLoginScene;
	
	private Scene myMainScene;
	
	private BooleanProperty myShowLoadingImage;
	
	public EchoBotGUI() {
		myController = new Controller(this);
		myShowLoadingImage = new SimpleBooleanProperty(false);
	}
	
	private void setup() {
		createLoginScene();
		
		myStage.setTitle("EchoBot");
		myStage.setScene(myLoginScene);
		myStage.setOnCloseRequest(event -> {
			shutdownStage();
		});
		myStage.show();
	}

	private void createLoginScene() {
		BorderPane root = new BorderPane();
		Scene loginScene = new Scene(root);
		addElementsToLoginScene(root);
		myLoginScene = loginScene;
	}

	private void addElementsToLoginScene(BorderPane root) {
		// Create grid.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		ColumnConstraints colOne = new ColumnConstraints(100);
		ColumnConstraints colTwo = new ColumnConstraints(100, 200, 300);
		colTwo.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(colOne, colTwo);
		
		// Create labels.
		Label emailLabel = new Label("Bot's Email:");
		Label passwordLabel = new Label("Bot's Password:");
		
		// Create fields.
		TextField emailField = new TextField();
		emailField.setPromptText("The bot's email address.");
		
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("The bot's password.");
		
		// Create login loading image.
		Image loadingImage = new Image((new File("images/loading.gif")).toURI().toString());
		ImageView loadingImageView = new ImageView(loadingImage);
		loadingImageView.visibleProperty().bind(myShowLoadingImage);
		
		// Create login button.
		Button loginButton = new Button("Login");
		loginButton.setOnAction(event -> {
			login(emailField.getText(), passwordField.getText());
		}); 
		
		// Adjust alignment of elements.
		GridPane.setHalignment(emailLabel, HPos.LEFT);
		GridPane.setHalignment(passwordLabel, HPos.LEFT);
		GridPane.setHalignment(emailField, HPos.RIGHT);
		GridPane.setHalignment(passwordField, HPos.RIGHT);
		GridPane.setHalignment(loadingImageView, HPos.CENTER);
		GridPane.setHalignment(loginButton, HPos.RIGHT);
		
		// Add elements to grid.
		grid.add(emailLabel, 0, 0);
		grid.add(passwordLabel, 0, 1);
		grid.add(emailField, 1, 0);
		grid.add(passwordField, 1, 1);
		grid.add(loadingImageView, 1, 2);
		grid.add(loginButton, 1, 2);
		
		root.setCenter(grid);
	}

	private void createMainScene() {
		BorderPane root = new BorderPane();
		Scene mainScene = new Scene(root);
		addElementsToMainScene(root);
		myMainScene = mainScene;
	}
	
	private void addElementsToMainScene(BorderPane root) {
		// Create grid.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		// Create labels.
		Label acceptRequestsFrom = new Label("Accepting requests from:");
		
		// Create list.
		ObservableList<Friend> potentialRequesters = FXCollections.observableArrayList(myController.getFriendList());
		
		// Create list view.
		ComboBox<Friend> potentialRequestersView = new ComboBox<Friend>(potentialRequesters);
		
		// Create set button.
		Button goButton = new Button("Go");
		goButton.setOnAction(event -> {
			Friend f = potentialRequestersView.getValue();
			if (f != null) {
				myController.setFriend(f);
				Alert info = new Alert(AlertType.INFORMATION);
				info.setHeaderText(null);
				info.setContentText("Bot will accept requests from " + f.getUserName() + ".");
				info.show();
			}
		});
		
		// Set alignment.
		GridPane.setHalignment(acceptRequestsFrom, HPos.LEFT);
		GridPane.setHalignment(potentialRequestersView, HPos.RIGHT);
		GridPane.setHalignment(goButton, HPos.RIGHT);
		
		// Add elements.
		grid.add(acceptRequestsFrom, 0, 0);
		grid.add(potentialRequestersView, 1, 0);
		grid.add(goButton, 1, 1);
		
		root.setCenter(grid);
	}
	
	@Override
	public void start(Stage thePrimaryStage) throws Exception {
		myStage = thePrimaryStage;
		setup();
	}
	
	public void login(String theEmail, String thePassword) {
		if (!theEmail.isEmpty() && !thePassword.isEmpty()) {
			setShowLoadingImage(true);
			myController.login(theEmail, thePassword);
		} else {
			Alert emptyValues = new Alert(AlertType.ERROR);
			emptyValues.setHeaderText(null);
			emptyValues.setContentText("Please enter both an email and a password for your bot.");
			emptyValues.showAndWait();
		}
	}
	
	public void setShowLoadingImage(boolean theBooelan) {
		myShowLoadingImage.set(theBooelan);
	}
	
	public void showMainScene() {
		createMainScene();
		
		myStage.setScene(myMainScene);
		myStage.sizeToScene();
		
		myLoginScene = null;
	}
	
	public void showLoginAlert(String theMessage) {
		setShowLoadingImage(false);
		Alert loginAlert = new Alert(AlertType.ERROR);
		loginAlert.setHeaderText(null);
		loginAlert.setContentText(theMessage);
		loginAlert.showAndWait();
	}
	
	public void shutdownStage() {
		myController.shutdown();
	}
	
	public static void main(String... theArgs) {
		launch();
	}
}
