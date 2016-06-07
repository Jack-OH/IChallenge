package controllerService;

public class ControllerServiceMain {

	public static void main(String[] args) throws Exception {
		
		// create ControllerService
		//ControllerService cs = new ControllerService("localhost", 5001);
		ControllerService cs = new ControllerService("192.168.1.3", 5001);
		
		while(true) {
			
			Thread.sleep(3000);
			
			
		}
		
		//cs.close();
		
	}

}
