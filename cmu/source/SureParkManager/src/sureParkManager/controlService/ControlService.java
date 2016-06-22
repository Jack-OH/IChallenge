package sureParkManager.controlService;

import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.managementService.IManagementFacility;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ControlService extends Thread implements IControlService {
	public static final int kConnectionRetryCount = 2;
	
	private ArrayList<FacilityClientInfo> mClientInfo = new ArrayList<FacilityClientInfo>();
	private IManagementFacility mgrFacility = null;
	private boolean isNewGarageAdded = false;
	private int newFacilityId = 0;
	
	public ControlService(IManagementFacility mgrFacility) throws Exception {

		 SureParkConfig config = SureParkConfig.getInstance();
         this.mgrFacility = mgrFacility;
        
		 for( int i = 0 ; i < config.getGarageNum() ; i++ ) {
			 FacilityClientInfo info = new FacilityClientInfo();
			 GarageInfo gInfo = config.getGarageInfoFromIndex(i);
			 info.ip = gInfo.ip;
			 info.facilityId = gInfo.id;
			 mClientInfo.add(info);
		 }
		 
		 start();
		 
	}

	public boolean createClient(FacilityClientInfo info, String host, int port, int facilityId) throws Exception {
		try {
			System.out.println("Try to connect IP : " + host);
			info.mClientSocket = new Socket(host, port);
			System.out.println("connected to serer");
			
			info.mfWriter = new FacilityPacketWriter(info);
			info.mfWriter.start();
			
			info.mfReader = new FacilityPacketReader(info);
			info.mfReader.setManager(mgrFacility);
			info.mfReader.start();
			
			info.mfWriter.sendInformation();
			
			return true;
			
		} catch (IOException ioe) {
			System.err.println("cannot establish connection");
			//ioe.printStackTrace();
			return false;
		}
	}
	
	public void addFacility(int facilityId) {

		newFacilityId = facilityId;
		System.out.println("add facility id=" + facilityId);
		isNewGarageAdded = true;
	}
	
	public void openEntryGate(int facilityId, int slotIndex ) {
		for( FacilityClientInfo i : mClientInfo ) {
			if( i.facilityId == facilityId ) {
				i.mfWriter.request2openEntryGate();
				i.mfWriter.request2turnOnStallLED(slotIndex);
				i.mfReader.setParkingSlotIndex(slotIndex);
				break;
			}
		}
	}
	
	
	public void close() throws IOException {
		for( FacilityClientInfo i : mClientInfo ) {			
			i.mClientSocket.close();			
		}

	}
	
	public void run() {
		try{
			while(!isInterrupted() ) {
				
				if( isNewGarageAdded ) {
					 SureParkConfig config = SureParkConfig.getInstance();				        
					 FacilityClientInfo info = new FacilityClientInfo();
					 GarageInfo gInfo = config.getGarageInfoFromGarageID(newFacilityId);
					 info.ip = gInfo.ip;
					 info.facilityId = gInfo.id;
					 mClientInfo.add(info);
					 isNewGarageAdded = false;
				}
			
				// connection
				for( FacilityClientInfo info : mClientInfo ) {
					if( info.needtocheck == false && info.mClientSocket == null ) {
						int retryCount = kConnectionRetryCount;
						boolean isConnected= false;
						while(retryCount > 0) {
							if( false == createClient(info, info.ip, 5001, info.facilityId) ) {
								retryCount--;
								Thread.sleep(3000); 
								continue;
							}
							isConnected = true;
							break;	
						}
						
						if( isConnected == false ) {
							info.setNeedtoCheck();
							mgrFacility.setFacilityFailure(info.facilityId, true);
						}

					}
				}
				
				Thread.sleep(10000); 
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("ControlService thread stopped!");
		}

	}

}
