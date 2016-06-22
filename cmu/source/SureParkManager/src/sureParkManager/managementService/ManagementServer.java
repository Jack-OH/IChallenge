package sureParkManager.managementService;

import java.net.*;

import sureParkManager.controlService.ControlService;
import sureParkManager.controlService.IControlService;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManagementServer extends Thread {
	// Definitions
	public static final int kNoError        = 0;
    public static final int kNoClientSocket = -8000;
    public static final int kNoConnection   = -8001;

	// Attributes
	private static ManagementServer instance = null;
	private static ServerSocket serverSocket = null;
	private final int portNum = 3333;

    private static ManagementServer mgtServer;

    /**
     * This executor service has 10 threads.
     * So it means your server can process max 10 concurrent requests.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private IControlService ctlService = null;

	public ManagementServer() {
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

	public void setControlService(IControlService ctlService) {
		this.ctlService = ctlService;
	}

	public IControlService getControlService() {
		return this.ctlService;
	}

	public void run() {
        // TODO Auto-generated constructor stub
        /*****************************************************************************
         * First we instantiate the server socket. The ServerSocket class also
         * does the listen()on the specified port.
         *****************************************************************************/
        try {
            System.out.println("\n\nStarting Server");
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.err.println("\n\nCould not instantiate socket on port: " + portNum + " " + e);
            System.exit(1);

        }

        while (true) {
            try {
                System.out.println("\n\nWaiting for connection on port " + portNum + ".");
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ManagementComm(this.ctlService, clientSocket));
            } catch (Exception e) {
                System.err.println("Accept failed.");
            }
        }
    }

    //Call the method when you want to stop your server
    private void stopServer() {
        //Stop the executor service.
        executorService.shutdownNow();
        try {
            //Stop accepting requests.
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error in server shutdown");
            e.printStackTrace();
        }
        System.exit(0);
    }
} // class