package service;

import dao.*;
import model.*;
import response.FillResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * An object used to populate the server's database with generated data for the specified user name.
 */
public class FillService extends AncestorGenerator {
    public FillService() throws IOException {
    }

    /**
     * Populate the server's database with generated data for the specified user name.
     *
     * @return a response regarding the status of the request.
     */
    public FillResponse fill(String username, int generations) throws DataAccessException {

        FillResponse fillResponse = new FillResponse();
        //lets create a new database
        db = new Database();
        try {
            conn = db.openConnection();
            eventDao = new EventDao(conn);
            personDao = new PersonDao(conn);
            UserDao userDao = new UserDao(conn);

            if (generations < 0) {
                throw new InvalidParameterException();
            }

            user = userDao.getUser(username);
            if (user == null) {
                throw new DataAccessException("Error: User does not exist");
            }
            ArrayList<Person> persons = personDao.getPersonsOfUser(username);
            Person userPerson = new Person();
            int childBirthYear = 0;
            for (Person person : persons) {
                if (!person.getPersonID().equals(user.getPersonID())) {
                    eventDao.deleteEventsOfPerson(person.getPersonID());
                }
                else {
                    userPerson = person;

                    ArrayList<Event> events = eventDao.getEventsOfPerson(userPerson.getPersonID());
                    for (Event event : events) {
                        if (event.getEventType().equalsIgnoreCase("birth")) {
                            childBirthYear = event.getYear();
                        }
                    }
                }
            }
            if (childBirthYear == 0) {
                throw new DataAccessException();
            }
            if (persons.size() > 1) {
                personDao.deletePersonsOfUser(username);
                personDao.insert(userPerson);
            }

            generateAncestors(userPerson,childBirthYear,generations);

            db.closeConnection(true);
            fillResponse = new FillResponse("Successfully added " + (ancestorCount + 1) + " persons and " +
                    (ancestorCount * 3 + 1) + " events to the database.",true);
        }
        catch (DataAccessException | IOException e) {
            fillResponse.setMessage(e.getMessage());
            fillResponse.setSuccess(false);
            db.closeConnection(false);
        }
        return fillResponse;
    }
}
