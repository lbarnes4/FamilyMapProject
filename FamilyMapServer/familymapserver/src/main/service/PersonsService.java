package service;

import dao.*;
import handler.AuthorizationException;
import model.AuthToken;
import response.PersonsResponse;
import java.sql.Connection;


/**
 * An object used to get all persons of the current user from the database.
 */
public class PersonsService {
    /**
     * Get all persons of the current user from the database.
     * @param auth - an authorization string that identifies the user and authorizes the request.
     * @return a response including an array of person objects and/or a status of the request.
     */
    public PersonsResponse persons(String auth) throws DataAccessException {
        PersonsResponse personsResponse = new PersonsResponse();
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken authToken = authTokenDao.getAuthToken(auth);
            if (authToken != null) {
                PersonDao personDao = new PersonDao(conn);
                personsResponse.data = personDao.getPersonsOfUser(authToken.getUserName());
                personsResponse.setSuccess(true);
                db.closeConnection(true);
            }
            else {
                throw new AuthorizationException("Error: unauthorized request");
            }

        } catch (DataAccessException | AuthorizationException e) {
            personsResponse.setMessage(e.getMessage());
            personsResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return personsResponse;
    }
}
