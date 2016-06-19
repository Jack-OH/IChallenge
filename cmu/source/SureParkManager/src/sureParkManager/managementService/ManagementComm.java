package sureParkManager.managementService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sureParkManager.common.ReservationInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.controlService.ControlService;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ManagementComm extends Thread {
    private Socket clientSocket = null;
    ControlService ctlService = null;

    BufferedReader in = null;
    BufferedWriter out = null;

    protected static Vector handlers = new Vector ();

    public ManagementComm() throws IOException {
    }

    public ManagementComm(ControlService ctlService, Socket clientSocket) throws Exception {
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

            ManagementDBTransaction mgtDBtr = ManagementDBTransaction.getInstance();

            String inputLine; // Data from client
            try {
                handlers.addElement(this);

                while(true) {
                    System.out.println("Waiting for input.....");

                    try {
                        inputLine = in.readLine();
                        if (inputLine == null)  break;

                        System.out.println("readLine start::");
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(inputLine);
                        JSONObject jsonObject = (JSONObject) obj;

                        System.out.println("now json parsing...");

                        // json parsing...
                        JSONObject subJsonObject;
                        JSONObject retJsonObj = new JSONObject();
                        subJsonObject = (JSONObject) jsonObject.get("newGarage");

                        if (subJsonObject != null) {
                            mgtDBtr.addNewGarage(subJsonObject.toJSONString());
                            retJsonObj.put("newGarage", "OK");

                            // Update Config.
                            SureParkConfig config = SureParkConfig.getInstance();
                            config.updateGarageInfo(); // update from DB

                            // Notify Garage Info updated.
                            ctlService.addFacility((int)subJsonObject.get("garageName"));
                        }

                        subJsonObject = (JSONObject) jsonObject.get("newReservation");
                        if (subJsonObject != null) {
                            mgtDBtr.addNewReservation(subJsonObject.toJSONString());
                            retJsonObj.put("newReservation", "OK");
                        }

                        subJsonObject = (JSONObject) jsonObject.get("newUser");
                        if (subJsonObject != null) {
                            mgtDBtr.addNewUser(subJsonObject.toJSONString());
                            retJsonObj.put("newUser", "OK");
                        }

                        subJsonObject = (JSONObject) jsonObject.get("cancelReservation");
                        if (subJsonObject != null) {
                            retJsonObj.put("cancelReservation", "OK");
                        }

                        subJsonObject = (JSONObject) jsonObject.get("parkingCar");
                        if (subJsonObject != null) {
                            int garageID, slot;

                            garageID = mgtDBtr.getEmptyGarageID((String)subJsonObject.get("usingGarage"));
                            if (garageID < 0) continue;

                            slot = mgtDBtr.getEmptyGarageSlotNum(garageID);
                            if (slot < 0) continue;

                            // DB Update
                            boolean ret = mgtDBtr.parkingCar(subJsonObject.toJSONString(), garageID, slot);

                            if (ret) {
                                System.out.println("Garage ID " + garageID + ", Empty slot number is " + slot);

                                // open gate
                                ctlService.openEntryGate(garageID, slot);

                                retJsonObj.put("parkingCar", "OK");
                            }
                            else
                                retJsonObj.put("parkingCar", "FAIL");
                        }

                        // Send response
                        String outStr = retJsonObj.toJSONString() + "\n";
                        System.out.println(outStr);
                        out.write(outStr);
                        out.flush();
                    } catch (IOException e) {
                        System.err.println("readLine failed::");
                        break;
                    }
                    Thread.sleep(1000);
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