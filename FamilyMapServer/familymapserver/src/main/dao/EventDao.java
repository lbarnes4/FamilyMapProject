package dao;

import model.Event;

import java.sql.*;
import java.util.ArrayList;

/**
 * Provides an interface to the Event database table.
 */
public class EventDao {

    private final Connection conn;

    public EventDao(Connection conn)
    {
        this.conn = conn;
    }
    /**
     * Insert an Event object into the database.
     * @param event - the Event to be inserted.
     */
    public void insert(Event event) throws DataAccessException, IllegalArgumentException {
        if (event == null) {
            throw new IllegalArgumentException();
        }
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, UserName, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setDouble(4, event.getLatitude());
            stmt.setDouble(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting Event into the database");
        }
    }

    /**
     * Delete all Events from the database.
     */
    public void deleteAll() throws DataAccessException
    {

        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Events";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing Events");
        }
    }

    /**
     * Delete all Events belonging to one Person.
     * @param personID - the personID of the Person whose Events are to be deleted.
     */
    public void deleteEventsOfPerson(String personID) throws DataAccessException {
        String sql = "DELETE FROM Events WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting events of person");
        }
    }

    /**
     * Get an Event object from the database.
     * @param eventID - the eventID of the Event to be returned.
     * @return the Event object.
     */
    public Event getEvent(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("UserName"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Get all Events belonging to one Person.
     * @param personID - the personID of the Person whose Events are to be returned.
     * @return a set of Events.
     */
    public ArrayList<Event> getEventsOfPerson(String personID) throws DataAccessException {
        ArrayList<Event> events = new ArrayList<>();
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("UserName"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding persons");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Event> getEventsOfUser(String userName) throws DataAccessException {
        ArrayList<Event> events = new ArrayList<>();
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE UserName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("UserName"),
                        rs.getString("PersonID"), rs.getDouble("Latitude"), rs.getDouble("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding events");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
