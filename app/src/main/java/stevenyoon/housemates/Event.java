package stevenyoon.housemates;
/**
 * Created by Mikael on 3/18/16.
 */
/*
* Event class to store an event's date, start time, end time, event name, club name, details, and
 * id.
 * Outside of the constructor method it has methods for getting and updating the variables listed
  * above.
* */
public class Event {
    private String date;
    private String startT;
    private String endT;
    private String eventName;
    private String clubName;
    private String details;
    private String id;
    public static int max_event_id = 0;

    public Event (String d, String s, String e, String eventName, String clubName, String details, String id) {
        this.date = d;
        this.startT = s;
        this.endT = e;
        this.eventName = eventName;
        this.clubName = clubName;
        this.details = details;
        this.id = id;

    }
    /*
    * Methods for getting and updating member variables
    *
    *  */
    public String getEventName() { return eventName;}
    public String getdate() { return date;}
    public String getStartTime() {return startT;}
    public String getEndTime() {return endT;}
    public String getClubName() {return clubName;}
    public String getDetails() {return details;}
    public String getId() {return id;}

    public void updateEventName(String e) {this.eventName = e;}
    public void updateDate(String d) {this.date = d;}
    public void updateStartTime(String s) {this.startT = s;}
    public void updateEndTime(String e) {this.endT = e;}
    public void updateClubName(String s) {this.clubName = s;}
    public void updateDetails(String s) {this.details = s;}


}
