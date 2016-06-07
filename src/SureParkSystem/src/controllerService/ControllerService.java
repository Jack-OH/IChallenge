package controllerService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ControllerService {

	BufferedWriter mOut = null;
	BufferedReader mIn = null;
	Socket mClientSocket = null;
	
	FacilityPacketWriter mfWriter = null;
	FacilityPacketReader mfReader = null;

	
	public ControllerService(String host, int port) throws Exception {
    	
		try {
			mClientSocket = new Socket(host, port);
			System.out.println("connected to serer");
		} catch (IOException ioe) {
			System.err.println("cannot establish connection");
			ioe.printStackTrace();
		}
    	
    	mOut = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
		mIn = new BufferedReader( new InputStreamReader( mClientSocket.getInputStream()));
		
		mfReader = new FacilityPacketReader(mIn);
		mfReader.setDaemon(true);
		mfReader.start();
		
		mfWriter = new FacilityPacketWriter(mOut);
		mfWriter.setDaemon(true);
		mfWriter.start();
		
	}
	
	public void close() throws IOException {
		mOut.close();
		mIn.close();
		
		mClientSocket.close();		
	}	
	

}
