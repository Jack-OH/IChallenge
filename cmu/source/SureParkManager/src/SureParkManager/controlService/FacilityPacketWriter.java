package sureParkManager.controlService;
import sureParkManager.common.SureParkConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.LinkedBlockingQueue;



public class FacilityPacketWriter extends Thread {
	
	private BufferedWriter mOut = null;
	private FacilityClientInfo mInfo = null;
	private LinkedBlockingQueue<String> mMessageQueue = new LinkedBlockingQueue<String>();
	
	public FacilityPacketWriter(FacilityClientInfo info) throws IOException {
		mInfo = info;
    	mOut = new BufferedWriter(new OutputStreamWriter(mInfo.mClientSocket.getOutputStream()));

	}
	
	
	public void sendInformation() throws Exception {
		//$0001I4\n
		SureParkConfig c = SureParkConfig.getInstance();
		int slotNum = c.getGarageInfoFromGarageID(mInfo.facilityId).slotNum;
		
		String packet = String.format("$%04dI%d\n", mInfo.facilityId, slotNum);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void request2openEntryGate() {
		//$0001E1\n
		String packet = String.format("$%04dE1\n", mInfo.facilityId);

		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void request2turnOnStallLED(int stallIndex) {
		//$0001L4\n
		String packet = String.format("$%04dL%d\n", mInfo.facilityId, stallIndex);
		try {
			mMessageQueue.put(packet);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while (!isInterrupted()) {
				String message = (String) mMessageQueue.take();
				if( message != null ) {
					mOut.write(message, 0, message.length());
					mOut.flush();					
				}
			}
		
			
		} catch (InterruptedException ioe) {
			try {
				mInfo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			System.out.println("FacilityPacketWriter thread stopped");
		}
								
	}

}
