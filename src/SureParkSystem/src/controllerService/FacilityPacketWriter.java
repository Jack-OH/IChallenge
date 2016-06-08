package controllerService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FacilityPacketWriter extends Thread {
	
	private BufferedWriter mOut = null;
	
	public FacilityPacketWriter(BufferedWriter out) {
		mOut = out;		
	}
	
	public void run() {
		try {
			// read cmd message
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			while (!isInterrupted()) {
				String message = in.readLine() + '\n';
				message = "$0001I4\n";
				System.out.println("length=" +  message.length() );
				mOut.write(message, 0, message.length());
				mOut.flush();
			}
			
		} catch (IOException ioe) {
			
		}				
	
	}

}
