package request;

import model.Event;
import model.Person;
import model.User;
import java.util.List;

/**
 * An object containing information that is passed to the load method in the LoadService class
 */
public class LoadRequest {
    /**
     * A list of users to load.
     */
    public List<User> users;

    /**
     * A list of persons to load.
     */
    public List<Person> persons;

    /**
     * A list of events to load.
     */
    public List<Event> events;
}
