package sureParkManager.managementService;

import java.util.ArrayList;
import java.util.Calendar;

import sureParkManager.common.ReservationInfo;
import sureParkManager.managementService.ManagementDBTransaction;

public class NoShowManager extends Thread {
	
	public static final int kNectCheckTime = 10*1000; //10sec
	private static Calendar now;
	
	public NoShowManager() {
		now = Calendar.getInstance();
	}
	
	public void run() {
		

		while(!isInterrupted()) {
			try {
				ManagementDBTransaction mgrDB = ManagementDBTransaction.getInstance();
				
				ArrayList<ReservationInfo> info = mgrDB.getReservationInfo();
				if( info != null ) {
					for( ReservationInfo r : info ) {						
						Calendar reserveTime = Calendar.getInstance(); 
						reserveTime.setTime(r.reservationTime);
						reserveTime.add(Calendar.MINUTE, r.gracePeriod);
						now.setTimeInMillis(System.currentTimeMillis());
						System.out.println("reserve=" + reserveTime.getTime() + ", now=" + now.getTime() + ", gracePeriod=" + r.gracePeriod);

						if( now.after(reserveTime) ) {
							mgrDB.cancelReservation(r.confirmInformation);
							
							System.out.println("Reservation canceled " + r.confirmInformation);
						}
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
