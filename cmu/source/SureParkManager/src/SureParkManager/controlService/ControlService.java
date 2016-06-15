package sureParkManager.controlService;
import sureParkManager.managementService.AbstractManagementFacility;
import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;



public class ControlService {

	private static final int kFacilitySlotNumberBase = 0;

	private ArrayList<FacilityClientInfo> mClientInfo = null;
	private AbstractManagementFacility mgrFacility = null;
	
	public ControlService(AbstractManagementFacility mgrFacility) throws Exception {

		 SureParkConfig config = SureParkConfig.getInstance();
         this.mgrFacility = mgrFacility;
        
		 for( int i = 0 ; i < config.getGarageNum() ; i++ ) {
			 FacilityClientInfo info = new FacilityClientInfo();
			 mClientInfo.add(new FacilityClientInfo());
			 
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
    	
		info.facilityId = facilityId;
    	info.mOut = new BufferedWriter(new OutputStreamWriter(info.mClientSocket.getOutputStream()));
		info.mIn = new BufferedReader(new InputStreamReader(info.mClientSocket.getInputStream()));
		
		info.mfWriter = new FacilityPacketWriter(info.mOut, facilityId);
		info.mfWriter.setDaemon(true);
		info.mfWriter.start();
		
		info.mfReader = new FacilityPacketReader(info.mIn, facilityId);
        info.mfReader.setManager(mgrFacility);
		info.mfReader.setDaemon(true);
		info.mfReader.start();
		
		info.mfWriter.sendInformation();	
		
		
	}
	
	public void openEntryGate(int facilityId, int slotIndex ) {
		for( FacilityClientInfo i : mClientInfo ) {
			if( i.facilityId == facilityId ) {
				i.mfWriter.request2openEntryGate();
				i.mfWriter.request2turnOnStallLED(slotIndex);
				break;
			}
		}
	}
	
	
	public void close() throws IOException {
		for( FacilityClientInfo i : mClientInfo ) {
			i.mOut.close();
			i.mIn.close();
			i.mClientSocket.close();			
		}

	}

}
