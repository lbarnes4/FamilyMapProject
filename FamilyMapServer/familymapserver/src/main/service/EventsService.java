package service;

import dao.*;
import handler.AuthorizationException;
import model.AuthToken;
import response.EventsResponse;
import java.sql.Connection;

/**
 * An object used to get all events of a user.
 */
public class EventsService {
    /**
     * Get all events associated with a user.
     * @param auth - an authorization string that identifies the user and authorizes the request.
     * @return a response including a list of events and/or a status of the request.
     */
    public EventsResponse events(String auth) throws DataAccessException {

        EventsResponse eventsResponse = new EventsResponse();
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken authToken = authTokenDao.getAuthToken(auth);
            if (authToken != null) {
                EventDao eventDao = new EventDao(conn);
                eventsResponse.data = eventDao.getEventsOfUser(authToken.getUserName());
                eventsResponse.setSuccess(true);
                db.closeConnection(true);
            }
            else {
                throw new AuthorizationException("Error: unauthorized request");
            }

        } catch (DataAccessException | AuthorizationException e) {
            eventsResponse.setMessage(e.getMessage());
            eventsResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return eventsResponse;
    }
}
