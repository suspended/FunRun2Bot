package model;

import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class that is responsible for monitoring
 * communication between the client and the
 * server. It is also responsible for acting
 * on incoming messages if necessary.
 * 
 * @author Damiene Stewart
 */
public class GameLobbyMonitor extends Monitor {
	
	/**
	 * The custom game's token.
	 */
	private int myToken;
	
	/**
	 * The time that this object was instantiated.
	 */
	private long myBaseTime;
	
	/**
	 * The incoming time from the server. Used to synchronize
	 * the time between the client and server to make movement
	 * smooth.
	 */
	private long myServerTime;
	
	private boolean myQuickRaceFlag;
	
	/**
	 * Construct a new Game Lobby Monitor.
	 * @param theClient the client object.
	 * @param theIP the IP address of the game server program.
	 * @param theToken the game's token.
	 */
	public GameLobbyMonitor(Client theClient, String theIP, int theToken) {
		super(theClient, theIP, Config.GAME_SERVER_PORT);
		myToken = theToken;
		myServerTime = 0;
		myBaseTime = System.currentTimeMillis();
		myQuickRaceFlag = true;
	}

	@Override
	protected void processSocketData(Object theData, PrintWriter theWriter) {
		JSONArray data = (JSONArray) theData;
		String messageType = data.getString(0);
		
		switch (messageType) {
		
		case "5":
			setSpeed(theWriter);
			break;
			
		case "7":
			endRace();
			rejoin(theWriter);
			break;
		
		case "13":
			die(data, theWriter);
			break;
				
		case "16":
			sendPong(data.getInt(1), theWriter);
			break;
		
		case "17":
			setServerTime(data.getLong(1));
			break;
			
		case "18":
			corrigatePosition(data, theWriter);
			break;
			
		case "28":
			endRace();
			break;
			
		case "30":
			setQuickRaceFlag(data.getInt(1));
			break;
			
		case "35":
			if (myQuickRaceFlag) {
				endRace();
			}
			break;
		
		case "39":
			joinCustomGame(data.getString(1), theWriter);
			break;
		
		default:
			System.out.println(data);
		}
	}
	
	private void setQuickRaceFlag(int theBool) {
		// TODO Auto-generated method stub
		if (theBool == 1) {
			myQuickRaceFlag = true;
			endRace();
		} else if (theBool == 0) {
			myQuickRaceFlag = false;
		}
	}

	/**
	 * This method is called when the bot should die.
	 * This happens when the player dies from certain things,
	 * like rockets, saws, etc... because the bot is too close.
	 * @param theData the data having the death related information.
	 * @param theWriter the socket writer to send the response with.
	 */
	private void die(JSONArray theData, PrintWriter theWriter) {
		if (theData.getInt(1) == 1) {
			int powerUp = theData.getInt(4);
			int hitType = theData.getInt(6);
			int killer = theData.getInt(3);
			long respawnTime = (theData.getInt(2) + getServerTime());
			
			JSONArray arr = new JSONArray()
							.put(9)
							.put(respawnTime)
							.put(killer)
							.put(powerUp)
							.put(0)
							.put(hitType);
			
			theWriter.println(arr.toString());		
		}
	}
	
	/**
	 * Send the server information about the bot's current location.
	 * This is done based on information received from the server
	 * about the player's location.
	 * @param data the incoming data to assess.
	 * @param theWriter the writer for the socket.
	 */
	private void corrigatePosition(JSONArray data, PrintWriter theWriter) {
		if (data.getInt(1) == 1) {
			echoMovement(data.getInt(2), data.getInt(3), data.getInt(4),
				data.getInt(5), theWriter);
		}
	}
	
	private long getServerTime() {
		return myServerTime + (System.currentTimeMillis() - myBaseTime);
	}
	
	/**
	 * Tell the server where the bot is.
	 * @param xPos bot's x-axis position.
	 * @param yPos bot's y-axis position.
	 * @param xV bot's x-axis speed.
	 * @param yV bot's y-axis speed.
	 * @param theWriter the writer for the socket.
	 */
	private void echoMovement(int xPos, int yPos, int xV, int yV, PrintWriter theWriter) {
		JSONArray ar = new JSONArray()
						.put(3)
						.put(getServerTime())
						.put(xPos)
						.put(yPos)
						.put(xV)
						.put(yV);

		theWriter.println(ar.toString());
	}

	/**
	 * Rejoin the custom game.
	 * @param theWriter the socket's writer.
	 */
	private void rejoin(PrintWriter theWriter) {
		// TODO this doesn't work.
		JSONArray ar = new JSONArray()
						.put(16);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		theWriter.println(ar.toString());
	}

	/**
	 * End the race by stopping the monitor.
	 */
	private void endRace() {
		myClient.toggleLobbyStatus();
		myClient.setStatus(1);
		stopMonitor();
	}

	/**
	 * Reply to count down messages from the server.
	 * @param theWriter the socket's writer.
	 */
	private void setSpeed(PrintWriter theWriter) {
		JSONArray ar = new JSONArray()
						.put(19);
		
		for (int i = 0; i < 4; i++) {
			try {
				Thread.sleep(1000);
				theWriter.println(ar.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the server time.
	 * @param theServerTime incoming server time.
	 */
	private void setServerTime(long theServerTime) {
		myServerTime = theServerTime / 1000000;
	}
	
	/**
	 * Join a custom game by sending the appropriate data.
	 * @param theToken
	 * @param theWriter
	 */
	private void joinCustomGame(String theToken, PrintWriter theWriter) {
		JSONObject data = new JSONObject()
							.put("a", theToken)
							.put("p", myClient.getID())
							.put("m", 5)
							.put("t", 4)
							.put("g", myToken);

		theWriter.println(data.toString());
	}
	
	/**
	 * Reply to ping from the server.
	 * @param data the ping data.
	 * @param theWriter the socket's writer.
	 */
	private void sendPong(int data, PrintWriter theWriter) {
		JSONArray ar = new JSONArray()
						.put(4)
						.put(data);
		theWriter.println(ar.toString());
	}
}
