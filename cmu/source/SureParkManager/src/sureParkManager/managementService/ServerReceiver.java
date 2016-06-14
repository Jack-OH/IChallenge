package sureParkManager.managementService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sureParkManager.controlService.ControlService;

import java.io.*;
import java.net.*;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ServerReceiver extends Thread {
    ManagementServer mgtServer = null;
    Socket clientSocket = null;
    ControlService ctlService = null;
    ServerSender commTx = null;

    public ServerReceiver(ControlService ctlService) throws Exception {
        // TODO Auto-generated constructor stub
        mgtServer = ManagementServer.getInstance();
        this.ctlService = ctlService;
        commTx = new ServerSender();
    }

    public void run() {
        try {
            try {
                mgtServer.setClientSocket(mgtServer.getServerSocket().accept());
                clientSocket = mgtServer.getClientSocket();
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
            ManagementDBTransaction mgtDBtr = ManagementDBTransaction.getInstance();

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
                            JSONObject retJsonObj = new JSONObject();
                            subJsonObject = (JSONObject) jsonObject.get("newGarage");

                            if(subJsonObject != null) {
                                mgtDBtr.addNewGarage(subJsonObject.toJSONString());
                                retJsonObj.put("newGarage", "OK");
                            }

                            subJsonObject = (JSONObject) jsonObject.get("newReservation");
                            if(subJsonObject != null) {
                                mgtDBtr.addNewReservation(subJsonObject.toJSONString());
                                retJsonObj.put("newReservation", "OK");
                            }

                            subJsonObject = (JSONObject) jsonObject.get("newUser");
                            if(subJsonObject != null) {
                                mgtDBtr.addNewUser(subJsonObject.toJSONString());
                                retJsonObj.put("newUser", "OK");
                            }

                            subJsonObject = (JSONObject) jsonObject.get("cancelReservation");
                            if(subJsonObject != null) {
                                retJsonObj.put("cancelReservation", "OK");
                            }

                            subJsonObject = (JSONObject) jsonObject.get("parkingCar");
                            if(subJsonObject != null) {
                                // DB Update

                                // open gate
                                this.ctlService.openEntryGate();

                                retJsonObj.put("parkingCar", "OK");
                            }

                            // Send response
							String outStr = retJsonObj.toJSONString() + "\n";
							commTx.write(outStr);
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
} // class