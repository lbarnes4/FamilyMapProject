package response;

import model.Event;

/**
 * The response returned when an EventService object calls the event method.
 */
public class EventResponse extends Response {
    /**
     * The username of the user that is associated with the event.
     */
    private String associatedUsername;

    /**
     * The identifier of the event
     */
    private String eventID;

    /**
     * The identifier of the person that the event belongs to.
     */
    private String personID;

    /**
     * The latitude where the event occurred.
     */
    private double latitude;

    /**
     * The longitude where the event occurred.
     */
    private double longitude;

    /**
     * The country where the event occurred.
     */
    private String country;

    /**
     * The city where the event occurred.
     */
    private String city;

    /**
     * The type of the event (birth, death, marriage, etc).
     */
    private String eventType;

    /**
     * The year the event occurred.
     */
    private Integer year;

    public EventResponse(Event event) {
        setAssociatedUsername(event.getAssociatedUsername());
        setEventID(event.getEventID());
        setPersonID(event.getPersonID());
        setLatitude(event.getLatitude());
        setLongitude(event.getLongitude());
        setCountry(event.getCountry());
        setCity(event.getCity());
        setEventType(event.getEventType());
        setYear(event.getYear());
    }

    public EventResponse() {

    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
