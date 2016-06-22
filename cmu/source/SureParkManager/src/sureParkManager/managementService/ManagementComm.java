package sureParkManager.managementService;

import sureParkManager.common.ReservationInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.controlService.ControlService;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ManagementComm extends Thread {
    private Socket clientSocket = null;
    private IControlService ctlService = null;

    private BufferedReader in = null;
    private BufferedWriter out = null;

    protected static Vector handlers = new Vector ();

    public ManagementComm() throws IOException {
    }

    public ManagementComm(IControlService ctlService, Socket clientSocket) throws Exception {
        // TODO Auto-generated constructor stub
        this.ctlService = ctlService;
        this.clientSocket = clientSocket;

        in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
    }

    public void run() {
        try {
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

            String inputLine; // Data from client
            try {
                handlers.addElement(this);

                while(true) {
                    System.out.println("Waiting for input.....");

                    try {
                        inputLine = in.readLine();
                        if (inputLine == null)  break;

                        System.out.println("readLine start::");

                        ManagementProtocol protocol = new ManagementProtocol(ctlService);

                        String outStr = protocol.parseJson(inputLine);
                        if (!outStr.isEmpty()) {
                            out.write(outStr);
                            out.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("readLine failed::");
                        break;
                    }
                    // Thread.sleep(1000);
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace ();
            } finally {
                System.err.println("ManagementComm is terminated");
                handlers.removeElement (this);
                try {
                    in.close();
                    out.close();
                    clientSocket.close ();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            // listener.notifyThatDarnedExceptionHappened(...);
        }
    } // run

    public static void broadcast(String message) {
        synchronized (handlers) {
            Enumeration e = handlers.elements ();
            while (e.hasMoreElements ()) {
                ManagementComm comm = (ManagementComm) e.nextElement ();
                try {
                    synchronized (comm.out) {
                        comm.out.write(message);
                    }
                    comm.out.flush ();
                } catch (IOException ex) {
                    comm.stop ();
                }
            }
        }
    }
} // class