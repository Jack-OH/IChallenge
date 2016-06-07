package controllerService;
import java.io.*;
import java.net.Socket;


public class FacilityController extends Thread {
	
	BufferedWriter mOut = null;
	BufferedReader mIn = null;
	Socket mClientSocket = null;	
	
	public FacilityController(String host, int port) throws Exception {
    	
		try {
			
	    	mClientSocket = new Socket(host, port);
	    	mOut = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
			mIn = new BufferedReader( new InputStreamReader( mClientSocket.getInputStream()));
			
		} finally {
			mClientSocket.close();
		}
		
	}
	
	public void closeFacilityController() throws IOException {
		mOut.close();
		mIn.close();
		mClientSocket.close();		
	}
	
	public void run() {
		try {
			// send a packet to facility
			// read cmd message
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String inputLine;
			
			while (!isInterrupted()) {
				String message = in.readLine() + '\n';
				//mOut.println(message);
				//mOut.flush();
				mOut.write( message , 0, message.length()  );
				mOut.flush();
				
	    		//if ((inputLine = mIn.readLine()) != null)
	    		//{
  	  			//	System.out.println("FROM SERVER: " + inputLine);
  	  			
	  			//}
			}
			
		} catch (IOException ioe) {
			
		}	
		
		
	}

}
