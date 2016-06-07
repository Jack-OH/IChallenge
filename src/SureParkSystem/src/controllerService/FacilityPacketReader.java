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
			System.out.println("invalid packet. through it $");
			return;
		}
		
		int length = packet.length();
		String strGarageId = packet.substring(1, 5);
		int id = Integer.valueOf(strGarageId);
		String strCode = packet.substring(5, 6);
		
		System.out.println("code=" + strCode + ", GarageId=" + id );
		
		//if( strCode == "S") {
			// Slot Status
			String slotStatus = packet.substring(6, length);
			System.out.println(slotStatus);
			
			Config c = Config.getInstance();
			GarageInfo info = c.getGarageInfo(id);
			
			System.out.println("slotSize=" + info.slotStatus.size());
			
			for( int i = 0 ; i < slotStatus.length() ; i++ ) {
				info.slotStatus.set(i, Integer.valueOf(slotStatus.charAt(i))-'0' );
				
				System.out.println(info.slotStatus.get(i));
			}
			
			
//		} else {
//			// Invalid Packet
//			System.out.println("invalid packet. through it ");
//			return;
//		}	
		
	}
	
	public void run() {
		// display read message from server
		try {
			String message;
			while ((message = mIn.readLine()) != null) {
				System.out.println(message + ", length=" + message.length());
				
				if( message.length() > 0 ) {
					parse(message);
				}
			}
		} catch (IOException ioe) {
			System.err.println("Connection to server broken");
			ioe.printStackTrace();
		}
		
	}

}
