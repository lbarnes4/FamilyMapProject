package model;

import response.EventResponse;

/**
 * An important event of a Person's life (birth, marriage, death, etc).
 *
 */
public class Event {


    /**
     * The unique identifier of the event.
     */
    private String eventID;

    /**
     * The username of the User to which the event belongs.
     */
    private String associatedUsername;

    /**
     * The Person of the Person to which the event belongs.
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
     * The country where the event occurred.
     */
    private String city;

    /**
     * The type of event (birth, death, marriage, etc).
     */
    private String eventType;

    /**
     * The year the event occurred.
     */
    private int year;

    public Event(String eventID, String associatedUsername, String personID, double latitude, double longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public Event(EventResponse response) {
        this.eventID = response.getEventID();
        this.associatedUsername = response.getAssociatedUsername();
        this.personID = response.getPersonID();
        this.latitude = response.getLatitude();
        this.longitude = response.getLongitude();
        this.country = response.getCountry();
        this.city = response.getCity();
        this.eventType = response.getEventType();
        this.year = response.getYear();
    }

    public Event() {

    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Event) {
            Event oEvent = (Event) o;
            return oEvent.getEventID().equals(getEventID()) &&
                    oEvent.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oEvent.getPersonID().equals(getPersonID()) &&
                    (Math.abs(oEvent.getLatitude() - getLatitude()) <.01) &&
                    (Math.abs(oEvent.getLongitude() - getLongitude()) <.01) &&
                    oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getEventType().equals(getEventType()) &&
                    oEvent.getYear() == (getYear());
        } else {
            return false;
        }
    }
}
