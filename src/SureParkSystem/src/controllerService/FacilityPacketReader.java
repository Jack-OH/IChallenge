package controllerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FacilityPacketReader extends Thread {
	
	private BufferedReader mIn = null;
	private int ArduinoId = 0;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	volatile private ScheduledFuture<?> beeperHandle;
	
	
	public FacilityPacketReader(BufferedReader in, int ArduinoId) {
		mIn = in;
		this.ArduinoId = ArduinoId;
	}
	
	public void parse(String packet) {
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
			
			Config c = Config.getInstance();
			GarageInfo info = c.getGarageInfo(id);
			
			System.out.println("slotSize=" + info.slotStatus.size());
			
			// save into the stall list
//			for( int i = 0 ; i < slotStatus.length() ; i++ ) {
//				info.slotStatus.set(i, Integer.valueOf(slotStatus.charAt(i))-'0' );
//				
//				System.out.println(info.slotStatus.get(i));
//			}

			
		} else {
			// Invalid Packet
			System.out.println("invalid packet. through it ");
			return;
		}	
		
	}
	
	// 1분 뒤에 실행될 내용..
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
					
					if( beeperHandle != null ) {
						beeperHandle.cancel(true);
					}
					
					beeperHandle = scheduler.schedule(beeper, 10, TimeUnit.SECONDS);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken");
			
			// 서버 커넥션 끊어질때 호출됨. 
			ioe.printStackTrace();
		}
		
	}

}
