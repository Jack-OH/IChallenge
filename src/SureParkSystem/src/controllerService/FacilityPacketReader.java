package controllerService;

import java.io.BufferedReader;
import java.io.IOException;

public class FacilityPacketReader extends Thread {
	
	private BufferedReader mIn = null;
	
	public FacilityPacketReader(BufferedReader in) {
		mIn = in;		
	}
	
	public void parse(String packet) {
		if( packet.charAt(0) != '$' ) {
			System.out.println("invalid packet. through it");
			return;
		}
		
		
	}
	
	public void run() {
		// display read message from server
		try {
			String message;
			while ((message = mIn.readLine()) != null) {
				//TODO: parse packet

				System.out.println(message);
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken");
			ioe.printStackTrace();
		}
		
	}

}
