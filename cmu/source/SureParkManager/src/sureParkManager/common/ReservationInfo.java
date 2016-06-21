package sureParkManager.common;

import java.util.Date;

/**
 * Created by jaeheonkim on 2016. 6. 18..
 */
public class ReservationInfo {

    public Date     reservationTime;
    public int      gracePeriod;        // Unit : minute
    public String   confirmInformation;

    public ReservationInfo (Date reservationTime, int gracePeriod, String confirmInformation) {
        this.reservationTime = reservationTime;
        this.gracePeriod = gracePeriod;
        this.confirmInformation = confirmInformation;
    }
}
