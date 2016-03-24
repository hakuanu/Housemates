package stevenyoon.housemates;
import java.util.Date;
import java.sql.Time;
/**
 * Created by Mikael on 3/18/16.
 */
public class Event {
    private String date;
    private String startT;
    private String endT;
    private String eventName;
    private String clubName;
    private String details;

    public Event (String d, String s, String e, String eventName, String clubName, String details) {
        this.date = d;
        this.startT = s;
        this.endT = e;
        this.eventName = eventName;
        this.clubName = clubName;
        this.details = details;
    }

    public String getEventName() { return eventName;}
    public String getdate() { return date;}
    public String getStartTime() {return startT;}
    public String getEndTime() {return endT;}
    public String getClubName() {return clubName;}
    public String getDetails() {return details;}

    public void updateEventName(String e) {this.eventName = e;}
    public void updateDate(String d) {this.date = d;}
    public void updateStartTime(String s) {this.startT = s;}
    public void updateEndTime(String e) {this.endT = e;}
    public void updateClubName(String s) {this.clubName = s;}
    public void updateDetails(String s) {this.details = s;}


}
