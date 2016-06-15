package sureParkManager.controlService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class FacilityClientInfo {
	BufferedWriter mOut = null;
	BufferedReader mIn = null;
	Socket mClientSocket = null;
	
	FacilityPacketWriter mfWriter = null;
	FacilityPacketReader mfReader = null;
	
	int facilityId = 0;
	
}
