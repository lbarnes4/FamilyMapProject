package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import handler.AuthorizationException;
import model.AuthToken;
import model.Person;
import response.PersonResponse;
import java.sql.Connection;

/**
 * An object used to get a single person from the database.
 */
public class PersonService {
    /**
     * Get a single person from the database.
     * @param auth - an authorization string that the server uses to authorize the request.
     * @param personID - the identifier of the person to be returned.
     * @return a response including the person and/or a status of the request.
     */
    public PersonResponse person(String auth, String personID) throws DataAccessException {

        PersonResponse personResponse = new PersonResponse();
        Database db = new Database();
        try {
            Connection conn = db.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken authToken = authTokenDao.getAuthToken(auth);
            if (authToken != null) {
                PersonDao personDao = new PersonDao(conn);
                Person person = personDao.getPerson(personID);
                if (person != null) {
                    personResponse = new PersonResponse(person);
                }
                else {
                    throw new DataAccessException("Error: Person does not exist");
                }
                if (!personResponse.getAssociatedUsername().equalsIgnoreCase(authToken.getUserName())) {
                    personResponse = new PersonResponse();
                    throw new AuthorizationException("Error: unauthorized request");
                }
                personResponse.setSuccess(true);
                db.closeConnection(true);
            }
            else {
                throw new AuthorizationException("Error: unauthorized request");
            }

        } catch (DataAccessException | AuthorizationException e) {
            personResponse.setMessage(e.getMessage());
            personResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return personResponse;
    }
}
