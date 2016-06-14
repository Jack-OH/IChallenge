package SureParkManager.controlService;
import SureParkManager.common.GarageInfo;
import SureParkManager.common.SureParkConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;



public class ControlService {


	ArrayList<FacilityClientInfo> mClientInfo;
	
	public ControlService() throws Exception {
		
		 SureParkConfig config = SureParkConfig.getInstance();
		 
		 for( int i = 0 ; i < config.getGarageNum() ; i++ ) {
			 FacilityClientInfo info = new FacilityClientInfo();
			 GarageInfo gInfo = config.getGarageInfoFromIndex(i);
			 createClient(info, gInfo.ip, 5001, gInfo.id); 
		 }
		 
		
	}
	
	public void createClient(FacilityClientInfo info, String host, int port, int facilityId) throws Exception {
		try {
			System.out.println("IP : " + host);
			info.mClientSocket = new Socket(host, port);
			System.out.println("connected to serer");
		} catch (IOException ioe) {
			System.err.println("cannot establish connection");
			ioe.printStackTrace();
		}
    	
    	info.mOut = new BufferedWriter(new OutputStreamWriter(info.mClientSocket.getOutputStream()));
		info.mIn = new BufferedReader(new InputStreamReader(info.mClientSocket.getInputStream()));
		
		info.mfWriter = new FacilityPacketWriter(info.mOut, facilityId);
		info.mfWriter.setDaemon(true);
		info.mfWriter.start();
		
		info.mfReader = new FacilityPacketReader(info.mIn, facilityId);
		info.mfReader.setDaemon(true);
		info.mfReader.start();
		
		info.mfWriter.sendInformation();		
	}
	
	public void openEntryGate() {
		//mfWriter.request2openEntryGate();
	}
	
	public void turnonStallLED(int stallIndex) {
		//info.mfWriter.request2turnOnStallLED(stallIndex);
	}
	
	public void close() throws IOException {
		//info.mOut.close();
		//mIn.close();
		
		//mClientSocket.close();		
	}	
	

}
