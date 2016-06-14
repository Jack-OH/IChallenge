package sureParkManager.managementService;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.*;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ServerSender {
    ManagementServer mgtServer = null;
    Socket clientSocket = null;

    public ServerSender() {
        mgtServer = ManagementServer.getInstance();
        clientSocket = mgtServer.getClientSocket();
    }

    public int write(String str) throws Exception {
        String outStr = str;

        if (clientSocket == null) return ManagementServer.kNoClientSocket;

        if (clientSocket.isConnected()) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            System.out.println("Sending message to client...." + outStr);
            out.write(outStr);
            out.flush();
            return ManagementServer.kNoError;
        } else {
            System.out.println("Can not sending message to client, no client....");
            return ManagementServer.kNoConnection;
        }
    } // write
}
