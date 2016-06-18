package sureParkManager.noshowService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import sureParkManager.common.ReservationInfo;
import sureParkManager.managementService.ManagementDBTransaction;

public class NoShowManager extends Thread {
	
	public static final int kNectCheckTime = 10*1000; //10sec
	private static Calendar now;
	
	public NoShowManager() {
		now.setTimeInMillis(System.currentTimeMillis());
	}
	
	public void run() {
		

		while(!isInterrupted()) {
			try {
				ManagementDBTransaction mgrDB = ManagementDBTransaction.getInstance();
				
				ArrayList<ReservationInfo> info = mgrDB.getReservationInfo();
				
				for( ReservationInfo r : info ) {
					Date current = (Date) now.getTime();
					
					if( r.reservationTime.compareTo(current) > 0 ) {
						// TODO:: remove db
					}
				}

				Thread.sleep(kNectCheckTime);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		}
		
	}

}
