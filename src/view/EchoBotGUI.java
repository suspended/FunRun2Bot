package view;

import java.io.File;

import com.sun.glass.events.KeyEvent;

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

/**
 * GUI class for application. This is a Java FX
 * built GUI. There are bound to be quite a few
 * bugs to fix.
 * @author Damiene Stewart
 */
public class EchoBotGUI extends Application {
	
	/**
	 * A reference to the controller class that will work with the
	 * model to get things done.
	 */
	private Controller myController;
	
	/**
	 * The primary stage.
	 */
	private Stage myStage;
	
	/**
	 * The login scene. Later on this should be removed with
	 * simply the scene group/root? That way simply the
	 * roots can be swapped (according to stackoverflow.com).
	 */
	private Scene myLoginScene;
	
	/**
	 * The main, working scene for the GUI application. This
	 * is where the user will go after login.
	 */
	private Scene myMainScene;
	
	/**
	 * This property indicates whether or not the loading
	 * image should be shown.
	 */
	private BooleanProperty myShowLoadingImage;
	
	/**
	 * Create an instance of the GUI class, initializing the
	 * instance variables.
	 */
	public EchoBotGUI() {
		myController = new Controller(this);
		myShowLoadingImage = new SimpleBooleanProperty(false);
		myStage = null;
		myLoginScene = null;
		myMainScene = null;
	}
	
	/**
	 * Static start method to kick things off on the
	 * Java FX application thread.
	 * @param theArgs the command-line arguments, unused.
	 */
	public static void main(String... theArgs) {
		launch();
	}
	
	/**
	 * {@inheritDoc}
	 * This will get called from launch() in the main thread.
	 * @param thePrimaryStage the application's stage.
	 * @throws Exception - good to know!
	 */
	@Override
	public void start(Stage thePrimaryStage) throws Exception {
		myStage = thePrimaryStage;
		setup();
	}
	
	/**
	 * This function initiates the login procedure by calling
	 * the login function of the controller. It also starts
	 * displaying the loading image.
	 * @param theEmail the bot's email.
	 * @param thePassword the bot's password.
	 */
	public void login(String theEmail, String thePassword) {
		if (!theEmail.isEmpty() && !thePassword.isEmpty()) {
			showLoadingImage(true);
			myController.login(theEmail, thePassword);
		} else {
			showLoginAlert("Please enter both an email and a password for your bot.");
		}
	}
	
	/**
	 * Provides initial application setup and login scene
	 * construction.
	 */
	private void setup() {
		// Configure login scene as it will be needed as
		// soon as the application launches.
		myLoginScene = createLoginScene();
		
		// Stage setup.
		myStage.setTitle("EchoBot");
		myStage.setScene(myLoginScene);
		myStage.setOnCloseRequest(event -> {
			shutdown();
		});
		myStage.show();
	}

	/**
	 * Constructs and returns the login scene.
	 */
	private Scene createLoginScene() {
		BorderPane root = new BorderPane();
		Scene loginScene = new Scene(root);
		addElementsToLoginScene(root);
		return loginScene;
	}

	/**
	 * Continues with the construction of the login scene
	 * by adding the necessary elements. Some of this code
	 * seems really similar to that of the main scene. Maybe
	 * the main things out into separate functions?
	 * @param root the scene's root of which to add elements.
	 */
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
		
		// Set up password field.
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("The bot's password.");
		passwordField.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyEvent.VK_ENTER)) {
				login(emailField.getText(), passwordField.getText());
			}
		});
		
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

	/**
	 * Constructs and returns the main scene.
	 */
	private Scene createMainScene() {
		BorderPane root = new BorderPane();
		Scene mainScene = new Scene(root);
		addElementsToMainScene(root);
		return mainScene;
	}
	
	/**
	 * Continues with the construction of the main scene by
	 * adding the necessary elements.
	 * @param root the root of which to add elements.
	 */
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
	
	/**
	 * Sets the value of the property bound to the loading
	 * image's visible property.
	 * @param theBooelan the value of which to set the property
	 * to.
	 */
	public void showLoadingImage(boolean theBooelan) {
		myShowLoadingImage.set(theBooelan);
	}
	
	/**
	 * Removes the login scene and displays the main scene.
	 * The login scene is discarded until it is needed again.
	 */
	public void showMainScene() {
		createMainScene();
		myStage.setScene(myMainScene);
		myStage.sizeToScene();
		
		myLoginScene = null;
	}
	
	/**
	 * Show the alert if the user has neglected to enter
	 * correct details.
	 * @param theMessage the alert message to display.
	 */
	public void showLoginAlert(String theMessage) {
		showLoadingImage(false);
		Alert loginAlert = new Alert(AlertType.ERROR);
		loginAlert.setHeaderText(null);
		loginAlert.setContentText(theMessage);
		loginAlert.showAndWait();
	}
	
	/**
	 * Initiate shutdown sequence.
	 */
	public void shutdown() {
		myController.shutdown();
	}

}
