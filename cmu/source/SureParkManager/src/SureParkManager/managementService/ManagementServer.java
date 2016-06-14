package sureParkManager.managementService;

import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import sureParkManager.controlService.ControlService;

import java.io.*;

public class ManagementServer {
	// Definitions
	static final int kNoError        = 0;
	static final int kNoClientSocket = -8000;
	static final int kNoConnection   = -8001;

	// Attributes
	private static ManagementServer instance = null;
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	final int portNum = 3333;

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

	public static ManagementServer getInstance() {
		if (instance == null) {
			synchronized (ManagementServer.class) {
				if (instance == null) {
					instance = new ManagementServer();
				}
			}
		}
		return instance;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket pClientSocket) {
		clientSocket = pClientSocket;
	}
} // class