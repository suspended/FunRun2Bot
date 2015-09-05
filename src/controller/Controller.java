package controller;

import java.util.List;

import model.Client;
import model.Friend;
import view.EchoBotGUI;

public class Controller {

	private EchoBotGUI myGUI;
	
	private Client myClient;
	
	public Controller(EchoBotGUI theGUI) {
		myGUI = theGUI;
		myClient = new Client(this);
	}
	
	public void login(String theEmail, String thePassword) {
		boolean successfulLogin = myClient.login(theEmail, thePassword);
		
		if (successfulLogin) {
			myClient.startDataMonitor();
		} else {
			myGUI.showLoginAlert(myClient.getLoginErrorMessage());
		}
	}
	
	public void loggedIn() {
		myGUI.showMainScene();
	}
	
	public List<Friend> getFriendList() {
		return myClient.getFriendList();
	}
	
	public void setFriend(Friend theFriend) {
		myClient.setMyFriend(theFriend);
	}

	public void shutdown() {
		myClient.shutdown();
	}
}
