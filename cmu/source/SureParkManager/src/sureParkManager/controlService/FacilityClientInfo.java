package sureParkManager.controlService;

import java.io.IOException;
import java.net.Socket;

public class FacilityClientInfo {
	
	boolean needtocheck = false;
	int facilityId = 0;
	String ip;
	Socket mClientSocket = null;	
	FacilityPacketWriter mfWriter = null;
	FacilityPacketReader mfReader = null;
	
	public void setNeedtoCheck() {
		needtocheck = true;
	}
	
	public void close() throws IOException {
		
		if( mClientSocket != null ) {
			mClientSocket.close();
			
			if( mfWriter != null )
				mfWriter.interrupt();
			if( mfReader != null )
				mfReader.interrupt();
			
			mClientSocket = null;
			mfWriter = null;
			mfReader = null;			
		}

	}
	
}
