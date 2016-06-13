package SureParkManager.controlService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import SureParkManager.common.GarageInfo;
import SureParkManager.common.SureParkConfig;

public class FacilityPacketReader extends Thread {
	
	private BufferedReader mIn = null;
	private int facilityId = 0;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	volatile private ScheduledFuture<?> noPacketHandle;
	
	
	public FacilityPacketReader(BufferedReader in, int id) {
		mIn = in;
		facilityId = id;
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
			
			boolean needtoUpdate = false;
			for( int i = 0 ; i < info.slotNum ; i++ ) {
				int status = Integer.valueOf(slotStatus.charAt(i))-'0';
				if( status != info.slotStatus.get(i) ) {
					needtoUpdate = true;
					break;
				}
			}
			
			if( needtoUpdate ) {
				ArrayList<Integer> list = new ArrayList<Integer>();
				
				for( int i = 0 ; i < info.slotNum ; i++ ) {
					list.add(Integer.valueOf(slotStatus.charAt(i))-'0' );
				}
				
				c.setGarageSlotState(facilityId, list);
				System.err.println("Update Garage Status!!");
			}
			
			
		} else {
			// Invalid Packet
			System.out.println("invalid packet. through it ");
		}	
		
	}
	
	// 1�� �ڿ� ����� ����..
	final Runnable beeper = new Runnable() {
	       public void run() { System.out.println("beep"); }
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
					
					noPacketHandle = scheduler.schedule(beeper, 10, TimeUnit.SECONDS);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken");
			
			// ���� Ŀ�ؼ� �������� ȣ���. 
			ioe.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
