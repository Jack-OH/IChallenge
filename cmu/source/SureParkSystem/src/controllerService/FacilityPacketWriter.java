package controllerService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class FacilityPacketWriter extends Thread {
	
	private BufferedWriter mOut = null;
	private int ArduinoId = 0;
	private LinkedBlockingQueue<String> mMessageQueue = new LinkedBlockingQueue();
	
	public FacilityPacketWriter(BufferedWriter out, int ArduinoId) {
		mOut = out;	
		this.ArduinoId = ArduinoId;
		
		//Config c = Config.getInstance();
		//GarageInfo info = c.getGarageInfo(ArduinoId);
	}
	
	public void sendInformation() {
		//$0001I4\n
		Config c = Config.getInstance();
		int slotNum = c.getGarageInfo(ArduinoId).slotNum;
		
		String packet = String.format("$%04dI%d\n", ArduinoId, slotNum);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void request2openEntryGate() {
		//$0001G1\n
		String packet = String.format("$%04dG1\n", ArduinoId);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void request2turnOnStallLED(int stallIndex) {
		//$0001L4\n
		String packet = String.format("$%04dL%d\n", ArduinoId, stallIndex);
		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
//	private synchronized String getNextMessageFromQueue() throws InterruptedException {
//		while(mMessageQueue.size() == 0 ) {
//			wait();
//		}
//		String message = (String) mMessageQueue.take();
//		mMessageQueue.removeElementAt(0);
//		return message;
//	}
	
	public void run() {
		try {
			// read cmd message
			//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			while (!isInterrupted()) {
				//String message = in.readLine() + '\n';
				//message = "$0001I4\n";
				//System.out.println("length=" +  message.length() );
				//String message = getNextMessageFromQueue();
				String message = (String) mMessageQueue.take();
				mOut.write(message, 0, message.length());
				mOut.flush();
			}
			
		} catch (InterruptedException ioe) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
							
	
	}

}
