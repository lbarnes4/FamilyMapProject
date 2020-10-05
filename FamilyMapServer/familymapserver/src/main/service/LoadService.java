package service;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import response.LoadResponse;
import java.sql.Connection;

/**
 * An object used to load the posted user, person, and event data into the database.
 */
public class LoadService {
    /**
     * Load the posted user, person, and event data into the database.
     * @param request - an object containing the user, person, and event data
     * @return a response regarding the status of the request.
     */
    public LoadResponse load(LoadRequest request) throws DataAccessException {

        LoadResponse loadResponse = new LoadResponse();
        Database db = new Database();
        try {
            
            Connection conn = db.openConnection();
            db.clearTables();
            UserDao userDao = new UserDao(conn);
            for (User user : request.users) {
                userDao.insert(user);
            }
            PersonDao personDao = new PersonDao(conn);
            for (Person person : request.persons) {
                personDao.insert(person);
            }
            EventDao eventDao = new EventDao(conn);
            for (Event event : request.events) {
                eventDao.insert(event);
            }
            loadResponse = new LoadResponse("Successfully added " + request.users.size() + " users, " +
                    request.persons.size() + " persons, and " + request.events.size() + " events.", true);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            loadResponse.setMessage(e.getMessage());
            loadResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return loadResponse;

    }
}
