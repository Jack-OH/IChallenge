package controllerService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ControllerServiceMain {

	public static void main(String[] args) throws Exception {
		
		Config c = Config.getInstance();
		
		// put garage information
		c.putGarageInfo(1, new GarageInfo(1, "SurePark", "192.168.1.3", 5001, 4));
		c.putGarageInfo(2, new GarageInfo(2, "SurePark2", "localhost", 5001, 4));
		
		System.out.println("GarageNum=" + c.getGarageNum());
		
		GarageInfo info = c.getGarageInfo(1);		
		System.out.println("ip=" + info.ip + ", port=" + info.port);
		// create ControllerService
		ControllerService cs = new ControllerService(info.ip, info.port, 1);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			
			String message = in.readLine();
			
			if( message.equals("GATE") ) {
				cs.openEntryGate();
			} else if(message.equals("LED")) {
				cs.turnonStallLED(1);
			} else if(message.equals("EXIT")) {
				break;
			}	
			
			
			Thread.sleep(3000);
			
			
		}
		
		cs.close();
		
	}

}
