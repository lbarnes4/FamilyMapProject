package service;

import dao.*;
import handler.AuthorizationException;
import model.AuthToken;
import model.Event;
import response.EventResponse;
import java.sql.Connection;

/**
 * An object used to get an event from the database.
 */
public class EventService {

    /**
     * Get an event from the database.
     * @param auth - an authorization string that the server uses to authorize the request.
     * @param eventID - the identifier of the event to be returned
     * @return a response including an event and/or a status of the request.
     */
    public EventResponse event(String auth, String eventID) throws DataAccessException {

        EventResponse eventResponse = new EventResponse();
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken authToken = authTokenDao.getAuthToken(auth);
            if (authToken != null) {
                EventDao eventDao = new EventDao(conn);
                Event event = eventDao.getEvent(eventID);
                if (event != null) {
                    eventResponse = new EventResponse(event);
                }
                else {
                    throw new DataAccessException("Error: Event does not exist");
                }
                if (!eventResponse.getAssociatedUsername().equalsIgnoreCase(authToken.getUserName())) {
                    eventResponse = new EventResponse();
                    throw new AuthorizationException("Error: unauthorized request");
                }
                eventResponse.setSuccess(true);
                db.closeConnection(true);
            }
            else {
                throw new AuthorizationException("Error: unauthorized request");
            }

        } catch (DataAccessException | AuthorizationException e) {
            eventResponse.setMessage(e.getMessage());
            eventResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return eventResponse;
    }
}
