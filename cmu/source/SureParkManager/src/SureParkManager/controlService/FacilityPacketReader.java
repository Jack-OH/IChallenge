package sureParkManager.controlService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sureParkManager.common.GarageInfo;
import sureParkManager.common.ReservationInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.managementService.IManagementFacility;
import sureParkManager.managementService.ManagementDBTransaction;

public class FacilityPacketReader extends Thread {
	
	private BufferedReader mIn = null;
	private FacilityClientInfo mInfo = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	volatile private ScheduledFuture<?> noPacketHandle;
	private IManagementFacility mgrFacility = null;
	private boolean isFirstStatus = true;
	private boolean isParkingCar = false;
	private int parkingSlotIndex = -1;
	private int changedSlotIndex = -1;
	
	
	public FacilityPacketReader(FacilityClientInfo info) throws IOException {
		mInfo = info;
		mIn = new BufferedReader(new InputStreamReader(mInfo.mClientSocket.getInputStream()));
	}
	
	public void setManager(IManagementFacility mgr) {
		mgrFacility = mgr;
	}
	
	public void setParkingSlotIndex( int slot ) {
		parkingSlotIndex = slot;
		if( parkingSlotIndex >= 0 )
			isParkingCar = true;
	}
	
	
	public void parse(String packet) throws Exception {
		if( packet.charAt(0) != '$' ) {
			System.out.println("No $, invalid packet");
			return;
		}
		
		int length = packet.length();		
		String strGarageId = packet.substring(1, 5);
		int id = Integer.valueOf(strGarageId);
		
		if( length > 5 ) {
			String strCode = packet.substring(5, 6);
			
			System.out.println("code=" + strCode + ", GarageId=" + id );
			
			if( strCode.equals("S") ) {
				// Slot Status
				String slotStatus = packet.substring(6, length);
				System.out.println(slotStatus);
				
				SureParkConfig c = SureParkConfig.getInstance();
				GarageInfo info = c.getGarageInfoFromGarageID(mInfo.facilityId);
				
				for( int i = 0 ; i < info.slotNum ; i++ ) {
					int status = Integer.valueOf(slotStatus.charAt(i))-'0';
					
					if( isFirstStatus ) {
						// Initial
						mgrFacility.updateSlotStatus(mInfo.facilityId, i, status);
					} else if ( isParkingCar && status != info.slotStatus.get(i) ) {
						
						// normal parking a car!! 
						if( i == parkingSlotIndex ) {
							mgrFacility.updateSlotStatus(mInfo.facilityId, i, status);
							System.out.println("--------> normal parking slotid=" + i);
						} else {
						// wrong parking a car!!
							mgrFacility.updateWrongParking(mInfo.facilityId, i);
							System.out.println("--------> wrong parking slotid=" + i);
						}
						changedSlotIndex = i;
					} else if ( status != info.slotStatus.get(i) ) {
						mgrFacility.updateSlotStatus(mInfo.facilityId, i, status);
						changedSlotIndex = i;
					}
				}
				
				isFirstStatus = false;
				isParkingCar = false;
				parkingSlotIndex = -1;
				
			} else if( strCode.equals("X") ) {
				
				System.out.println("eXit Car");
				
				if( isParkingCar ) {
					System.out.println("----------> bypass slotid=" + parkingSlotIndex);
					mgrFacility.leaveWithoutParking(mInfo.facilityId, parkingSlotIndex);
					isParkingCar = false;
					parkingSlotIndex = -1;					
				} else if ( changedSlotIndex >= 0 ) {
					mgrFacility.leaveWithParking(mInfo.facilityId, changedSlotIndex);
					System.out.println("------------> leave garage slotid=" + changedSlotIndex);
					changedSlotIndex = -1;
				} 				
				
				

			} else {
				// Invalid Packet
				System.out.println("invalid packet. no code ");
			}
		} else {
			//System.out.println("Heartbeat packet");
		}

		
	}
	
	// 1ºÐ µÚ¿¡ ½ÇÇàµÉ ³»¿ë..
	final Runnable handler = new Runnable() {
	       public void run() { 
	    	   System.out.println("no heartbeat from facility"); 
	    	   try {
	    		   if( mgrFacility != null ) {
	    			   // if no heartbeat packet, we try to reconnection....
	    			   mInfo.close();
	    			   //mInfo.setNeedtoCheck();
	    			   //mgrFacility.setFacilityFailure(mInfo.facilityId, true);
	    		   }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }
	     };
	
	public void run() {
		// display read message from server
		try {
			
			while ( !isInterrupted() ) {
				String message = mIn.readLine();
				if( message == null ) break;
				
				//System.out.println(message + ", length=" + message.length());
				
				if( message.length() > 0 ) {
					parse(message);
					
					if( noPacketHandle != null ) {
						noPacketHandle.cancel(true);
					}
					
					noPacketHandle = scheduler.schedule(handler, 20, TimeUnit.SECONDS);
				}
			}
			
			
		} catch (IOException ioe) {
			System.err.println("Connection to server broken, facilityId=" + mInfo.facilityId);
	
			try {
				mInfo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// ¼­¹ö Ä¿³Ø¼Ç ²÷¾îÁú¶§ È£ÃâµÊ. 
			//ioe.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if( noPacketHandle != null ) {
				noPacketHandle.cancel(true);
			}
			System.out.println("FacilityPacketReader thread stopped");
		}
		
	}

}
