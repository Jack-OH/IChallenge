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

	
	public ControllerService(String host, int port, int ArduinoId) throws Exception {
    	
		try {
			mClientSocket = new Socket(host, port);
			System.out.println("connected to serer");
		} catch (IOException ioe) {
			System.err.println("cannot establish connection");
			ioe.printStackTrace();
		}
    	
    	mOut = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
		mIn = new BufferedReader( new InputStreamReader( mClientSocket.getInputStream()));
		
		mfWriter = new FacilityPacketWriter(mOut, ArduinoId);
		mfWriter.setDaemon(true);
		mfWriter.start();
		
		mfReader = new FacilityPacketReader(mIn, ArduinoId);
		mfReader.setDaemon(true);
		mfReader.start();
		
		mfWriter.sendInformation();
		
	}
	
	public void openEntryGate() {
		mfWriter.request2openEntryGate();
	}
	
	public void turnonStallLED(int stallIndex) {
		mfWriter.request2turnOnStallLED(stallIndex);
	}
	
	public void close() throws IOException {
		mOut.close();
		mIn.close();
		
		mClientSocket.close();		
	}	
	

}
