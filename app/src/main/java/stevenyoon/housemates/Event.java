package stevenyoon.housemates;
import java.util.Date;
import java.sql.Time;
/**
 * Created by Mikael on 3/18/16.
 */
public class Event {
    Date d;
    Time startT;
    Time endT;
    String eventName;
    String clubName;
    String details;

    public Event (Date d, Time s, Time e, String eventName, String clubName, String details) {
        this. d = d;
        this.startT = s;
        this.endT = e;
        this.eventName = eventName;
        this.clubName = clubName;
        this.details = details;
    }



}
