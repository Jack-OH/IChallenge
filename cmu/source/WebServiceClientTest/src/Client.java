
/******************************************************************************************************************
* File: Client.java
* Course/Project: 2014 LG Executive Education Program Studio Project
*
* Copyright: Copyright (c) 2013 Carnegie Mellon University
* Versions:
*	1.0 Apr 2013 - IOT project
*	2.0 Apr	2014 - Smart Warehouse
*
* Description:
*
* This class serves as an example for how to write a client application that connects to an Arudino server.
* There is nothing uniquely specific to the Arduino in this code, other than the application level protocol
* that is used between this application and the Arduino. The assumption is that the Arduino ServerDemo is running
* on the Arduino. When this application is started it attempts to connect to the arduino client. Once connected
* this application writes a message to the server. The message sent is alternated between two possible messages.
* There is no particular reason for this. Once client (this app) sends the message, it reads two strings from
* the server. And the process continues ad infinitum. Note that this is intended to be a minimal demo, more
* error handling should be added, especially where reading and writing data to and from server. Read errors in
* this section of the code will cause this program to crash.
*
* Compilation and Execution Instructions:
*
*	Compiled in a command window as follows: javac Client.java
*	Execute the program from a command window as follows: java Client <Server IP Address>
*
* Parameters: 		None
*
* Internal Methods: None
*
******************************************************************************************************************/

import java.io.*;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class Client {
	public static void main(String argv[]) throws Exception {

		ClientRead clientRead = new ClientRead();

		clientRead.start();

		Thread.sleep(1000);
		clientRead.write("./src/newGarage.json");
//
//		Thread.sleep(3000);
//		clientRead.write("./src/newUser.json");
//		
//		Thread.sleep(3000);
//		clientRead.write("./src/newReservation.json");	
//		clientRead.write("./src/parkingCar.json");

		while (true) {
			Thread.sleep(3000);
//			clientRead.write("./src/newGarage.json");

		} // while
		/*****************************************************************************
		 * That's it! Close the streams and close the socket.
		 *****************************************************************************/

		// clientSocket.close();
	} // main
} // class

class ClientRead extends Thread {
	static Socket clientSocket = null; // The socket.
	BufferedReader in = null;
	BufferedWriter out = null;
	
	public ClientRead() throws Exception{
		// TODO Auto-generated constructor stub
		int portNum = 3333; // Port number for server socket
		String ipAddr = new String("localhost");
		
		try {
			clientSocket = new Socket(ipAddr, portNum);
		} catch (IOException e) {
			System.err.println("Could not connect to on port: " + portNum + "\n");
		}
		
		 in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		 out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	}

	public void run() {
		try {
			String inputLine;

			while (clientSocket.isConnected()) {
				/*****************************************************************************
				 * Now we read two messages from the server...
				 *****************************************************************************/

				try {
					System.out.println("Wait Recv");
					if ((inputLine = in.readLine()) != null) {
						System.out.println("FROM SERVER: " + inputLine);
					} 
				} catch (Exception e) {
					System.err.println("readLine failed::");
					break;
				}

				 Thread.sleep(500);
			} // while
			/*****************************************************************************
			 * That's it! Close the streams and close the socket.
			 *****************************************************************************/
			 in.close();
			 out.close();
			 clientSocket.close();

		} catch (Exception e) {
			
		}
	} // run

	public void write(String strFileLoc) throws Exception {
		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(strFileLoc));
		JSONObject jsonObject = (JSONObject) obj;
//		JSONObject newGarageJsonObject = (JSONObject) jsonObject.get("newGarage");
//		String newGarage = (String) newGarageJsonObject.get("garageName");

		String jsonObjectString = jsonObject.toJSONString() + "\n";

//		String garageName = (String) newGarageJsonObject.get("garageName");
		System.out.println(strFileLoc + "send...");

		if (clientSocket.isConnected()) {	
			out.write(jsonObjectString);
			out.flush();
			System.out.println(jsonObjectString);
	
			System.out.println("-------------------------------------------------------");
		} else {
			System.out.println("Client socket is closed...");
		}
	}
}
