package SureParkManager.managementService;

import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class ManagementServer extends Thread {
	static private ServerSocket serverSocket = null;
	static private Socket clientSocket = null;
	static final int portNum = 3333;

	public ManagementServer() {
		// TODO Auto-generated constructor stub
		/*****************************************************************************
		 * First we instantiate the server socket. The ServerSocket class also
		 * does the listen()on the specified port.
		 *****************************************************************************/
		try {
			serverSocket = new ServerSocket(portNum);
			System.out.println("\n\nWaiting for connection on port " + portNum + ".");
		} catch (IOException e) {
			System.err.println("\n\nCould not instantiate socket on port: " + portNum + " " + e);
			System.exit(1);
		}
	}

	public void run() {
		try {
			try {
				clientSocket = serverSocket.accept();
			} catch (Exception e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}

			/*****************************************************************************
			 * At this point we are all connected and we need to create the
			 * streams so we can read and write.
			 *****************************************************************************/
			System.out.println("Connection successful");

			/*****************************************************************************
			 * If we get to this point, a client has connected. Now we need to
			 * instantiate a client socket. Once its instantiated, then we
			 * accept the connection.
			 *****************************************************************************/

			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DBtransactionManager db = new DBtransactionManager();

			String inputLine; // Data from client
			try {
				while (clientSocket.isConnected()) {
					System.out.println("Waiting for input.....");

					try {
						while ((inputLine = in.readLine()) != null) {
							System.out.println("readLine start::");
							JSONParser parser = new JSONParser();
							Object obj = parser.parse(inputLine);
							JSONObject jsonObject = (JSONObject) obj;
							
							System.out.println("now json parsing...");
							// json parsing...
							
							JSONObject subJsonObject;
							subJsonObject = (JSONObject) jsonObject.get("newGarage");
							if(subJsonObject != null)
								db.addNewGarage(subJsonObject.toJSONString());
							
							subJsonObject = (JSONObject) jsonObject.get("newReservation");
							if(subJsonObject != null)
								db.addNewReservation(subJsonObject.toJSONString());
							
							subJsonObject = (JSONObject) jsonObject.get("newUser");
							if(subJsonObject != null)
								db.addNewUser(subJsonObject.toJSONString());
							
							//String outStr = new String("ACK!");
							//this.write(outStr);
						} // while
					} catch (IOException e) {
						System.err.println("readLine failed::");
						break;
					}
					Thread.sleep(1000);
				} // while loop
			} catch (SocketTimeoutException e) {
				System.err.println("socket closed");
			}

			/*****************************************************************************
			 * Close up the I/O ports then close up the sockets
			 *****************************************************************************/
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// listener.notifyThatDarnedExceptionHappened(...);
		}
	} // run

	public int write(String str) throws Exception {
		String outStr = str;
		if (clientSocket.isConnected()) {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			System.out.println("Sending message to client...." + outStr);
			out.write(outStr);
			out.flush();
			return 0;
		} else {
			System.out.println("Can not sending message to client, no client....");
			return -1;
		}

	} // write
} // class