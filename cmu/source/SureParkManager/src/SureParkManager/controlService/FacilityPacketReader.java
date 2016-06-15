package sureParkManager.controlService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.managementService.AbstractManagementFacility;

public class FacilityPacketReader extends Thread {
	
	private BufferedReader mIn = null;
	private int facilityId = 0;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	volatile private ScheduledFuture<?> noPacketHandle;
	private AbstractManagementFacility mgrFacility = null;
	private boolean isFirstStatus = true;
	
	
	public FacilityPacketReader(BufferedReader in, int id) {
		mIn = in;
		facilityId = id;
	}
	
	public void setManager(AbstractManagementFacility mgr) {
		mgrFacility = mgr;
	}
	
	public void parse(String packet) throws Exception {
		if( packet.charAt(0) != '$' ) {
			System.out.println("invalid packet. through it $");
			return;
		}
		
		int length = packet.length();
		String strGarageId = packet.substring(1, 5);
		int id = Integer.valueOf(strGarageId);
		String strCode = packet.substring(5, 6);
		
		System.out.println("code=" + strCode + ", GarageId=" + id );
		
		if( strCode.equals("S") ) {
			// Slot Status
			String slotStatus = packet.substring(6, length);
			System.out.println(slotStatus);
			
			SureParkConfig c = SureParkConfig.getInstance();
			//SureParkConfig info = c.getGarageInfo(id);
			GarageInfo info = c.getGarageInfoFromGarageID(facilityId);
			
			for( int i = 0 ; i < info.slotNum ; i++ ) {
				int status = Integer.valueOf(slotStatus.charAt(i))-'0';
				if( isFirstStatus || status != info.slotStatus.get(i) ) {
					//evtMgr.updateSlotStatus(facilityId, i, status);
					mgrFacility.updateSlotStatus(facilityId, i, status);
				}
			}
			
			isFirstStatus = false;
			

			
			
		} else {
			// Invalid Packet
			System.out.println("invalid packet. through it ");
		}	
		
	}
	
	// 1ºÐ µÚ¿¡ ½ÇÇàµÉ ³»¿ë..
	final Runnable handler = new Runnable() {
	       public void run() { 
	    	   System.out.println("no heartbeat from facility"); 
	    	   //evtMgr.setFacilityFailure(facilityId, true);
	    	   try {
				mgrFacility.setFacilityFailure(facilityId, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }
	     };
	
	public void run() {
		// display read message from server
		try {
			String message;
			while ((message = mIn.readLine()) != null) {
				System.out.println(message + ", length=" + message.length());
				
				if( message.length() > 0 ) {
					parse(message);
					
					if( noPacketHandle != null ) {
						noPacketHandle.cancel(true);
					}
					
					noPacketHandle = scheduler.schedule(handler, 10, TimeUnit.SECONDS);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken");
			
			// ¼­¹ö Ä¿³Ø¼Ç ²÷¾îÁú¶§ È£ÃâµÊ. 
			ioe.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
