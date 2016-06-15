package sureParkManager.controlService;
import sureParkManager.common.SureParkConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;



public class FacilityPacketWriter extends Thread {
	
	private BufferedWriter mOut = null;
	private int facilityId = 0;
	private LinkedBlockingQueue<String> mMessageQueue = new LinkedBlockingQueue<String>();
	private boolean runningTread = true;
	
	public FacilityPacketWriter(BufferedWriter out, int id) {
		mOut = out;	
		facilityId = id;

	}
	
	public void stopThread() {
		runningTread = false;
	}
	
	public void sendInformation() throws Exception {
		//$0001I4\n
		SureParkConfig c = SureParkConfig.getInstance();
		int slotNum = c.getGarageInfoFromGarageID(facilityId).slotNum;
		
		String packet = String.format("$%04dI%d\n", facilityId, slotNum);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void request2openEntryGate() {
		//$0001G1\n
		String packet = String.format("$%04dG1\n", facilityId);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void request2turnOnStallLED(int stallIndex) {
		//$0001L4\n
		String packet = String.format("$%04dL%d\n", facilityId, stallIndex);
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
			
			while (runningTread && !isInterrupted()) {
				//String message = in.readLine() + '\n';
				//message = "$0001I4\n";
				//System.out.println("length=" +  message.length() );
				//String message = getNextMessageFromQueue();
				String message = (String) mMessageQueue.take();
				mOut.write(message, 0, message.length());
				mOut.flush();
			}
			
			System.out.println("FacilityPacketReader thread stopped");
			
		} catch (InterruptedException ioe) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
							
	
	}

}
